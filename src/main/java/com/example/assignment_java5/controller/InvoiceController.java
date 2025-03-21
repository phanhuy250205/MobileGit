package com.example.assignment_java5.controller;

import com.example.assignment_java5.model.*;
import com.example.assignment_java5.repository.chitietdonhangreponsitory;
import com.example.assignment_java5.repository.donhangrepository;
import com.example.assignment_java5.repository.hoadonrepository;
import com.example.assignment_java5.repository.sanphamrepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    private final donhangrepository donHangRepository;
    private final chitietdonhangreponsitory chiTietDonHangRepository;

    @Autowired
    private sanphamrepository sanphamRepository;

    @Autowired
    private hoadonrepository hoadonRepository;

    @Autowired
    public InvoiceController(donhangrepository donHangRepository, chitietdonhangreponsitory chiTietDonHangRepository) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
    }

    @GetMapping("/view")
    public String viewInvoice(@RequestParam Long donHangId, Model model) {
        // 📌 **Kiểm tra đơn hàng**: Xác nhận đơn hàng tồn tại trước khi hiển thị hóa đơn
        Optional<donhang> optionalDonHang = donHangRepository.findById(donHangId);
        if (optionalDonHang.isEmpty()) {
            return "redirect:/cart/view";
        }

        donhang donHang = optionalDonHang.get();
        nhanvien customer = donHang.getNhanVien(); // Lấy thông tin nhân viên/người mua

        // 📌 **In thông tin kiểm tra**: Hiển thị thông tin để debug và xác nhận dữ liệu
        System.out.println("Họ tên: " + customer.getTenNhanVien());
        System.out.println("Số điện thoại: " + customer.getSoDienThoai());
        System.out.println("Địa chỉ: " + customer.getDiaChi());

        // 📌 **Truyền dữ liệu vào Model**: Chuẩn bị dữ liệu để hiển thị trên giao diện hóa đơn
        model.addAttribute("donHang", donHang);
        model.addAttribute("customer", customer);
        model.addAttribute("cartItems", chiTietDonHangRepository.findByDonHang(donHang)); // Danh sách sản phẩm trong đơn
        model.addAttribute("totalAmount", donHang.getTongTien());

        return "hoadon";
    }

    @PostMapping("/confirm")
    @Transactional
    public ResponseEntity<Map<String, Object>> confirmInvoice(@RequestParam Long donHangId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 📌 **Kiểm tra đơn hàng**: Đảm bảo đơn hàng tồn tại trước khi xác nhận
        Optional<donhang> optionalDonHang = donHangRepository.findById(donHangId);
        if (optionalDonHang.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Không tìm thấy đơn hàng.");
            return ResponseEntity.badRequest().body(response);
        }

        donhang donHang = optionalDonHang.get();
        nhanvien nguoiMua = donHang.getNhanVien(); // Lấy thông tin người mua từ đơn hàng

        // 📌 **Kiểm tra đăng nhập**: Xác nhận nhân viên đã đăng nhập để thực hiện xác nhận hóa đơn
        nhanvien nhanVienXacNhan = (nhanvien) session.getAttribute("currentUser");
        if (nhanVienXacNhan == null) {
            response.put("status", "error");
            response.put("message", "Bạn cần đăng nhập để xác nhận đơn hàng!");
            return ResponseEntity.badRequest().body(response);
        }

        // 📌 **Tạo hóa đơn**: Lưu thông tin hóa đơn vào cơ sở dữ liệu
        hoadon hoaDon = new hoadon();
        hoaDon.setNguoiMua(nguoiMua);
        hoaDon.setNhanVien(nhanVienXacNhan);
        hoaDon.setNgayLap(LocalDateTime.now());
        hoaDon.setTongTien(donHang.getTongTien());
        hoadonRepository.save(hoaDon);

        // 📌 **Cập nhật trạng thái đơn hàng**: Chuyển trạng thái từ "Chờ xác nhận" sang "Đã thanh toán"
        donHang.setTrangThai("Đã thanh toán");
        donHangRepository.save(donHang);

        // 📌 **Trả phản hồi JSON**: Gửi thông báo thành công và ID hóa đơn để xử lý tiếp theo trên giao diện
        response.put("status", "success");
        response.put("message", "Mua hàng thành công!");
        response.put("hoadonId", hoaDon.getId());

        return ResponseEntity.ok(response);
    }
}