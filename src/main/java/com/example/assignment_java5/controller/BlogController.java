package com.example.assignment_java5.controller;


import com.example.assignment_java5.Dto.BlogDTO;
import com.example.assignment_java5.model.blog;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.service.BlogService;
import com.example.assignment_java5.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller // Đổi thành @Controller để trả về view Thymeleaf
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private FileUploadService fileUploadService;

    // Chuyển từ Blog sang BlogDTO
    private BlogDTO convertToDTO(blog blog) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setId(blog.getId());
        blogDTO.setTieuDe(blog.getTieuDe());
        blogDTO.setNoiDung(blog.getNoiDung());
        blogDTO.setNgayTao(blog.getNgayTao());
        blogDTO.setNgayCapNhat(blog.getNgayCapNhat());
        blogDTO.setTrangThai(blog.getTrangThai());
        blogDTO.setNhanVienId(blog.getNhanVien().getId());
        blogDTO.setAnhDaiDien(blog.getAnhDaiDien()); // Thêm trường ảnh đại diện
        blogDTO.setTags(blog.getTags());
        return blogDTO;
    }

    // Chuyển từ BlogDTO sang Blog
    private blog convertToEntity(BlogDTO blogDTO) {
        blog blog = new blog();
        blog.setId(blogDTO.getId());
        blog.setTieuDe(blogDTO.getTieuDe());
        blog.setNoiDung(blogDTO.getNoiDung());
        blog.setTrangThai(blogDTO.getTrangThai());
        blog.setAnhDaiDien(blogDTO.getAnhDaiDien()); // Thêm trường ảnh đại diện
        blog.setTags(blogDTO.getTags());

        nhanvien nhanVien = new nhanvien();
        nhanVien.setId(blogDTO.getNhanVienId());
        blog.setNhanVien(nhanVien);
        return blog;
    }

    @GetMapping("/list")
    public String getAllBlogs(Model model) {
        List<blog> blogs = blogService.findAll();
        List<BlogDTO> blogDTOs = blogs.stream().map(this::convertToDTO).collect(Collectors.toList());
        blogDTOs.forEach(blog -> System.out.println("Ảnh đại diện: " + blog.getAnhDaiDien()));
        model.addAttribute("blogs", blogDTOs);
        return "blog";
    }
    @PreAuthorize("hasRole('ADMIN')")
    // Hiển thị form tạo blog (upload blog)
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setNhanVienId(1L); // Giả sử ID nhân viên mặc định, thay bằng logic thực tế (ví dụ: từ session)
        model.addAttribute("blog", blogDTO);
        return "uploadblog"; // Trả về template uploadblog.html
    }

    @PostMapping("/save")
    public String saveBlog(
            @ModelAttribute("blog") BlogDTO blogDTO,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "saveDraft", required = false) String saveDraft,
            @RequestParam("tags") String tags, // Thêm tham số này
            Model model) {
        try {
            if (!imageFile.isEmpty()) {
                String imagePath = fileUploadService.uploadFile(imageFile, "blogs");
                blogDTO.setAnhDaiDien(imagePath);
            }
            blogDTO.setTags(tags); // Gán tags vào DTO
            if (saveDraft != null) {
                blogDTO.setTrangThai("Bản nháp");
            } else {
                blogDTO.setTrangThai("Công khai");
            }
            blog blog = convertToEntity(blogDTO);
            blogService.save(blog);
            return "redirect:/blogs";
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
            return "uploadblog";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "uploadblog";
        }
    }
    // Hiển thị form chỉnh sửa blog
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        blog blog = blogService.findById(id);
        if (blog == null) {
            return "redirect:/blogs"; // Nếu không tìm thấy, quay lại danh sách
        }
        BlogDTO blogDTO = convertToDTO(blog);
        model.addAttribute("blog", blogDTO);
        return "blog-form"; // Trả về template blog-form.html
    }

    // Xử lý cập nhật blog
    @PostMapping("/update/{id}")
    public String updateBlog(@PathVariable Long id, @ModelAttribute("blog") BlogDTO blogDTO) {
        blogDTO.setId(id); // Đảm bảo ID không bị thay đổi
        blog blog = convertToEntity(blogDTO);
        blogService.save(blog);
        return "redirect:/blogs"; // Chuyển hướng về danh sách blog
    }

    // Xóa blog
    @GetMapping("/delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        blogService.deleteById(id);
        return "redirect:/blogs"; // Chuyển hướng về danh sách blog
    }

    // Hiển thị danh sách blog theo nhân viên
    @GetMapping("/nhanvien/{nhanVienId}")
    public String getBlogsByNhanVienId(@PathVariable Long nhanVienId, Model model) {
        List<blog> blogs = blogService.findByNhanVienId(nhanVienId);
        List<BlogDTO> blogDTOs = blogs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        model.addAttribute("blogs", blogDTOs);
        return "blog-list"; // Trả về template blog-list.html
    }
}