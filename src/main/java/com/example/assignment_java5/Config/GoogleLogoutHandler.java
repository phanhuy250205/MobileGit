package com.example.assignment_java5.Config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GoogleLogoutHandler implements LogoutHandler {

    private final RestTemplate restTemplate;

    @Autowired
    public GoogleLogoutHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String accessToken = oauthToken.getPrincipal().getAttribute("access_token");
            if (accessToken != null && !accessToken.isEmpty()) {
                String revokeUrl = "https://accounts.google.com/o/oauth2/revoke?token=" + accessToken;
                try {
                    String result = restTemplate.getForObject(revokeUrl, String.class);
                    System.out.println("Google token revoked successfully: " + result);
                } catch (Exception e) {
                    System.err.println("Error revoking Google token: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("No access token found to revoke.");
            }
        } else {
            System.out.println("Authentication is not an OAuth2AuthenticationToken: " + (authentication != null ? authentication.getClass().getName() : "null"));
        }
    }
}