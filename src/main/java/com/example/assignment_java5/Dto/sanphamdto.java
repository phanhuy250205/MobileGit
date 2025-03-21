package com.example.assignment_java5.Dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class sanphamdto {

    private Long id;
    private String tenSanPham;
    private String moTa;
    private BigDecimal gia;
    private int soLuong;
//    private String anh;
    private String thuongHieu;
    private String model;
    private String cauHinh;
    private String trangThai;
    private BigDecimal giamGia;
}
