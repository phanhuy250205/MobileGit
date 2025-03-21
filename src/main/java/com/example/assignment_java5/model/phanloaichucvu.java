package com.example.assignment_java5.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phan_loai_chuc_vu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class phanloaichucvu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenChucVu;

    private String moTa;
}
