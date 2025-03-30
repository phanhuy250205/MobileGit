package com.example.assignment_java5.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duong_dan_anh", nullable = false)
    private String duongDanAnh; // Đường dẫn ảnh được upload

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private blog blog; // Liên kết với bảng blog

    @Column(name = "ngay_upload", nullable = false)
    private LocalDateTime ngayUpload; // Thời gian upload ảnh

    @Column(name = "trang_thai", columnDefinition = "NVARCHAR(50) DEFAULT 'Hoạt động'")
    private String trangThai = "Hoạt động"; // Trạng thái ảnh (Hoạt động, Đã xóa, ...)

    @Column(name = "mo_ta")
    private String moTa; // Mô tả ngắn về ảnh (tùy chọn)

    // Tự động cập nhật ngày upload khi tạo mới
    @PrePersist
    protected void onCreate() {
        this.ngayUpload = LocalDateTime.now();
    }
}