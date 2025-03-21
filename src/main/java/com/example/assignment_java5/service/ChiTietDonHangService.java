package com.example.assignment_java5.service;


import com.example.assignment_java5.model.chitietdonhang;
import com.example.assignment_java5.model.donhang;
import com.example.assignment_java5.model.sanpham;
import com.example.assignment_java5.repository.chitietdonhangreponsitory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChiTietDonHangService {
    private final chitietdonhangreponsitory chiTietDonHangRepository;

    public ChiTietDonHangService(chitietdonhangreponsitory chiTietDonHangRepository) {
        this.chiTietDonHangRepository = chiTietDonHangRepository;
    }

    // 🔹 Lưu chi tiết đơn hàng
    public void saveChiTietDonHang(chitietdonhang chiTietDonHang) {
        chiTietDonHangRepository.save(chiTietDonHang);
    }

    // 🔹 Lấy danh sách chi tiết đơn hàng theo đơn hàng
    public List<chitietdonhang> getChiTietByDonHang(donhang donHang) {
        return chiTietDonHangRepository.findByDonHang(donHang);
    }

    // 🔹 Kiểm tra sản phẩm đã có trong đơn hàng chưa
    public Optional<chitietdonhang> findByDonHangAndSanPham(donhang donHang, sanpham sanPham) {
        return chiTietDonHangRepository.findByDonHangAndSanPham(donHang, sanPham);
    }
}
