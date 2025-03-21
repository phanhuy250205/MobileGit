package com.example.assignment_java5.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @NotBlank(message = "Email Không được để trống")
    @Email(message = "Email Không hợp lệ")
    private  String email;

    @NotBlank(message = "Mật Khẩu Không được để trống")
    @Size(min = 6, message = "❌ Mật khẩu phải có ít nhất 6 ký tự!")
    private  String passwold;
}
