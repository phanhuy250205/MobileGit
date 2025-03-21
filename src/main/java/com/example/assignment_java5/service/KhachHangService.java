package com.example.assignment_java5.service;

import com.example.assignment_java5.Dto.KhachHangDTO;

import java.util.List;
import java.util.Map;

public interface KhachHangService {
    List<KhachHangDTO> getAllKhachHang();

    void updateTrangthai(Long id , String trangthai);

    KhachHangDTO getKhachHangById(Long id);
    Map<String, Integer> thongKeKhachHang();

}
