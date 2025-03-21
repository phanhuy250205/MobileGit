package com.example.assignment_java5.controller;


import com.example.assignment_java5.model.phanloaihang;
import com.example.assignment_java5.service.PhanLoaiHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/phanloai")
public class PhanLoaiHangController {

    @Autowired
    private PhanLoaiHangService phanLoaiHangService;

    // ✅ API lấy danh mục dưới dạng JSON
    @GetMapping("/list")
    public List<phanloaihang> getDanhMucList() {
        return phanLoaiHangService.getAllDanhMuc();
    }
}
