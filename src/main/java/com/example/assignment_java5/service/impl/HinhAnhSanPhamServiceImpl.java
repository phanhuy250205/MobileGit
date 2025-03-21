package com.example.assignment_java5.service.impl;

import com.example.assignment_java5.model.HinhAnhSanPham;

import com.example.assignment_java5.repository.hinhanhsanphamrepository;
import com.example.assignment_java5.service.HinhAnhSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HinhAnhSanPhamServiceImpl implements HinhAnhSanPhamService {

    @Autowired
    private hinhanhsanphamrepository HinhAnhSanPhamRepository;

    @Override
    public HinhAnhSanPham saveHinhAnh(HinhAnhSanPham hinhAnhSanPham) {
        return HinhAnhSanPhamRepository.save(hinhAnhSanPham);
    }

    @Override
    public List<HinhAnhSanPham> getHinhAnhBySanPhamId(Long sanPhamId) {
        return HinhAnhSanPhamRepository.findBySanPhamId(sanPhamId);
    }

    @Override
    public void deleteHinhAnh(Long id) {
        HinhAnhSanPhamRepository.deleteById(id);
    }
}
