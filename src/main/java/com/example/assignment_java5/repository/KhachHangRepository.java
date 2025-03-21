package com.example.assignment_java5.repository;

import com.example.assignment_java5.Dto.KhachHangDTO;

import com.example.assignment_java5.model.nhanvien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface KhachHangRepository extends JpaRepository<nhanvien, Long> {

    @Query(value = """
        SELECT 
            nv.id AS maKhachHang,
            nv.ten_nhan_vien AS tenKhachHang,
            nv.email AS email,
            nv.so_dien_thoai AS soDienThoai,
            COUNT(dh.id) AS soDonHang,
            COALESCE(SUM(ctdh.so_luong * ctdh.gia), 0) AS tongChiTieu,
            nv.trang_thai AS trangThai
        FROM nhan_vien nv
        LEFT JOIN don_hang dh ON nv.id = dh.nhan_vien_id
        LEFT JOIN chi_tiet_don_hang ctdh ON dh.id = ctdh.don_hang_id
        WHERE nv.chuc_vu_id = 10002
        GROUP BY nv.id, nv.ten_nhan_vien, nv.email, nv.so_dien_thoai, nv.trang_thai
        ORDER BY nv.id DESC;
    """, nativeQuery = true)
    List<KhachHangDTO> getDanhSachKhachHang();

    @Modifying
    @Transactional
    @Query("UPDATE nhanvien nv SET nv.trangThai = :trangThai WHERE nv.id = :id")
    void updateTrangThai(Long id, String trangThai);


    @Query(value = """
    SELECT 
        nv.id AS maKhachHang,
        nv.ten_nhan_vien AS tenKhachHang,
        nv.email AS email,
        nv.so_dien_thoai AS soDienThoai,
        COUNT(dh.id) AS soDonHang,
        COALESCE(SUM(ctdh.so_luong * ctdh.gia), 0) AS tongChiTieu,
        nv.trang_thai AS trangThai
    FROM nhan_vien nv
    LEFT JOIN don_hang dh ON nv.id = dh.nhan_vien_id
    LEFT JOIN chi_tiet_don_hang ctdh ON dh.id = ctdh.don_hang_id
    WHERE nv.id = :id
    GROUP BY nv.id, nv.ten_nhan_vien, nv.email, nv.so_dien_thoai, nv.trang_thai
""", nativeQuery = true)
    KhachHangDTO findKhachHangById(@Param("id") Long id);

}
