package com.example.assignment_java5.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "don_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class donhang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nhan_vien_id") // Đổi từ khach_hang_id sang nhan_vien_id
    private nhanvien nhanVien; // Liên kết với entity nhanvien thay vì khachhang

    private LocalDateTime ngayDat = LocalDateTime.now();

    @Column(nullable = false)
    private BigDecimal tongTien;
    @Column(name = "trang_thai") // Cập nhật theo tên mới trong DB
    private String trangThai;





}
