package com.example.assignment_java5.service;

import com.example.assignment_java5.model.blog;

import java.util.List;

public interface BlogService {
    List<blog> findAll();
    blog findById(Long id);

    // Tạo hoặc cập nhật bài blog
    blog save(blog Blog);

    // Xóa bài blog theo ID
    void deleteById(Long id);

    // Lấy tất cả bài blog của một nhân viên
    List<blog> findByNhanVienId(Long nhanVienId);

    // Lấy tất cả bài blog theo trạng thái
    List<blog> findByTrangThai(String trangThai);
}
