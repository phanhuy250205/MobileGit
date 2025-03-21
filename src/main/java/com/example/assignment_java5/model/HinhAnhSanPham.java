package com.example.assignment_java5.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hinh_anh_san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HinhAnhSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_hinh_anh", nullable = false)
    private String urlHinhAnh;

    @ManyToOne
    @JoinColumn(name = "san_pham_id", nullable = false)
    private sanpham sanPham;
}
