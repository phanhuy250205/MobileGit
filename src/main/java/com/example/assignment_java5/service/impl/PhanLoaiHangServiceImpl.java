package com.example.assignment_java5.service.impl;

import com.example.assignment_java5.model.phanloaihang;

import com.example.assignment_java5.repository.phanloaihangrepository;
import com.example.assignment_java5.service.PhanLoaiHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PhanLoaiHangServiceImpl implements PhanLoaiHangService {

    @Autowired
    private phanloaihangrepository PhanLoaiHangRepository;

    @Override
    public List<phanloaihang> getAllDanhMuc() {
        return PhanLoaiHangRepository.findAll();
    }

    @Override
    public Optional<phanloaihang> getDanhMucById(Long id) {
        return PhanLoaiHangRepository.findById(Math.toIntExact(id));
    }
}
