package com.example.assignment_java5.repository;

import com.example.assignment_java5.model.nhanvien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface nhanvienrepository extends JpaRepository<nhanvien, Long> {
    Optional<nhanvien> findByEmail(String email);

    Optional<nhanvien> findById(Long id); // ✅ Thêm phương thức findById
}
