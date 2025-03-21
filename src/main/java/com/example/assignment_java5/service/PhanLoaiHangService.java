package com.example.assignment_java5.service;

import com.example.assignment_java5.model.phanloaihang;
import java.util.List;
import java.util.Optional;

public interface PhanLoaiHangService {
    List<phanloaihang> getAllDanhMuc(); // Lấy tất cả danh mục
    Optional<phanloaihang> getDanhMucById(Long id); // Lấy danh mục theo ID
}
