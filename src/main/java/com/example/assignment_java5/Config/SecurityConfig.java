package com.example.assignment_java5.Config;

import com.example.assignment_java5.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain");
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Các URL công khai
                        .requestMatchers(
                                "/register",
                                "/login",
                                "/api/index/**",
                                "/api/check-login",
                                "/",
                                "/login/oauth2/code/google",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",
                                "/api/sanpham/**",
                                "/cart/**",
                                "/invoice/**",
                                "/send-otp",
                                "/verify-otp",
                                "/blogs/list", // Cho phép truy cập công khai
                                "/profile"
                        ).permitAll()
                        .requestMatchers("/dashboard").hasRole("USER")
                        // Chỉ người dùng có vai trò ADMIN được truy cập /blogs/**
                        .requestMatchers("/blogs/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Tất cả các yêu cầu khác phải đăng nhập
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .addLogoutHandler(googleLogoutHandler())
                        .logoutSuccessHandler((req, res, auth) -> {
                            logger.info("Logout successful for user: {}", auth != null ? auth.getName() : "unknown");
                            SecurityContextHolder.clearContext();
                            HttpSession session = req.getSession(false);
                            if (session != null) {
                                session.invalidate();
                                logger.debug("Session invalidated: {}", session.getId());
                            }
                            res.sendRedirect("/login?logout");
                        })
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/cart/add", "/send-otp", "/verify-otp")
                )
                .oauth2Client(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public GoogleLogoutHandler googleLogoutHandler() {
        return new GoogleLogoutHandler(restTemplate());
    }
}