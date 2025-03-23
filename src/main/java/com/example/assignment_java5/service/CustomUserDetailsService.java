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
        SimpleGrantedAuthority authority; // Khai báo authority ở đây, chỉ gán giá trị một lần
        phanloaichucvu chucVu = nhanVien.getChucVu();

        if (chucVu != null && chucVu.getId() != null) {
            switch (chucVu.getId().intValue()) {
                case 2: // Admin
                    role = "ADMIN";
                    break;
                case 10002: // khachang
                    role = "USER";
                    break;
                default:
                    throw new UsernameNotFoundException("Invalid role for user with chuc_vu_id: " + chucVu.getId());
            }
            authority = new SimpleGrantedAuthority("ROLE_" + role);
        } else {
            authority = new SimpleGrantedAuthority("ROLE_GUEST"); // Nhất quán với tiền tố "ROLE_"
        }

        return new User(
                nhanVien.getEmail(),
                nhanVien.getPasswold(),
                Collections.singletonList(authority)
        );
    }
}