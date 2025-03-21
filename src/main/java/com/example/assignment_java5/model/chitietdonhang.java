package com.example.assignment_java5.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_don_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class chitietdonhang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "don_hang_id")
    private donhang donHang;

    @ManyToOne
    @JoinColumn(name = "san_pham_id")
    private sanpham sanPham;

    @Column(nullable = false)
    private int soLuong;

    @Column(nullable = false)
    private BigDecimal gia;



}
