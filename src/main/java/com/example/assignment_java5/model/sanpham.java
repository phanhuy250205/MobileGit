package com.example.assignment_java5.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class sanpham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "ten_san_pham")
    private String tensanpham;

    @Column(name = "mo_ta")
    private String mota;

    @Column(nullable = false)
    private BigDecimal gia;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

//    private String anh;
    private String thuongHieu;
    private String model;
    private String cauHinh;
    private String trangThai = "Còn hàng";
    private BigDecimal giamGia = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "phan_loai_id")  // Cột này phải khớp với DB
    private phanloaihang phanLoaiHang;

    // 🆕 Thêm quan hệ OneToMany để lưu nhiều ảnh sản phẩm
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HinhAnhSanPham> danhSachHinhAnh;
    @Transient
    private String formattedPrice; // Thêm trường để lưu giá đã định dạng

    @Transient  // Không lưu vào DB, chỉ dùng cho view
    public String getAnhDauTien() {
        if (danhSachHinhAnh != null && !danhSachHinhAnh.isEmpty()) {
            return danhSachHinhAnh.get(0).getUrlHinhAnh();  // Lấy ảnh đầu tiên
        }
        return "/img/no-image.jpg"; // Nếu không có ảnh, dùng ảnh mặc định
    }

}
