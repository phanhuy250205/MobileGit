package com.example.assignment_java5.repository;

import com.example.assignment_java5.model.HinhAnhSanPham;
import com.example.assignment_java5.model.sanpham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface hinhanhsanphamrepository extends JpaRepository<HinhAnhSanPham,Long> {
    List<HinhAnhSanPham> findBySanPhamId(Long sanPhamId);
}
