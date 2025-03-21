package com.example.assignment_java5.repository;

import com.example.assignment_java5.model.phanloaichucvu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface phanloaichucvurepository extends JpaRepository<phanloaichucvu, Long> {

    // Tìm vai trò theo ID (Long) - không cần khai báo static
    Optional<phanloaichucvu> findById(Long id);

    // Tìm vai trò theo tên chức vụ (tenChucVu)
    Optional<phanloaichucvu> findByTenChucVu(String tenChucVu);
}
