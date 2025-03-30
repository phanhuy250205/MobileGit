package com.example.assignment_java5.repository;

import com.example.assignment_java5.model.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogImageRepository extends JpaRepository<BlogImage, Long> {
    // Tìm tất cả ảnh theo blogId
    List<BlogImage> findByBlogId(Long blogId);

    // Tìm ảnh theo đường dẫn
    BlogImage findByDuongDanAnh(String duongDanAnh);
}