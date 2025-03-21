package com.example.assignment_java5.controller;

import com.example.assignment_java5.model.sanpham;
import com.example.assignment_java5.service.sanphamservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/index")
public class indexcontroller {
    @Autowired
    private sanphamservice SanPhamService;

    @GetMapping("/")
    public String home(Model model) {
        // Lấy danh sách sản phẩm
        List<sanpham> newestProducts = SanPhamService.getTop6NewestProducts();
        List<sanpham> randomProducts = SanPhamService.get8RandomProducts();

        // Định dạng giá với dấu chấm làm phân cách hàng nghìn
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        newestProducts = newestProducts.stream().map(product -> {
            String formattedPrice = decimalFormat.format(product.getGia());
            product.setFormattedPrice(formattedPrice); // Lưu giá đã định dạng
            return product;
        }).collect(Collectors.toList());

        randomProducts = randomProducts.stream().map(product -> {
            String formattedPrice = decimalFormat.format(product.getGia());
            product.setFormattedPrice(formattedPrice);
            return product;
        }).collect(Collectors.toList());

        model.addAttribute("newestProducts", newestProducts);
        model.addAttribute("randomProducts", randomProducts);

        return "index";
    }
}