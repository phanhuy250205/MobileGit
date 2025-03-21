package com.example.assignment_java5.controller;

import com.example.assignment_java5.model.chitietdonhang;
import com.example.assignment_java5.model.donhang;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.model.sanpham;
import com.example.assignment_java5.repository.chitietdonhangreponsitory;
import com.example.assignment_java5.repository.donhangrepository;
import com.example.assignment_java5.repository.nhanvienrepository;
import com.example.assignment_java5.repository.sanphamrepository;
import com.example.assignment_java5.service.CartService;
import com.example.assignment_java5.service.impl.CartServiceImpl;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final donhangrepository donHangRepository;
    private final chitietdonhangreponsitory chiTietDonHangRepository;
    private final sanphamrepository sanPhamRepository;
    private final nhanvienrepository nhanVienRepository;

    @Autowired
    private CartServiceImpl cartService;
    @PersistenceContext
    private EntityManager entityManager;

    public CartController(donhangrepository donHangRepository,
                          chitietdonhangreponsitory chiTietDonHangRepository,
                          sanphamrepository sanPhamRepository,
                          nhanvienrepository nhanVienRepository) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam Long sanPhamId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 📌 **Kiểm tra đăng nhập**: Đảm bảo người dùng đã đăng nhập trước khi thêm sản phẩm vào giỏ hàng
        nhanvien currentUser = (nhanvien) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("status", "error");
            response.put("message", "Người dùng chưa đăng nhập.");
            return ResponseEntity.badRequest().body(response);
        }

        // 📌 **Kiểm tra sản phẩm**: Xác nhận sản phẩm tồn tại trong hệ thống trước khi thêm vào giỏ
        Optional<sanpham> optionalSanPham = sanPhamRepository.findById(sanPhamId);
        if (optionalSanPham.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Sản phẩm không tồn tại.");
            return ResponseEntity.badRequest().body(response);
        }
        sanpham sanPham = optionalSanPham.get();
        if (sanPham.getSoLuong() <= 0) {
            response.put("status", "error");
            response.put("message", "Sản Phẩm Đã Hết Hàng");
            return ResponseEntity.badRequest().body(response);
        }

        // 📌 **Tạo hoặc lấy đơn hàng**: Tìm đơn hàng chưa thanh toán hoặc tạo mới nếu chưa có
        Optional<donhang> optionalDonHang = donHangRepository.findByNhanVienAndTrangThai(currentUser, "Chưa thanh toán");
        donhang donHang = optionalDonHang.orElseGet(() -> {
            donhang newDonHang = new donhang();
            newDonHang.setNhanVien(currentUser);
            newDonHang.setTrangThai("Chưa thanh toán");
            newDonHang.setTongTien(BigDecimal.ZERO);
            return donHangRepository.save(newDonHang);
        });

        // 📌 **Thêm chi tiết đơn hàng**: Nếu sản phẩm đã có thì tăng số lượng, nếu chưa thì tạo mới
        Optional<chitietdonhang> optionalChiTiet = chiTietDonHangRepository.findByDonHangAndSanPham(donHang, sanPham);
        chitietdonhang chiTiet;

        if (optionalChiTiet.isPresent()) {
            chiTiet = optionalChiTiet.get();
            chiTiet.setSoLuong(chiTiet.getSoLuong() + 1);
        } else {
            chiTiet = new chitietdonhang();
            chiTiet.setDonHang(donHang);
            chiTiet.setSanPham(sanPham);
            chiTiet.setSoLuong(1);
            chiTiet.setGia(sanPham.getGia());
        }

        chiTietDonHangRepository.save(chiTiet);

        // 📌 **Cập nhật tổng tiền**: Tính lại tổng tiền của đơn hàng sau khi thêm sản phẩm
        BigDecimal newTotal = donHang.getTongTien().add(sanPham.getGia());
        donHang.setTongTien(newTotal);
        donHangRepository.save(donHang);

        // 📌 **Đếm số loại sản phẩm**: Tính số loại sản phẩm khác nhau trong giỏ để hiển thị trên giao diện
        int cartCount = (int) chiTietDonHangRepository.findByDonHang_NhanVien_IdAndDonHang_TrangThai(currentUser.getId(), "Chưa thanh toán")
                .stream()
                .map(chitietdonhang::getSanPham)
                .distinct()
                .count();
        session.setAttribute("cartCount", cartCount);

        // 📌 **Trả phản hồi JSON**: Gửi dữ liệu về giao diện để cập nhật mà không cần tải lại trang
        response.put("status", "success");
        response.put("cartCount", cartCount);
        response.put("totalAmount", donHang.getTongTien());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/view")
    public String viewCart(Model model, HttpSession session) {
        // 📌 **Kiểm tra đăng nhập**: Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
        nhanvien currentUser = (nhanvien) session.getAttribute("currentUser");
        if (currentUser == null) {
            System.out.println("🔴 Lỗi: Người dùng chưa đăng nhập.");
            return "redirect:/login";
        }

        // 📌 **Lấy giỏ hàng**: Truy vấn tất cả sản phẩm trong giỏ của nhân viên hiện tại
        List<chitietdonhang> cartItems = chiTietDonHangRepository.findByDonHang_NhanVien_IdAndDonHang_TrangThai(
                currentUser.getId(), "Chưa thanh toán"
        );

        model.addAttribute("chiTietDonHang", cartItems);

        // 📌 **Đếm số loại sản phẩm**: Đếm số loại sản phẩm để hiển thị số lượng trên giao diện
        int cartCount = (int) cartItems.stream().map(chitietdonhang::getSanPham).distinct().count();
        session.setAttribute("cartCount", cartCount);

        // 📌 **Tính tổng tiền**: Tính tổng tiền đơn hàng dựa trên số lượng và giá từng sản phẩm
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (chitietdonhang item : cartItems) {
            if (item.getGia() != null) {
                totalAmount = totalAmount.add(item.getGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
            }
        }

        // 📌 **Áp dụng giảm giá**: Giảm giá cố định và đảm bảo tổng tiền không âm
        BigDecimal discount = new BigDecimal(2000000);
        BigDecimal finalTotal = totalAmount.subtract(discount).max(BigDecimal.ZERO);

        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("finalTotal", finalTotal);

        return "cart"; // Chuyển đến trang hiển thị giỏ hàng
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @RequestParam Long sanPhamId,
            @RequestParam int quantity,
            HttpSession session) {

        nhanvien currentUser = (nhanvien) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Optional<donhang> optionalDonHang = donHangRepository.findByNhanVienAndTrangThai(currentUser, "Chưa thanh toán");
        if (optionalDonHang.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        donhang donHang = optionalDonHang.get();

        Optional<chitietdonhang> optionalChiTiet = chiTietDonHangRepository.findByDonHangAndSanPham(donHang, sanPhamRepository.findById(sanPhamId).orElse(null));
        if (optionalChiTiet.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        chitietdonhang chiTiet = optionalChiTiet.get();
        sanpham sanPham = chiTiet.getSanPham();

        // 📌 **Kiểm tra tồn kho**: Giới hạn số lượng theo số lượng tồn kho của sản phẩm
        if (quantity > sanPham.getSoLuong()) {
            quantity = sanPham.getSoLuong();
        }

        chiTiet.setSoLuong(quantity);
        chiTietDonHangRepository.save(chiTiet);

        // 📌 **Cập nhật tổng tiền**: Tính lại tổng tiền sau khi thay đổi số lượng
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (chitietdonhang item : chiTietDonHangRepository.findByDonHang_NhanVien_IdAndDonHang_TrangThai(currentUser.getId(), "Chưa thanh toán")) {
            totalAmount = totalAmount.add(item.getGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
        }
        donHang.setTongTien(totalAmount);
        donHangRepository.save(donHang);

        // 📌 **Trả phản hồi JSON**: Gửi tổng tiền và số lượng tồn kho để cập nhật giao diện
        Map<String, Object> response = new HashMap<>();
        response.put("totalAmount", totalAmount);
        response.put("soLuongTonKho", sanPham.getSoLuong());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long sanPhamId, HttpSession session) {
        nhanvien currentUser = (nhanvien) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<sanpham> optionalSanPham = sanPhamRepository.findById(sanPhamId);
        if (optionalSanPham.isEmpty()) {
            return "redirect:/cart/view";
        }
        sanpham sanPham = optionalSanPham.get();

        Optional<donhang> optionalDonHang = donHangRepository.findByNhanVienAndTrangThai(currentUser, "Chưa thanh toán");
        if (optionalDonHang.isEmpty()) {
            return "redirect:/cart/view";
        }
        donhang donHang = optionalDonHang.get();

        Optional<chitietdonhang> optionalChiTiet = chiTietDonHangRepository.findByDonHangAndSanPham(donHang, sanPham);
        if (optionalChiTiet.isEmpty()) {
            return "redirect:/cart/view";
        }

        chitietdonhang chiTiet = optionalChiTiet.get();

        // 📌 **Xóa sản phẩm**: Xóa chi tiết đơn hàng chứa sản phẩm khỏi giỏ
        chiTietDonHangRepository.delete(chiTiet);

        // 📌 **Cập nhật tổng tiền**: Trừ giá trị sản phẩm vừa xóa ra khỏi tổng tiền
        donHang.setTongTien(donHang.getTongTien().subtract(chiTiet.getGia().multiply(BigDecimal.valueOf(chiTiet.getSoLuong()))));
        donHangRepository.save(donHang);

        // 📌 **Cập nhật số lượng**: Đếm lại số loại sản phẩm trong giỏ
        int cartCount = chiTietDonHangRepository.countByNhanVienAndTrangThai(currentUser, "Chưa thanh toán");
        session.setAttribute("cartCount", cartCount);

        return "redirect:/cart/view";
    }

    @GetMapping("/count")
    @ResponseBody
    public int getCartCount(HttpSession session) {
        nhanvien currentUser = (nhanvien) session.getAttribute("currentUser");
        if (currentUser == null) {
            return 0;
        }

        // 📌 **Đếm số loại sản phẩm**: Trả về số loại sản phẩm trong giỏ để hiển thị trên giao diện
        return (int) chiTietDonHangRepository.findByDonHang_NhanVien_IdAndDonHang_TrangThai(
                currentUser.getId(), "Chưa thanh toán"
        ).stream().map(chitietdonhang::getSanPham).distinct().count();
    }

    @PostMapping("/checkout")
    @Transactional
    public String checkout(HttpSession session) {
        nhanvien currentUser = (nhanvien) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<donhang> optionalDonHang = donHangRepository.findByNhanVienAndTrangThai(currentUser, "Chưa thanh toán");
        if (optionalDonHang.isEmpty()) {
            return "redirect:/cart/view";
        }

        // 📌 **Thanh toán**: Cập nhật trạng thái đơn hàng thành "Chờ xác nhận"
        donhang donHang = optionalDonHang.get();
        donHang.setTrangThai("Chờ xác nhận");
        donHangRepository.save(donHang);

        // 📌 **Xóa giỏ hàng**: Đặt lại số lượng sản phẩm trong giỏ về 0
        session.setAttribute("cartCount", 0);

        // 📌 **Chuyển đến hóa đơn**: Chuyển hướng đến trang xem hóa đơn với ID đơn hàng
        return "redirect:/invoice/view?donHangId=" + donHang.getId();
    }
}