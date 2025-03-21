package com.example.assignment_java5.service;

import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.model.phanloaichucvu;
import com.example.assignment_java5.repository.nhanvienrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private nhanvienrepository nhanVienRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        nhanvien nhanVien = nhanVienRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        // Ánh xạ role dựa trên chucVu.id
        String role;
        phanloaichucvu chucVu = nhanVien.getChucVu();

        if (chucVu != null && chucVu.getId() != null) {
            switch (chucVu.getId().intValue()) {
                case 10002:
                    role = "ROLE_USER";
                    break;
                // Thêm các case khác nếu cần
                default:
                    role = "ROLE_GUEST";
                    break;
            }
        } else {
            role = "ROLE_GUEST";
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        return new User(
                nhanVien.getEmail(),
                nhanVien.getPasswold(), // Giả sử đã sửa tên trường thành password
                Collections.singletonList(authority)
        );
    }
}