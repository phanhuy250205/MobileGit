package com.example.assignment_java5.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "nhan_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class nhanvien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenNhanVien;

    @Column(unique = true, nullable = false)
    private String email;

    private String soDienThoai;
    private String diaChi;

    private String ngayTao;

    private  String passwold;

    private  String avatar;

    private LocalDate ngaysinh;

    @ManyToOne
    @JoinColumn(name = "chuc_vu_id")
    private phanloaichucvu chucVu;


    @Column(name = "trang_thai", columnDefinition = "NVARCHAR(50) DEFAULT 'Hoạt động'")
    private String trangThai = "Hoạt động"; // Đặt mặc định ở đây

}
