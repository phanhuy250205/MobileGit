package com.example.assignment_java5.service.impl;

import com.example.assignment_java5.model.chitietdonhang;
import com.example.assignment_java5.model.donhang;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.model.sanpham;
import com.example.assignment_java5.repository.chitietdonhangreponsitory;
import com.example.assignment_java5.repository.donhangrepository;
import com.example.assignment_java5.repository.nhanvienrepository;
import com.example.assignment_java5.repository.sanphamrepository;
import com.example.assignment_java5.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    private final donhangrepository donHangRepository;
    private final chitietdonhangreponsitory chiTietDonHangRepository;
    private final sanphamrepository sanPhamRepository;
    private final nhanvienrepository nhanVienRepository;

    private final Map<Long, Map<Long, Integer>> cartStorage = new HashMap<>();

    public CartServiceImpl(donhangrepository donHangRepository,
                           chitietdonhangreponsitory chiTietDonHangRepository,
                           sanphamrepository sanPhamRepository,
                           nhanvienrepository nhanVienRepository) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    public void addToCart(Long sanPhamId, Long nhanVienId) {
        nhanvien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên."));
        sanpham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm."));

        // Kiểm tra số lượng tồn kho
        if (sanPham.getSoLuong() < 1) {
            throw new IllegalStateException("Sản phẩm đã hết hàng.");
        }

        // Nếu nhân viên chưa có giỏ hàng, tạo giỏ mới
        cartStorage.putIfAbsent(nhanVienId, new HashMap<>());
        Map<Long, Integer> cart = cartStorage.get(nhanVienId);

        // Thêm sản phẩm vào giỏ hàng
        cart.put(sanPhamId, cart.getOrDefault(sanPhamId, 0) + 1);
    }

    public void removeFromCart(Long sanPhamId, Long nhanVienId) {
        if (cartStorage.containsKey(nhanVienId)) {
            Map<Long, Integer> cart = cartStorage.get(nhanVienId);
            cart.remove(sanPhamId);
        }
    }

    @Override
    public Map<Long, Integer> getCartItems(Long nhanVienId) {
        return cartStorage.getOrDefault(nhanVienId, new HashMap<>());
    }

    @Override
    public void clearCart(Long nhanVienId) {
        cartStorage.remove(nhanVienId);
    }

    @Transactional
    @Override
    public donhang checkout(Long nhanVienId) {
        nhanvien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên."));

        Map<Long, Integer> cartItems = getCartItems(nhanVienId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống.");
        }

        donhang donHang = new donhang();
        donHang.setNhanVien(nhanVien);
        donHang.setTongTien(BigDecimal.valueOf(0.0));
        donHangRepository.save(donHang);

        double total = 0.0;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();
            sanpham sanPham = sanPhamRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm."));

            if (sanPham.getSoLuong() < quantity) {
                throw new IllegalStateException("Không đủ hàng trong kho cho sản phẩm: " + sanPham.getTensanpham());
            }

            chitietdonhang chiTiet = new chitietdonhang();
            chiTiet.setDonHang(donHang);
            chiTiet.setSanPham(sanPham);
            chiTiet.setSoLuong(quantity);
            chiTiet.setGia(sanPham.getGia());
            chiTietDonHangRepository.save(chiTiet);

            // Cập nhật số lượng sản phẩm trong kho
            sanPham.setSoLuong(sanPham.getSoLuong() - quantity);
            sanPhamRepository.save(sanPham);
        }

        donHang.setTongTien(BigDecimal.valueOf(total));
        donHangRepository.save(donHang);

        // Xóa giỏ hàng sau khi thanh toán
        clearCart(nhanVienId);

        return donHang;
    }

    @Override
    public int getCartCount(Long nhanVienId) {
        return cartStorage.getOrDefault(nhanVienId, new HashMap<>()).values().stream().mapToInt(Integer::intValue).sum();
    }
    public BigDecimal calculateTotalPrice(Long nhanVienId) {
        Map<Long, Integer> cartItems = getCartItems(nhanVienId);
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();
            sanpham sanPham = sanPhamRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm."));

            BigDecimal productTotal = sanPham.getGia().multiply(BigDecimal.valueOf(quantity));
            total = total.add(productTotal);
        }

        return total;
    }


}
