package com.example.assignment_java5.service;

import com.example.assignment_java5.model.donhang;

import java.util.Map;

public interface CartService {
    void addToCart(Long productId, Long nhanVienId); // ✅ Đổi từ khachHangId thành nhanVienId
    void clearCart(Long nhanVienId);
    Map<Long, Integer> getCartItems(Long nhanVienId);
    donhang checkout(Long nhanVienId);
    int getCartCount(Long nhanVienId);
}
