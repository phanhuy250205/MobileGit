package com.example.assignment_java5.Dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class donhangdto {
    private Long id;
    private Long khachHangId;
    private LocalDateTime ngayDat;
    private BigDecimal tongTien;
    private  String trangThai;
}
