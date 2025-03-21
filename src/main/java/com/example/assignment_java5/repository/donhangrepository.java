package com.example.assignment_java5.repository;

import com.example.assignment_java5.model.donhang;
import com.example.assignment_java5.model.nhanvien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface donhangrepository extends JpaRepository<donhang, Long> {

    @Query("SELECT d FROM donhang d WHERE d.nhanVien = :nhanVien AND d.trangThai = :trangThai")
    Optional<donhang> findByNhanVienAndTrangThai(@Param("nhanVien") nhanvien nhanVien, @Param("trangThai") String trangThai);

}
