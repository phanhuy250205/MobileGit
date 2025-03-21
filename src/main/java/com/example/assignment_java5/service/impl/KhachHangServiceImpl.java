package com.example.assignment_java5.service.impl;

import com.example.assignment_java5.Dto.KhachHangDTO;
import com.example.assignment_java5.repository.KhachHangRepository;
import com.example.assignment_java5.service.KhachHangService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KhachHangServiceImpl  implements KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;
    @Override
    public List<KhachHangDTO> getAllKhachHang() {
       return khachHangRepository.getDanhSachKhachHang();
    }

    @Override
    @Transactional
    public void updateTrangthai(Long id, String trangthai) {
        khachHangRepository.updateTrangThai(id, trangthai);
    }

    @Override
    public KhachHangDTO getKhachHangById(Long id) {
        return khachHangRepository.findKhachHangById(id);
    }

    @Override
    public Map<String, Integer> thongKeKhachHang() {
        List<KhachHangDTO> danhsachKhachHang = khachHangRepository.getDanhSachKhachHang();

        int tongKhachHang =  danhsachKhachHang.size();
        int dangHoatDong = (int) danhsachKhachHang.stream().filter(kh -> "Hoạt Động".equalsIgnoreCase(kh.getTrangThai())).count();

        int daKhoa = tongKhachHang - dangHoatDong;

        Map<String, Integer> thongke = new HashMap<>();

        thongke.put("tongKhachHang" , tongKhachHang);
        thongke.put("dangHoatDong" , tongKhachHang);
        thongke.put("daKhoa" , daKhoa);
        return thongke;
    }

}
