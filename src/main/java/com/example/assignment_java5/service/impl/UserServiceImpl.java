package com.example.assignment_java5.service.impl;

import com.example.assignment_java5.Dto.nhanviendto;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.model.phanloaichucvu;
import com.example.assignment_java5.repository.nhanvienrepository;
import com.example.assignment_java5.repository.phanloaichucvurepository;
import com.example.assignment_java5.service.FileUploadService;
import com.example.assignment_java5.service.Userservice;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.Role;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
class UserserviceImpl implements Userservice {

    @Autowired
    private nhanvienrepository nhanvienRepository;

    @Autowired
    private phanloaichucvurepository phanloaichucvurepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    public nhanvien register(nhanviendto nhanVienDTO) {
        System.out.println("=== Bắt đầu lưu NhanVien vào cơ sở dữ liệu ===");
        System.out.println("Email: " + nhanVienDTO.getEmail());
        System.out.println("Tên: " + nhanVienDTO.getTenNhanVien());

        nhanvien nhanVien = new nhanvien();
        nhanVien.setTenNhanVien(nhanVienDTO.getTenNhanVien());
        nhanVien.setEmail(nhanVienDTO.getEmail());
        nhanVien.setSoDienThoai(nhanVienDTO.getSoDienThoai());
        nhanVien.setDiaChi(nhanVienDTO.getDiaChi());
        nhanVien.setNgaysinh(nhanVienDTO.getNgaysinh());
        nhanVien.setPasswold(nhanVienDTO.getPasswold());
        nhanVien.setTrangThai(nhanVienDTO.getTrangThai());
        nhanVien.setAvatar(nhanVienDTO.getAvatar());

        if (nhanVienDTO.getChucVuId() != null) {
            System.out.println("=== Kiểm tra chức vụ với ID: " + nhanVienDTO.getChucVuId());
            Optional<phanloaichucvu> chucVuOptional = phanloaichucvurepository.findById(nhanVienDTO.getChucVuId());
            if (chucVuOptional.isPresent()) {
                nhanVien.setChucVu(chucVuOptional.get());
                System.out.println("Đã gán chức vụ với ID: " + nhanVienDTO.getChucVuId());
            } else {
                System.out.println("Không tìm thấy chức vụ với ID: " + nhanVienDTO.getChucVuId());
                throw new RuntimeException("Không tìm thấy chức vụ với ID: " + nhanVienDTO.getChucVuId());
            }
        }

        System.out.println("=== Gọi nhanVienRepository.save ===");
        nhanvien savedUser = nhanvienRepository.save(nhanVien);
        System.out.println("Đã lưu NhanVien vào cơ sở dữ liệu với ID: " + savedUser.getId());
        return savedUser;
    }


    @Override
    public Optional<nhanviendto> login(String email, String password) {
        Optional<nhanvien> nhanvien = nhanvienRepository.findByEmail(email);

        if (nhanvien.isPresent()) {
            // Kiểm tra mật khẩu so với mật khẩu trong cơ sở dữ liệu (không mã hóa)
            if (password.equals(nhanvien.get().getPasswold())) {
                nhanviendto dto = new nhanviendto();
                dto.setId(nhanvien.get().getId());
                dto.setTenNhanVien(nhanvien.get().getTenNhanVien());
                dto.setEmail(nhanvien.get().getEmail());
                dto.setSoDienThoai(nhanvien.get().getSoDienThoai());
                dto.setDiaChi(nhanvien.get().getDiaChi());
                dto.setChucVuId(nhanvien.get().getChucVu().getId());

                return Optional.of(dto);
            }
        }
        return Optional.empty();  // Trả về nếu đăng nhập thất bại
    }

    @Override
    public nhanvien update(nhanviendto nhanviendto, MultipartFile avatarFile, String newPassword) {
        nhanvien nhanvien = nhanvienRepository.findById(nhanviendto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại"));

        // 🔹 Cập nhật thông tin cơ bản
        nhanvien.setTenNhanVien(nhanviendto.getTenNhanVien());
        nhanvien.setEmail(nhanviendto.getEmail());
        nhanvien.setSoDienThoai(nhanviendto.getSoDienThoai());
        nhanvien.setDiaChi(nhanviendto.getDiaChi());
        nhanvien.setNgaysinh(nhanviendto.getNgaysinh());

        // 🔹 Cập nhật mật khẩu nếu có nhập mới
        if (newPassword != null && !newPassword.isEmpty()) {
            nhanvien.setPasswold(newPassword);
        }

        // 🔹 Nếu có file ảnh mới được upload
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String avatarFileName = fileUploadService.uploadFile(avatarFile, "avatars");
                System.out.println("🟢 Đường dẫn ảnh mới: " + avatarFileName);

                // 🔹 Lưu đường dẫn avatar vào database
                nhanvien.setAvatar(avatarFileName);
                System.out.println("🟢 Avatar đã cập nhật trong SQL: " + nhanvien.getAvatar());

            } catch (IOException e) {
                throw new RuntimeException("Lỗi tải lên avatar!", e);
            }
        }

        // 🔹 Lưu cập nhật vào database
        nhanvien updatedUser = nhanvienRepository.save(nhanvien);
        System.out.println("🟢 Dữ liệu đã được lưu vào SQL với avatar: " + updatedUser.getAvatar());

        return updatedUser;
    }


    @Override
    public nhanvien getCurrentUser(HttpSession session) {
        return null;
    }
    @Override
    public void deleteUser(Long id) {
        if (!nhanvienRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy nhân viên với ID: " + id);
        }
        nhanvienRepository.deleteById(id);
    }

    @Override
    public nhanvien getById(Long id) {
        return nhanvienRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + id));
    }
    public Optional<nhanvien> findByEmail(String email) {
        System.out.println("Tìm kiếm NhanVien với email: " + email);
        Optional<nhanvien> user = nhanvienRepository.findByEmail(email);
        if (user.isPresent()) {
            System.out.println("Tìm thấy NhanVien với email: " + email);
        } else {
            System.out.println("Không tìm thấy NhanVien với email: " + email);
        }
        return user;
    }











}
