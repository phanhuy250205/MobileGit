package com.example.assignment_java5.Config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FacebookLogoutHandler implements LogoutHandler {

    private final RestTemplate restTemplate;

    public FacebookLogoutHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Nếu cần gọi API của Facebook để thu hồi token, bạn có thể thêm logic ở đây
        // Tuy nhiên, với Facebook, thường chỉ cần xóa session phía client là đủ
    }
}