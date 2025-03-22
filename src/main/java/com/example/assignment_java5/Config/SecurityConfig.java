package com.example.assignment_java5.Config;

import com.example.assignment_java5.Config.GoogleLogoutHandler;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.model.phanloaichucvu;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain");
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register", "/login", "/api/index/**", "/api/check-login", "/",
                                "/login/oauth2/code/google", "/css/**", "/js/**", "/images/**",
                                "/api/sanpham/**", "/cart/**", "/invoice/**","/send-otp","/verify-otp").permitAll()
                        .requestMatchers("/dashboard").hasRole("USER")
                        .anyRequest().authenticated()
                )
                // Tắt formLogin để controller xử lý
                .formLogin(form -> form.disable())
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
                        .ignoringRequestMatchers("/cart/add","/send-otp","/verify-otp")
                )
                .oauth2Client();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        logger.info("UserDetailsService bean initialized");
        return username -> {
            logger.debug("Attempting to load user by email: '{}'", username);
            try {
                String sql = "SELECT email, passwold, chuc_vu_id, trang_thai FROM nhan_vien WHERE email = ?";
                return jdbcTemplate.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> {
                    String dbEmail = rs.getString("email");
                    String dbPassword = rs.getString("passwold");
                    Long chucVuId = rs.getLong("chuc_vu_id");
                    String trangThai = rs.getString("trang_thai");

                    if (dbPassword == null) {
                        throw new UsernameNotFoundException("Password not set for user: " + dbEmail);
                    }

                    if ("Đã khóa".equalsIgnoreCase(trangThai)) {
                        throw new LockedException("⚠️ Tài khoản đã bị khóa!");
                    }

                    String springRole = chucVuId == 10002 ? "USER" : "ADMIN";
                    return User.withUsername(dbEmail)
                            .password(dbPassword)
                            .roles(springRole)
                            .build();
                });
            } catch (Exception e) {
                logger.error("Error loading user with email '{}': {}", username, e.getMessage());
                throw new UsernameNotFoundException("User not found with email: " + username);
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
       return  new BCryptPasswordEncoder();
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