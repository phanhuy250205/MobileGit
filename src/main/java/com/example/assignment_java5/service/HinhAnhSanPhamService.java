package com.example.assignment_java5.service;

import com.example.assignment_java5.model.HinhAnhSanPham;
import java.util.List;

public interface HinhAnhSanPhamService {
    HinhAnhSanPham saveHinhAnh(HinhAnhSanPham hinhAnhSanPham);
    List<HinhAnhSanPham> getHinhAnhBySanPhamId(Long sanPhamId);
    void deleteHinhAnh(Long id);
}
