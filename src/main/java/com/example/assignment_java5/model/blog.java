package com.example.assignment_java5.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tieu_de", nullable = false)
    private  String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "TEXT", nullable = false)
    private String noiDung; // Nội dung bài viết

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao; // Ngày tạo bài viết

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat; // Ngày cập nhật bài viết

    @ManyToOne
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private nhanvien nhanVien; // Liên kết với nhân viên

    @Column(name = "trang_thai", columnDefinition = "NVARCHAR(50) DEFAULT 'Hoạt động'")
    private String trangThai = "Hoạt động"; // Trạng thái mặc định

    // Tự động cập nhật ngày tạo trước khi lưu
    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
    }

    // Tự động cập nhật ngày cập nhật trước khi chỉnh sửa
    @PreUpdate
    protected void onUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}
