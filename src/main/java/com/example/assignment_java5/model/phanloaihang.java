package com.example.assignment_java5.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "phan_loai_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class phanloaihang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenPhanLoai;

    private String moTa;

    @OneToMany(mappedBy = "phanLoaiHang", cascade = CascadeType.ALL)
    private List<sanpham> sanPhams;
}
