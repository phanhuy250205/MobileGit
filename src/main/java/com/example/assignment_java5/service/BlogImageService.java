package com.example.assignment_java5.service;

import com.example.assignment_java5.model.BlogImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogImageService {
    // Lưu ảnh mới
    BlogImage saveBlogImage(BlogImage blogImage);

    // Upload ảnh và lưu thông tin vào database
    BlogImage uploadAndSaveImage(MultipartFile file, Long blogId, String moTa) throws Exception;

    // Lấy ảnh theo ID
    BlogImage findById(Long id);

    // Lấy danh sách ảnh theo blogId
    List<BlogImage> findByBlogId(Long blogId);

    // Xóa ảnh theo ID
    void deleteBlogImage(Long id);

    // Cập nhật trạng thái ảnh
    void updateTrangThai(Long id, String trangThai);
}