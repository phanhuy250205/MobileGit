package com.example.assignment_java5.controller;

import com.example.assignment_java5.Dto.sanphamdto;
import com.example.assignment_java5.model.HinhAnhSanPham;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.model.phanloaihang;
import com.example.assignment_java5.model.sanpham;
import com.example.assignment_java5.service.HinhAnhSanPhamService;
import com.example.assignment_java5.service.PhanLoaiHangService;
import com.example.assignment_java5.service.sanphamservice;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/sanpham")
public class sanphamcontroller {
    @Autowired
    private sanphamservice Sanphamservice;

    @Autowired
    private PhanLoaiHangService phanLoaiHangService;

    @Autowired
    private HinhAnhSanPhamService hinhAnhSanPhamService;

    @GetMapping("/list")
    public String getSanPhamList(@RequestParam(value = "category", required = false) Long categoryId,
                                 @RequestParam(value = "searchTerm", required = false) String searchTerm,
                                 @RequestParam(value = "minGia", required = false) Double minGia,
                                 @RequestParam(value = "maxGia", required = false) Double maxGia,
                                 @RequestParam(value = "thuongHieu", required = false) List<String> thuongHieu,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "12") int size,
                                 Model model, HttpSession session) {

        Page<sanpham> sanPhamPage;

        nhanvien currentUser = (nhanvien) session.getAttribute("currentUser");
        Long currentUserId = (currentUser != null && currentUser.getChucVu() != null) ? currentUser.getChucVu().getId() : null;
        System.out.println("Current User ID: " + currentUserId); // Log để kiểm tra

        if (categoryId != null) {
            sanPhamPage = Sanphamservice.getSanPhamByPhanLoaiHang_Id(categoryId, PageRequest.of(page, size));
        } else if (searchTerm != null || minGia != null || maxGia != null || (thuongHieu != null && !thuongHieu.isEmpty())) {
            sanPhamPage = Sanphamservice.searchAndFilterSanPham(searchTerm, minGia, maxGia, thuongHieu, page, size);
        } else {
            sanPhamPage = Sanphamservice.getAllSanPhamWithPagination(PageRequest.of(page, size));
        }

        List<phanloaihang> danhMucList = phanLoaiHangService.getAllDanhMuc();

        model.addAttribute("sanPhamPage", sanPhamPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamPage.getTotalPages());
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("minGia", minGia);
        model.addAttribute("maxGia", maxGia);
        model.addAttribute("thuongHieu", thuongHieu);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("danhMucList", danhMucList);

        // Sửa logic phân quyền
        if (currentUser != null && currentUser.getChucVu() != null) {
            if (currentUserId == 10002) { // USER
                model.addAttribute("showAddToCart", true);
                model.addAttribute("showStats", false);
            } else if (currentUserId == 1) { // ADMIN
                model.addAttribute("showAddToCart", false);
                model.addAttribute("showStats", true);
            } else { // Các vai trò khác
                model.addAttribute("showAddToCart", false);
                model.addAttribute("showStats", false);
            }
        } else {
            model.addAttribute("showAddToCart", false); // Không đăng nhập
            model.addAttribute("showStats", false);
        }

        return "products";
    }
    @GetMapping("/uploadsanpham")
    public String uploadsanpham(Model model) {
        // 📌 **Chuẩn bị dữ liệu cho trang upload**: Lấy danh sách sản phẩm và danh mục để hiển thị
        List<sanpham> sanphamList = Sanphamservice.getallSanpham();
        List<phanloaihang> danhMucList = phanLoaiHangService.getAllDanhMuc();

        model.addAttribute("sanPhams", sanphamList);
        model.addAttribute("danhMucList", danhMucList);
        model.addAttribute("sanPham", new sanpham()); // Thêm đối tượng rỗng để tránh lỗi null

        return "uploadoder";
    }

    @GetMapping("/detail/{id}")
    public String getSanPhamDetail(@PathVariable Long id, Model model, HttpSession session) {
        Optional<sanpham> sanPhamOpt = Sanphamservice.getSanPhamById(id);
        if (sanPhamOpt.isEmpty()) {
            return "redirect:/sanpham";
        }

        sanpham sanPham = sanPhamOpt.get();
        List<HinhAnhSanPham> danhSachAnh = hinhAnhSanPhamService.getHinhAnhBySanPhamId(id);
        for (HinhAnhSanPham anh : danhSachAnh) {
            System.out.println("Ảnh URL: " + anh.getUrlHinhAnh());
        }

        nhanvien currentUser = (nhanvien) session.getAttribute("currentUser");
        Long currentUserId = (currentUser != null && currentUser.getChucVu() != null) ? currentUser.getChucVu().getId() : null;
        System.out.println("Current User ID: " + currentUserId); // Log để kiểm tra

        model.addAttribute("sanPham", sanPham);
        model.addAttribute("danhSachAnh", danhSachAnh);

        // Sửa logic phân quyền
        if (currentUser != null && currentUser.getChucVu() != null && currentUserId == 10002) {
            model.addAttribute("showAddToCart", true);
        } else {
            model.addAttribute("showAddToCart", false);
        }

        return "product-detail";
    }
    @GetMapping("/{id}")
    public String getsanphamid(@PathVariable int id, Model model) {
        // 📌 **Lấy sản phẩm theo ID**: Truy vấn sản phẩm để hiển thị chi tiết
        sanpham Sanpham = Sanphamservice.getSanPhamById(Long.valueOf(id)).orElse(null);
        model.addAttribute("sanPham", Sanpham);
        return "product-detail"; // Trả về trang chi tiết sản phẩm
    }

    @PostMapping("/create")
    public String createSanpham(@ModelAttribute sanpham sanPham,
                                @RequestParam("phanLoaiId") Long phanLoaiId,
                                @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        // 📌 **Gán danh mục**: Liên kết sản phẩm với danh mục trước khi tạo
        sanPham.setPhanLoaiHang(phanLoaiHangService.getDanhMucById(phanLoaiId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy danh mục với ID: " + phanLoaiId)));

        // 📌 **Tạo sản phẩm**: Gọi service để lưu sản phẩm và ảnh (nếu có)
        Sanphamservice.createSanPham(sanPham, files);
        return "redirect:/api/sanpham/uploadsanpham"; // Điều hướng về trang upload sau khi tạo
    }

    @GetMapping("/edit/{id}")
    public String editSanPham(@PathVariable Long id, Model model) {
        // 📌 **Tìm sản phẩm để sửa**: Kiểm tra sản phẩm tồn tại trước khi chỉnh sửa
        Optional<sanpham> sanPhamOpt = Sanphamservice.getSanPhamById(id);
        if (sanPhamOpt.isPresent()) {
            sanpham sanPham = sanPhamOpt.get();
            model.addAttribute("sanPham", sanPham);
            List<phanloaihang> danhMucList = phanLoaiHangService.getAllDanhMuc();
            model.addAttribute("danhMucList", danhMucList);
            return "uploadoder"; // Trả về trang chỉnh sửa sản phẩm
        } else {
            return "redirect:/api/sanpham/uploadsanpham"; // Điều hướng về trang upload nếu không tìm thấy
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteSanPham(@PathVariable Long id) {
        // 📌 **Xóa sản phẩm**: Gọi service để xóa sản phẩm theo ID
        Sanphamservice.deleteSanPhamById(id);
        return "redirect:/api/sanpham/uploadsanpham";  // Điều hướng về trang upload sau khi xóa
    }

    @PostMapping("/update")
    public String updateSanPham(@ModelAttribute sanpham sanPham,
                                @RequestParam("phanLoaiId") Long phanLoaiId,
                                @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        // 📌 **Gán danh mục**: Liên kết sản phẩm với danh mục trước khi cập nhật
        sanPham.setPhanLoaiHang(phanLoaiHangService.getDanhMucById(phanLoaiId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy danh mục với ID: " + phanLoaiId)));

        // 📌 **Cập nhật sản phẩm**: Gọi service để cập nhật sản phẩm và ảnh (nếu có)
        Sanphamservice.updateSanPham(sanPham.getId(), sanPham, files);

        return "redirect:/api/sanpham/uploadsanpham";  // Điều hướng về trang upload sau khi sửa
    }
}