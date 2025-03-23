package com.example.assignment_java5.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogDTO {
    private  long id;
    private String tieuDe;
    private String noiDung;
    private LocalDateTime ngayTao;
    private  LocalDateTime ngayCapNhat;
    private String trangThai;
    private long nhanVienId;
    private String anhDaiDien; // Thêm trường này để lưu đường dẫn ảnh
    private String tags; // Hoặc List<String> nếu muốn xử lý dưới dạng danh sách

}
