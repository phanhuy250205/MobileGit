package com.example.assignment_java5.controller;

import com.example.assignment_java5.Dto.KhachHangDTO;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.repository.nhanvienrepository;
import com.example.assignment_java5.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class KhachHangController {
    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private nhanvienrepository NhanVienRepository;

    @GetMapping("/khachhang")
    public String getKhachHang(Model model) {
        // 📌 **Lấy danh sách khách hàng**: Gọi service để lấy tất cả thông tin khách hàng
        List<KhachHangDTO> danhSachKhachHang = khachHangService.getAllKhachHang();

        // 📌 **Thống kê khách hàng**: Lấy dữ liệu thống kê (tổng, đang hoạt động, đã khóa)
        Map<String, Integer> thongKe = khachHangService.thongKeKhachHang();

        // 📌 **Truyền dữ liệu vào Model**: Chuẩn bị dữ liệu để hiển thị trên giao diện quản lý khách hàng
        model.addAttribute("danhSachKhachHang", danhSachKhachHang);
        model.addAttribute("tongKhachHang", thongKe.get("tongKhachHang"));
        model.addAttribute("dangHoatDong", thongKe.get("dangHoatDong"));
        model.addAttribute("daKhoa", thongKe.get("daKhoa"));

        return "quanlykhachhang";
    }

    @PostMapping("/khachhang/update-status")
    @ResponseBody
    public ResponseEntity<?> updateKhachHangStatus(@RequestParam Long id, @RequestParam String trangThai) {
        try {
            // 📌 **Ghi log kiểm tra**: In thông tin request để debug và xác nhận dữ liệu đầu vào
            System.out.println("Nhận request cập nhật trạng thái: ID=" + id + ", Trạng thái mới=" + trangThai);

            // 📌 **Tìm khách hàng**: Kiểm tra xem khách hàng có tồn tại trong hệ thống không
            Optional<nhanvien> optionalKhachHang = NhanVienRepository.findById(id);

            if (optionalKhachHang.isPresent()) {
                nhanvien khachHang = optionalKhachHang.get();
                System.out.println("Tìm thấy nhân viên: " + khachHang.getTenNhanVien() + ", Trạng thái cũ=" + khachHang.getTrangThai());

                // 📌 **Cập nhật trạng thái**: Chỉ cập nhật nếu trạng thái mới khác trạng thái cũ
                if (khachHang.getTrangThai() == null || !khachHang.getTrangThai().equals(trangThai)) {
                    khachHang.setTrangThai(trangThai);
                    NhanVienRepository.save(khachHang); // Lưu thay đổi vào cơ sở dữ liệu
                    System.out.println("Cập nhật thành công trạng thái mới: " + khachHang.getTrangThai());

                    // 📌 **Trả phản hồi JSON**: Gửi trạng thái mới về giao diện để cập nhật
                    return ResponseEntity.ok(Collections.singletonMap("trangThai", khachHang.getTrangThai()));
                } else {
                    System.out.println("Trạng thái không thay đổi.");
                    return ResponseEntity.ok(Collections.singletonMap("error", "Trạng thái không thay đổi!"));
                }
            } else {
                System.out.println("Không tìm thấy khách hàng với ID: " + id);
                // 📌 **Thông báo lỗi**: Trả về lỗi nếu không tìm thấy khách hàng
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Khách hàng không tồn tại!"));
            }
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật trạng thái: " + e.getMessage());
            // 📌 **Xử lý ngoại lệ**: Trả về lỗi nếu có vấn đề trong quá trình cập nhật
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Lỗi cập nhật trạng thái!"));
        }
    }

    @GetMapping("/khachhang/{id}")
    @ResponseBody
    public ResponseEntity<?> getKhachHangById(@PathVariable Long id) {
        try {
            // 📌 **Lấy thông tin khách hàng**: Gọi service để lấy thông tin chi tiết của khách hàng theo ID
            KhachHangDTO khachHang = khachHangService.getKhachHangById(id);

            if (khachHang != null) {
                // 📌 **Trả phản hồi JSON**: Gửi thông tin khách hàng về giao diện nếu tìm thấy
                return ResponseEntity.ok(khachHang);
            } else {
                // 📌 **Thông báo lỗi**: Trả về lỗi nếu không tìm thấy khách hàng
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Không tìm thấy khách hàng!"));
            }
        } catch (Exception e) {
            // 📌 **Xử lý ngoại lệ**: Trả về lỗi nếu có vấn đề trong quá trình truy vấn
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Lỗi lấy thông tin khách hàng!"));
        }
    }
}