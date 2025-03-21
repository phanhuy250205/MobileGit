package com.example.assignment_java5.repository;

import com.example.assignment_java5.model.sanpham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface sanphamrepository extends JpaRepository<sanpham, Long>, JpaSpecificationExecutor<sanpham> {
    // Tìm kiếm sản phẩm dựa trên tên hoặc mô tả, tìm chuỗi con
    List<sanpham> findByTensanphamContainingOrMotaContaining(String tensanpham, String searchTerm);
    Optional<sanpham> findById(Long id);

    //Lọc theo khoản giá Theo tên hoặc mô tả
    Page<sanpham> findByTensanphamContainingOrMotaContaining(String tensanpham, String mota, Pageable pageable);
    Page<sanpham> findByGiaBetweenAndThuongHieuIn(Double minGia, Double maxGia, List<String> thuongHieu, Pageable pageable);
    @Query("SELECT s FROM sanpham s WHERE " +
            "(:searchTerm IS NULL OR s.tensanpham LIKE %:searchTerm% OR s.mota LIKE %:searchTerm%) AND " +
            "(:minGia IS NULL OR s.gia >= :minGia) AND " +
            "(:maxGia IS NULL OR s.gia <= :maxGia) AND " +
            "(:thuongHieu IS NULL OR s.thuongHieu IN :thuongHieu)")
    Page<sanpham> searchAndFilter(@Param("searchTerm") String searchTerm,
                                  @Param("minGia") Double minGia,
                                  @Param("maxGia") Double maxGia,
                                  @Param("thuongHieu") List<String> thuongHieu,
                                  Pageable pageable);

    Page<sanpham> findByPhanLoaiHang_Id(Long phanLoaiId, Pageable pageable);

    // Lấy sản phẩm theo danh mục (không phân trang)
    List<sanpham> findByPhanLoaiHang_Id(Long phanLoaiId);

    // Lấy 5 sản phẩm mới nhất theo ngày tạo
    List<sanpham> findTop6ByOrderByNgayTaoDesc();
}
