package com.example.assignment_java5.repository;

import com.example.assignment_java5.model.chitietdonhang;
import com.example.assignment_java5.model.donhang;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.model.sanpham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface chitietdonhangreponsitory extends JpaRepository<chitietdonhang, Integer> {

    List<chitietdonhang> findByDonHang(donhang donHang);

    Optional<chitietdonhang> findByDonHangAndSanPham(donhang donHang, sanpham sanPham);



    @Query("SELECT COUNT(c) FROM chitietdonhang c WHERE c.donHang.nhanVien = :nhanVien AND c.donHang.trangThai = :trangThai")
    int countByNhanVienAndTrangThai(@Param("nhanVien") nhanvien nhanVien, @Param("trangThai") String trangThai);

    @Query("SELECT c FROM chitietdonhang c WHERE c.donHang.nhanVien.id = :nhanVienId AND c.donHang.trangThai = 'Chưa thanh toán'")
    List<chitietdonhang> findCartByNhanVienId(@Param("nhanVienId") Long nhanVienId);

    List<chitietdonhang> findByDonHang_NhanVien_IdAndDonHang_TrangThai(Long nhanVienId, String trangThai);

}
