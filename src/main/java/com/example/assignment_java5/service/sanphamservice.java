package com.example.assignment_java5.service;

import com.example.assignment_java5.model.sanpham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface sanphamservice {
    List<sanpham> getallSanpham();

    // ✅ Lấy tất cả sản phẩm (không phân trang)
    List<sanpham> getAllSanPham();

    // ✅ Lấy sản phẩm theo ID
    Optional<sanpham> getSanPhamById(Long id);

    // ✅ Tạo sản phẩm mới (hỗ trợ tải lên ảnh)
    sanpham createSanPham(sanpham sanPham, List<MultipartFile> files);

    // ✅ Cập nhật sản phẩm (hỗ trợ tải lên ảnh)
    sanpham updateSanPham(Long id, sanpham sanPham, List<MultipartFile> files);

    // ✅ Xóa sản phẩm theo ID
    void deleteSanPhamById(Long id);

    // ✅ Tìm kiếm + Lọc + Phân trang
    Page<sanpham> searchAndFilterSanPham(String searchTerm, Double minGia, Double maxGia, List<String> thuongHieu, int page, int size);

    // ✅ Lấy tất cả sản phẩm có phân trang
    Page<sanpham> getAllSanPham(int page, int size);

    // ✅ Lấy sản phẩm theo danh mục (không phân trang)
    List<sanpham> getSanPhamByPhanLoaiHang_Id(Long phanLoaiId);

    // ✅ Lấy sản phẩm theo danh mục (có phân trang)
    Page<sanpham> getSanPhamByPhanLoaiHang_Id(Long phanLoaiId, Pageable pageable);

    List<sanpham> getTop6NewestProducts();
    List<sanpham> get8RandomProducts();
    Page<sanpham> getAllSanPhamWithPagination(Pageable pageable);
}
