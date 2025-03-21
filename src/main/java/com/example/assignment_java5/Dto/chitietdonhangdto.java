package com.example.assignment_java5.Dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class chitietdonhangdto {
    private Long id;
    private Long donHangId;
    private Long sanPhamId;
    private int soLuong;
    private BigDecimal gia;
}
