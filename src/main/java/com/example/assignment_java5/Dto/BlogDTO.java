package com.example.assignment_java5.Dto;

import com.example.assignment_java5.model.BlogImage;

import java.time.LocalDateTime;
import java.util.List;

public class BlogDTO {
    private Long id;
    private String tieuDe;
    private String noiDung;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private String trangThai;
    private Long nhanVienId;
    private String anhDaiDien;
    private String tags;
    private List<BlogImage> images; // Thêm danh sách ảnh

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public LocalDateTime getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Long getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(Long nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getAnhDaiDien() { return anhDaiDien; }
    public void setAnhDaiDien(String anhDaiDien) { this.anhDaiDien = anhDaiDien; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public List<BlogImage> getImages() { return images; }
    public void setImages(List<BlogImage> images) { this.images = images; }
}