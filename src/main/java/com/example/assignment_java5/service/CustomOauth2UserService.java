package com.example.assignment_java5.service;

import com.example.assignment_java5.Dto.nhanviendto;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.repository.nhanvienrepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOauth2UserService.class);

    @Autowired
    private nhanvienrepository nhanviendrepository;

    @Autowired
    private Userservice userservice;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        logger.info("Processing login from provider: {}", provider);
        logger.info("User attributes: {}", attributes);

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null || email.isEmpty()) {
            logger.error("Không lấy được email từ {}! Dữ liệu: {}", provider, attributes);
            throw new OAuth2AuthenticationException("Không lấy được email từ " + provider);
        }

        if (name == null || name.trim().isEmpty()) {
            if ("google".equals(provider)) {
                String givenName = (String) attributes.get("given_name");
                String familyName = (String) attributes.get("family_name");
                name = (givenName != null ? givenName : "") + " " + (familyName != null ? familyName : "");
            } else if ("facebook".equals(provider)) {
                String firstName = (String) attributes.get("first_name");
                String lastName = (String) attributes.get("last_name");
                name = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
            }
            if (name == null || name.trim().isEmpty()) {
                name = "Người dùng " + provider;
                logger.warn("Tên người dùng rỗng, đặt mặc định: {}", name);
            }
        }

        String username = email.split("@")[0]; // Lấy username từ phần trước @

        Optional<nhanvien> existingUser = nhanviendrepository.findByEmail(email);
        nhanvien user;
        if (existingUser.isEmpty()) {
            logger.info("Tài khoản không tồn tại, tạo mới cho: {}", email);
            nhanviendto newUser = new nhanviendto();
            newUser.setEmail(email);
            newUser.setTenNhanVien(name);
            newUser.setTrangThai("Hoạt động");
            newUser.setSoDienThoai("");
            newUser.setDiaChi("");
            newUser.setNgaysinh(null);
            newUser.setPasswold(passwordEncoder.encode(UUID.randomUUID().toString().substring(0, 8)));
            newUser.setChucVuId(10002L);

            user = userservice.register(newUser);
            logger.info("Đã tạo tài khoản {} cho: {}", provider, email);
        } else {
            user = existingUser.get();
            logger.info("Tài khoản {} đã tồn tại: {}", provider, email);
        }

        // Lưu vào session
        session.setAttribute("username", username);
        session.setAttribute("currentUser", convertToDTO(user));
        session.setAttribute("currentUserId", user.getChucVu() != null ? user.getChucVu().getId() : null);
        session.setAttribute("currentUserRole", user.getChucVu() != null ? user.getChucVu().getTenChucVu() : "Không xác định");

        logger.info("Đăng nhập thành công cho: {} với username: {}", email, username);
        return new DefaultOAuth2User(Collections.emptyList(), attributes, "email");
    }

    private nhanviendto convertToDTO(nhanvien user) {
        return nhanviendto.builder()
                .id(user.getId())
                .tenNhanVien(user.getTenNhanVien())
                .email(user.getEmail())
                .soDienThoai(user.getSoDienThoai())
                .diaChi(user.getDiaChi())
                .chucVuId(user.getChucVu() != null ? user.getChucVu().getId() : null)
                .passwold(user.getPasswold())
                .avatar(user.getAvatar())
                .build();
    }
}