package com.example.assignment_java5.service.impl;

import com.example.assignment_java5.model.HinhAnhSanPham;
import com.example.assignment_java5.model.sanpham;
import com.example.assignment_java5.repository.sanphamrepository;
import com.example.assignment_java5.service.FileUploadService;
import com.example.assignment_java5.service.HinhAnhSanPhamService;
import com.example.assignment_java5.service.sanphamservice;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class sanphamImpl implements sanphamservice {

    @Autowired
    private sanphamrepository sanPhamRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private HinhAnhSanPhamService hinhAnhSanPhamService;

    @Override
    public List<sanpham> getallSanpham() {
        return sanPhamRepository.findAll();
    }

    @Override
    public List<sanpham> getAllSanPham() {
        return List.of();
    }

    @Override
    public Optional<sanpham> getSanPhamById(Long id) {
        return sanPhamRepository.findById(id);
    }

    @Override
    public sanpham createSanPham(sanpham sanPham, List<MultipartFile> files) {
        sanpham savedSanPham = sanPhamRepository.save(sanPham);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String imageUrl = fileUploadService.uploadFile(file, "products");
                        HinhAnhSanPham hinhAnhSanPham = new HinhAnhSanPham();
                        hinhAnhSanPham.setSanPham(savedSanPham);
                        hinhAnhSanPham.setUrlHinhAnh(imageUrl);
                        hinhAnhSanPhamService.saveHinhAnh(hinhAnhSanPham);
                    } catch (IOException e) {
                        throw new RuntimeException("❌ Lỗi khi upload ảnh: " + e.getMessage());
                    }
                }
            }
        }
        return savedSanPham;
    }

    @Override
    public sanpham updateSanPham(Long id, sanpham sanPham, List<MultipartFile> files) {
        Optional<sanpham> existingSanPham = sanPhamRepository.findById(id);
        if (existingSanPham.isPresent()) {
            sanpham updateSanPham = existingSanPham.get();
            updateSanPham.setTensanpham(sanPham.getTensanpham());
            updateSanPham.setMota(sanPham.getMota());
            updateSanPham.setGia(sanPham.getGia());
            updateSanPham.setSoLuong(sanPham.getSoLuong());
            updateSanPham.setThuongHieu(sanPham.getThuongHieu());
            updateSanPham.setModel(sanPham.getModel());
            updateSanPham.setCauHinh(sanPham.getCauHinh());
            updateSanPham.setGiamGia(sanPham.getGiamGia());
            updateSanPham.setTrangThai(sanPham.getTrangThai());
            // Thêm dòng này để cập nhật danh mục
            updateSanPham.setPhanLoaiHang(sanPham.getPhanLoaiHang());

            if (files != null && !files.isEmpty()) {
                List<HinhAnhSanPham> oldImages = hinhAnhSanPhamService.getHinhAnhBySanPhamId(id);
                for (HinhAnhSanPham oldImage : oldImages) {
                    fileUploadService.deleteFile(oldImage.getUrlHinhAnh());
                    hinhAnhSanPhamService.deleteHinhAnh(oldImage.getId());
                }

                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        try {
                            String imageUrl = fileUploadService.uploadFile(file, "products");
                            HinhAnhSanPham hinhAnhSanPham = new HinhAnhSanPham();
                            hinhAnhSanPham.setSanPham(updateSanPham);
                            hinhAnhSanPham.setUrlHinhAnh(imageUrl);
                            hinhAnhSanPhamService.saveHinhAnh(hinhAnhSanPham);
                        } catch (IOException e) {
                            throw new RuntimeException("❌ Lỗi khi upload ảnh: " + e.getMessage());
                        }
                    }
                }
            }
            return sanPhamRepository.save(updateSanPham);
        }
        return null;
    }

    @Override
    public void deleteSanPhamById(Long id) {
        Optional<sanpham> optionalSanPham = sanPhamRepository.findById(id);
        if (optionalSanPham.isPresent()) {
            sanpham sanPham = optionalSanPham.get();
            List<HinhAnhSanPham> hinhAnhs = hinhAnhSanPhamService.getHinhAnhBySanPhamId(id);
            for (HinhAnhSanPham hinhAnh : hinhAnhs) {
                fileUploadService.deleteFile(hinhAnh.getUrlHinhAnh());
                hinhAnhSanPhamService.deleteHinhAnh(hinhAnh.getId());
            }
            sanPhamRepository.deleteById(id);
        } else {
            throw new RuntimeException("❌ Không tìm thấy sản phẩm để xóa!");
        }
    }

    @Override
    public Page<sanpham> searchAndFilterSanPham(String searchTerm, Double minGia, Double maxGia, List<String> thuongHieu, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        Specification<sanpham> spec = Specification.where(null);

        if (searchTerm != null && !searchTerm.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get("tensanpham"), "%" + searchTerm + "%"),
                            criteriaBuilder.like(root.get("mota"), "%" + searchTerm + "%")
                    )
            );
        }

        if (minGia != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("gia"), minGia)
            );
        }
        if (maxGia != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("gia"), maxGia)
            );
        }

        if (thuongHieu != null && !thuongHieu.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    root.get("thuongHieu").in(thuongHieu)
            );
        }

        return sanPhamRepository.findAll(spec, pageable);
    }
    @Override
    public Page<sanpham> getAllSanPhamWithPagination(Pageable pageable) {
        return sanPhamRepository.findAll(pageable);
    }
    public Page<sanpham> getAllSanPham(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findAll(pageable);
    }

    @Override
    public List<sanpham> getSanPhamByPhanLoaiHang_Id(Long phanLoaiId) {
        return sanPhamRepository.findByPhanLoaiHang_Id(phanLoaiId);
    }

    @Override
    public Page<sanpham> getSanPhamByPhanLoaiHang_Id(Long phanLoaiId, Pageable pageable) {
        return  sanPhamRepository.findByPhanLoaiHang_Id(phanLoaiId, pageable);
    }

    @Override
    public List<sanpham> getTop6NewestProducts() {
        return  sanPhamRepository.findTop6ByOrderByNgayTaoDesc();
    }


    @Override
    public List<sanpham> get8RandomProducts() {
       List<sanpham> allProducts = sanPhamRepository.findAll();
        List<sanpham> ramdomProduct  = new ArrayList<>();

        Random random = new Random();
        while (ramdomProduct.size() < 8 && allProducts.size() >0) {
            int index = random.nextInt(allProducts.size());
            ramdomProduct.add(allProducts.get(index));
            allProducts.remove(index);
        }
        return ramdomProduct;

    }


}
