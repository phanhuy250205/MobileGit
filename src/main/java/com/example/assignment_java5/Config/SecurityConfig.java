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
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
                                "/api/sanpham/**","/cart/**","/invoice/**").permitAll()
                        .requestMatchers("/dashboard").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("passwold")
                        .defaultSuccessUrl("/api/index/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                        .successHandler((req, res, auth) -> {
                            logger.info("Login successful for user: {}", auth.getName());
                            String email = auth.getName();
                            String sql = "SELECT id, email, ten_nhan_vien, chuc_vu_id FROM nhan_vien WHERE email = ?";
                            nhanvien currentUser = jdbcTemplate.queryForObject(sql, new Object[]{email}, (rs, rowNum) -> {
                                nhanvien nv = new nhanvien();
                                nv.setId(rs.getLong("id")); // Thêm ID
                                nv.setEmail(rs.getString("email"));
                                nv.setTenNhanVien(rs.getString("ten_nhan_vien"));
                                phanloaichucvu cv = new phanloaichucvu();
                                cv.setId(rs.getLong("chuc_vu_id"));
                                nv.setChucVu(cv);
                                return nv;
                            });
                            req.getSession().setAttribute("currentUser", currentUser);
                            req.getSession().setAttribute("username", currentUser.getTenNhanVien() != null ? currentUser.getTenNhanVien() : email);
                            res.sendRedirect("/api/index/");
                        })
                        .failureHandler((req, res, ex) -> {
                            String email = req.getParameter("email");
                            String passwold = req.getParameter("passwold");
                            logger.error("Login failed - email: '{}', passwold: '{}', error: {}", email, passwold, ex.getMessage());
                            res.sendRedirect("/login?error");
                        })
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
                        .ignoringRequestMatchers("/cart/add") // Tắt CSRF cho /cart/add
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
                String sql = "SELECT email, passwold, chuc_vu_id FROM nhan_vien WHERE email = ?";
                return jdbcTemplate.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> {
                    String dbEmail = rs.getString("email");
                    String dbPassword = rs.getString("passwold");
                    Long chucVuId = rs.getLong("chuc_vu_id");

                    if (dbPassword == null) {
                        logger.error("Password is NULL for email: {}", dbEmail);
                        throw new UsernameNotFoundException("Password not set for user: " + dbEmail);
                    }

                    logger.debug("Found user: email={}, passwold={}, chuc_vu_id={}", dbEmail, dbPassword, chucVuId);

                    String springRole = chucVuId == 10002 ? "USER" : "ADMIN";
                    logger.debug("Mapped Spring Security role: {}", springRole);

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
        return NoOpPasswordEncoder.getInstance();
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