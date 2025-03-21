package com.example.assignment_java5.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class nhanviendto {
    private Long id;
    @NotBlank(message = "❌ Họ và tên không được để trống!")
    private String tenNhanVien;
    @NotBlank(message = "❌ Email không được để trống!")
    @Email(message = "❌ Email không hợp lệ!")
    private String email;

    @NotBlank(message = "❌ Số điện thoại không được để trống!")
    private String soDienThoai;
    private String diaChi;
    private Long chucVuId;
    private LocalDate ngaysinh;

    @NotBlank(message = "❌ Mật khẩu không được để trống!")
    @Size(min = 6, message = "❌ Mật khẩu phải có ít nhất 6 ký tự!")
    private String passwold;

    @NotBlank(message = "❌ Xác nhận mật khẩu không được để trống!")
    private String confirmPassword; // Mật khẩu xác nhận (không lưu vào database)
    private boolean termsAccepted;
    private Long roleId;
    private String trangThai; // Trạng thái người dùng (Hoạt động, Đã khóa)
    private MultipartFile avatarFile; // Nhận file upload từ form
    private String avatar; // Lưu đường dẫn vào database (VARCHAR)
}
