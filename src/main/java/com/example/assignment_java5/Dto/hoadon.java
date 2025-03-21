package com.example.assignment_java5.Dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class hoadon {
    private Long id;
    private Long khachHangId;
    private LocalDateTime ngayLap;
    private BigDecimal tongTien;
    private Long nhanVienId;
}
