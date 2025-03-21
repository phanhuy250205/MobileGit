package com.example.assignment_java5.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class hoadon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private LocalDateTime ngayLap = LocalDateTime.now();

    @Column(nullable = false)
    private BigDecimal tongTien;

    @ManyToOne
    @JoinColumn(name = "nhan_vien_id")
    private nhanvien nhanVien;

    // Người mua hàng (có thể là nhân viên hoặc khách hàng dựa trên role)
    @ManyToOne
    @JoinColumn(name = "nguoi_mua_id", nullable = false)
    private nhanvien nguoiMua;
}
