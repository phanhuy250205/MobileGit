package com.example.assignment_java5.repository;

import com.example.assignment_java5.model.blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<blog, Long> {
    List<blog> findAllByNhanVienId(Long nhanVienId);
    List<blog> findByTrangThai(String trangThai);
}
