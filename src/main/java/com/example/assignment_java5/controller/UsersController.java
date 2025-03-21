package com.example.assignment_java5.controller;

import com.example.assignment_java5.Dto.LoginDTO;
import com.example.assignment_java5.Dto.nhanviendto;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.repository.nhanvienrepository;
import com.example.assignment_java5.repository.phanloaichucvurepository;
import com.example.assignment_java5.service.FileUploadService;
import com.example.assignment_java5.service.Userservice;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.Enumeration;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Controller
public class UsersController {
    @Autowired
    private Userservice userservice;
    @Autowired
    private nhanvienrepository nhanviendrepository;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private phanloaichucvurepository phanloaichucvurepository;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/")
    public String home() {
        return "redirect:/api/index/";
    }

    // ✅ Trang đăng ký
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("nhanviendto", new nhanviendto());
        model.addAttribute("roleList", phanloaichucvurepository.findAll());
        return "signup";
    }

    @PostMapping("/send-otp")
    @ResponseBody
    public String sendOtp(@RequestParam String email, HttpSession session) {
        Optional<nhanvien> existingUser = nhanviendrepository.findByEmail(email);
        if (existingUser.isPresent()) {
            System.out.println("Email đã tồn tại: " + email);
            return "error:Email Đã Tồn Tại";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("generatedOtp", otp);
        session.setAttribute("emailForOtp", email);
        session.setAttribute("otpGeneratedTime", System.currentTimeMillis());
        System.out.println("OTP generated: " + otp);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("TechZone <phanhuy250205@gmail.com>");
            message.setTo(email);
            message.setReplyTo("phanhuy250205@gmail.com");
            message.setSubject("Mã OTP Xác Minh Đăng Ký - TechZone");
            message.setText(
                    "Xin chào,\n\n" +
                            "Cảm ơn bạn đã đăng ký tại TechZone!\n" +
                            "Mã OTP của bạn là: " + otp + "\n" +
                            "Mã này có hiệu lực trong 5 phút. Vui lòng nhập mã để hoàn tất đăng ký.\n\n" +
                            "Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email.\n\n" +
                            "Trân trọng,\n" +
                            "Đội ngũ TechZone\n" +
                            "Email: phanhuy250205@gmail.com"
            );
            mailSender.send(message);
            System.out.println("OTP sent to: " + email);
            return "success:OTP đã được gửi đến email của bạn!";
        } catch (Exception e) {
            e.printStackTrace();
            return "error:Lỗi gửi OTP - " + e.getMessage();
        }
    }

    @PostMapping("/verify-otp")
    @ResponseBody
    public String verifyOtp(@RequestParam String otp, HttpSession session) {
        String generatedOtp = (String) session.getAttribute("generatedOtp");
        Long otpGeneratedTime = (Long) session.getAttribute("otpGeneratedTime");

        long currentTime = System.currentTimeMillis();
        long timeElapsed = (otpGeneratedTime != null) ? (currentTime - otpGeneratedTime) / 1000 : Long.MAX_VALUE;

        System.out.println("OTP nhập vào: " + otp);
        if (generatedOtp != null && generatedOtp.equals(otp) && timeElapsed <= 300) {
            return "success:OTP hợp lệ!";
        }
        return "error:OTP không đúng hoặc đã hết hạn!";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("nhanviendto") nhanviendto nhanviendto,
                               BindingResult result, @RequestParam String otp,
                               Model model, HttpSession session) {
        model.addAttribute("roleList", phanloaichucvurepository.findAll());

        if (result.hasErrors()) {
            return "signup";
        }

        Optional<nhanvien> existingUser = nhanviendrepository.findByEmail(nhanviendto.getEmail());
        if (existingUser.isPresent()) {
            model.addAttribute("emailError", "❌ Email đã tồn tại! Vui lòng chọn email khác.");
            return "signup";
        }

        if (!nhanviendto.getPasswold().equals(nhanviendto.getConfirmPassword())) {
            model.addAttribute("passwordError", "❌ Mật khẩu và xác nhận mật khẩu không khớp.");
            return "signup";
        }

        if (!nhanviendto.isTermsAccepted()) {
            model.addAttribute("termsError", "⚠️ Bạn phải đồng ý với điều khoản sử dụng.");
            return "signup";
        }

        String generatedOtp = (String) session.getAttribute("generatedOtp");
        String emailForOtp = (String) session.getAttribute("emailForOtp");
        Long otpGeneratedTime = (Long) session.getAttribute("otpGeneratedTime");

        long currentTime = System.currentTimeMillis();
        long timeElapsed = (otpGeneratedTime != null) ? (currentTime - otpGeneratedTime) / 1000 : Long.MAX_VALUE;

        if (generatedOtp == null || !generatedOtp.equals(otp) || !nhanviendto.getEmail().equals(emailForOtp) || timeElapsed > 300) {
            model.addAttribute("otpError", "❌ Mã OTP không hợp lệ hoặc đã hết hạn!");
            return "signup";
        }

        nhanviendto.setTrangThai("Hoạt động");
        nhanviendto.setChucVuId(10002L);

        nhanvien newUser = userservice.register(nhanviendto);

        // Xóa OTP khỏi session
        session.removeAttribute("generatedOtp");
        session.removeAttribute("emailForOtp");
        session.removeAttribute("otpGeneratedTime");

        return "redirect:/login?success";
    }

    @GetMapping("/api/check-login")
    public String checkLogin(HttpSession session) {
        String username = (String) session.getAttribute("username");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "Đăng nhập thành công: " + (username != null ? username : authentication.getName());
        } else {
            return "Chưa đăng nhập";
        }
    }

    @GetMapping("/login/oauth2/code/google")
    public String googleCallback(Authentication authentication, Model model, HttpServletRequest request) {
        System.out.println("=== Đã vào endpoint /login/oauth2/code/google ===");
        System.out.println("Request received: " + request);

        if (authentication == null) {
            System.out.println("Lỗi: Authentication là null! Có thể do redirect URI hoặc cấu hình OAuth2 không đúng.");
            String requestUri = (request != null) ? request.getRequestURI() : "Request is null";
            String queryString = (request != null) ? request.getQueryString() : "Query String is null";
            String requestMethod = (request != null) ? request.getMethod() : "Method is null";

            System.out.println("Request URI: " + requestUri);
            System.out.println("Query String: " + queryString);
            System.out.println("Request Method: " + requestMethod);

            if (request != null) {
                System.out.println("Request Headers:");
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        String headerName = headerNames.nextElement();
                        String headerValue = request.getHeader(headerName);
                        System.out.println(headerName + ": " + headerValue);
                    }
                } else {
                    System.out.println("Header names is null");
                }
            } else {
                System.out.println("Request object is null, cannot retrieve headers");
            }
            return "redirect:/login?error=authentication_failed";
        }

        OAuth2AuthenticationToken oauthToken = null;
        if (authentication instanceof OAuth2AuthenticationToken) {
            oauthToken = (OAuth2AuthenticationToken) authentication;
        } else {
            System.out.println("Authentication không phải là OAuth2AuthenticationToken: " + authentication.getClass().getName());
            SecurityContextHolder.clearContext();
            request.getSession().invalidate();
            return "redirect:/oauth2/authorization/google";
        }

        System.out.println("=== Kiểm tra principal ===");
        if (oauthToken.getPrincipal() == null) {
            System.out.println("Lỗi: Principal là null! Token: " + oauthToken + " - Kiểm tra scope hoặc API.");
            return "redirect:/login?error=missing_principal";
        }

        System.out.println("Dữ liệu từ Google: " + oauthToken.getPrincipal().getAttributes());

        String email = oauthToken.getPrincipal().getAttribute("email");
        String givenName = oauthToken.getPrincipal().getAttribute("given_name");
        String familyName = oauthToken.getPrincipal().getAttribute("family_name");

        System.out.println("Email lấy từ Google: " + email);
        System.out.println("Given Name: " + givenName);
        System.out.println("Family Name: " + familyName);

        String name = (givenName != null ? givenName : "") + " " + (familyName != null ? familyName : "");
        if (name.trim().isEmpty()) {
            name = "Người dùng Google";
            System.out.println("Tên người dùng rỗng, đặt mặc định: " + name);
        }

        if (email == null || email.isEmpty()) {
            System.out.println("Lỗi: Không lấy được email từ Google! Dữ liệu: " + oauthToken.getPrincipal().getAttributes());
            return "redirect:/login?error=missing_email";
        }

        System.out.println("Thông tin người dùng: Email=" + email + ", Name=" + name);

        String username = email.split("@")[0];

        System.out.println("=== Kiểm tra người dùng trong cơ sở dữ liệu ===");
        Optional<nhanvien> existingUser = nhanviendrepository.findByEmail(email);
        System.out.println("Kết quả kiểm tra: " + (existingUser.isPresent() ? "Người dùng đã tồn tại" : "Người dùng chưa tồn tại"));

        nhanvien user;
        if (existingUser.isEmpty()) {
            System.out.println("Tài khoản không tồn tại, tạo mới cho: " + email);
            nhanviendto newUser = new nhanviendto();
            newUser.setEmail(email);
            newUser.setTenNhanVien(name);
            newUser.setTrangThai("Hoạt động");
            newUser.setSoDienThoai("");
            newUser.setDiaChi("");
            newUser.setNgaysinh(null);

            String randomPassword = UUID.randomUUID().toString().substring(0, 8);
            newUser.setPasswold(passwordEncoder.encode(randomPassword));
            newUser.setChucVuId(10002L);

            try {
                System.out.println("=== Bắt đầu lưu người dùng vào cơ sở dữ liệu ===");
                System.out.println("Thông tin người dùng sẽ lưu: " + newUser.toString());
                user = userservice.register(newUser);
                System.out.println("Đã tạo tài khoản Google cho: " + email + " với mật khẩu mã hóa: " + newUser.getPasswold());
            } catch (Exception e) {
                System.out.println("Lỗi khi lưu vào cơ sở dữ liệu: " + e.getMessage());
                e.printStackTrace();
                return "redirect:/login?error=database_save_failed";
            }
        } else {
            user = existingUser.get();
            System.out.println("Tài khoản Google đã tồn tại: " + email);
        }

        if (user == null) {
            System.out.println("Lỗi: Không lấy được user từ cơ sở dữ liệu.");
            return "redirect:/login?error=user_not_found";
        }

        try {
            System.out.println("Thực hiện đăng nhập tự động cho: " + email);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
            session.setAttribute("username", username);
            session.setAttribute("currentUser", convertToDTO(user));
            session.setAttribute("currentUserId", user.getChucVu() != null ? user.getChucVu().getId() : null);
            session.setAttribute("currentUserRole", user.getChucVu() != null ? user.getChucVu().getTenChucVu() : "Không xác định");
            System.out.println("Đã lưu username vào session: " + username);

            System.out.println("Đăng nhập tự động thành công cho: " + email);
            return "redirect:/api/index/";
        } catch (Exception e) {
            System.out.println("Lỗi khi đăng nhập tự động: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/login?google_auth_success=true";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến trang dashboard!");
        return "dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Object currentUserObject = session.getAttribute("currentUser");

        if (currentUserObject == null) {
            return "redirect:/login";
        }

        nhanvien currentUser;
        if (currentUserObject instanceof nhanviendto) {
            nhanviendto currentUserDTO = (nhanviendto) currentUserObject;
            currentUser = userservice.getById(currentUserDTO.getId());
        } else if (currentUserObject instanceof nhanvien) {
            currentUser = (nhanvien) currentUserObject;
        } else {
            return "redirect:/login";
        }

        System.out.println("🟢 Avatar lấy từ database sau cập nhật: " + currentUser.getAvatar());

        model.addAttribute("updatedUser", currentUser);
        return "profile";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
                            BindingResult result,
                            Model model,
                            HttpSession session) {
        if (result.hasErrors()) {
            return "login";
        }

        Optional<nhanvien> nhanVienOpt = nhanviendrepository.findByEmail(loginDTO.getEmail());

        if (nhanVienOpt.isEmpty()) {
            model.addAttribute("error", "❌ Email không tồn tại!");
            return "login";
        }

        nhanvien nhanVien = nhanVienOpt.get();

        if (!passwordEncoder.matches(loginDTO.getPasswold(), nhanVien.getPasswold())) {
            model.addAttribute("error", "❌ Mật khẩu không đúng!");
            return "login";
        }

        if ("Đã khóa".equalsIgnoreCase(nhanVien.getTrangThai())) {
            model.addAttribute("error", "⚠️ Tài khoản đã bị khóa!");
            return "login";
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
            session.setAttribute("username", nhanVien.getTenNhanVien());
            session.setAttribute("currentUser", nhanVien);
            session.setAttribute("currentUserId", nhanVien.getChucVu().getId());
            session.setAttribute("currentUserRole", nhanVien.getChucVu() != null ? nhanVien.getChucVu().getTenChucVu() : "Không xác định");

            return "redirect:/api/index/";
        } catch (Exception e) {
            model.addAttribute("error", "❌ Lỗi đăng nhập: " + e.getMessage());
            return "login";
        }
    }

    @PostMapping("/update")
    public String updateUser(
            @ModelAttribute("nhanviendto") nhanviendto nhanviendto,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            Model model, HttpSession session) {

        if (nhanviendto.getId() == null) {
            model.addAttribute("error", "ID không hợp lệ");
            return "profile";
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Mật khẩu không khớp!");
                return "profile";
            }
            nhanviendto.setPasswold(passwordEncoder.encode(newPassword));
        }

        try {
            nhanvien updatedUser = userservice.update(nhanviendto, avatarFile, newPassword);

            System.out.println("🟢 Avatar lấy từ database sau cập nhật: " + updatedUser.getAvatar());

            session.setAttribute("username", updatedUser.getTenNhanVien());
            session.setAttribute("currentUser", convertToDTO(updatedUser));

            model.addAttribute("success", "Cập nhật thông tin thành công!");
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "profile";
        }
    }

    private nhanvien convertToModel(nhanviendto dto) {
        if (dto == null) return null;

        nhanvien nv = new nhanvien();
        nv.setId(dto.getId());
        nv.setTenNhanVien(dto.getTenNhanVien());
        nv.setEmail(dto.getEmail());
        nv.setSoDienThoai(dto.getSoDienThoai());
        nv.setDiaChi(dto.getDiaChi());
        nv.setPasswold(dto.getPasswold());

        return nv;
    }

    private nhanviendto convertToDTO(nhanvien model) {
        return nhanviendto.builder()
                .id(model.getId())
                .tenNhanVien(model.getTenNhanVien())
                .email(model.getEmail())
                .soDienThoai(model.getSoDienThoai())
                .diaChi(model.getDiaChi())
                .chucVuId(model.getChucVu() != null ? model.getChucVu().getId() : null)
                .passwold(model.getPasswold())
                .avatar(model.getAvatar())
                .build();
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/logout";
    }
}