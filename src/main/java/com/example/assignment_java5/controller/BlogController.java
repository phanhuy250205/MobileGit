package com.example.assignment_java5.controller;

import com.example.assignment_java5.Dto.BlogDTO;
import com.example.assignment_java5.model.blog;
import com.example.assignment_java5.model.nhanvien;
import com.example.assignment_java5.service.BlogImageService;
import com.example.assignment_java5.service.BlogService;
import com.example.assignment_java5.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private BlogImageService blogImageService;

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
        blogDTO.setAnhDaiDien(blog.getAnhDaiDien());
        blogDTO.setTags(blog.getTags());
        nhanvien nv = blog.getNhanVien();
        if (nv != null) {
            blogDTO.setNhanVienTen(nv.getTenNhanVien());
            blogDTO.setNhanVienAvatar(nv.getAvatar());
        }
        // Thêm danh sách ảnh
        List<com.example.assignment_java5.model.BlogImage> images = blogImageService.findByBlogId(blog.getId());
        blogDTO.setImages(images);
        return blogDTO;
    }

    // Chuyển từ BlogDTO sang Blog
    private blog convertToEntity(BlogDTO blogDTO) {
        blog blog = new blog();
        blog.setId(blogDTO.getId());
        blog.setTieuDe(blogDTO.getTieuDe());
        blog.setNoiDung(blogDTO.getNoiDung());
        blog.setTrangThai(blogDTO.getTrangThai());
        blog.setAnhDaiDien(blogDTO.getAnhDaiDien());
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
        model.addAttribute("blogs", blogDTOs);
        return "blog";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setNhanVienId(1L); // Giả sử ID nhân viên mặc định
        model.addAttribute("blog", blogDTO);
        return "uploadblog";
    }

    @PostMapping("/save")
    public String saveBlog(
            @ModelAttribute("blog") BlogDTO blogDTO,
            @RequestParam("imageFiles") MultipartFile[] imageFiles,
            @RequestParam(value = "saveDraft", required = false) String saveDraft,
            @RequestParam("tags") String tags,
            Model model) {
        try {
            blogDTO.setId(null);
            blog blog = convertToEntity(blogDTO);
            blog.setTags(tags);
            blog.setTrangThai(saveDraft != null ? "Bản nháp" : "Công khai");
            blog = blogService.save(blog);

            System.out.println("Số lượng file nhận được: " + imageFiles.length);
            if (imageFiles != null && imageFiles.length > 0) {
                for (int i = 0; i < imageFiles.length; i++) {
                    if (!imageFiles[i].isEmpty()) {
                        System.out.println("Đang xử lý ảnh " + (i + 1) + ": " + imageFiles[i].getOriginalFilename());
                        String moTa = "Ảnh " + (i + 1) + " cho bài viết: " + blogDTO.getTieuDe();
                        var blogImage = blogImageService.uploadAndSaveImage(imageFiles[i], blog.getId(), moTa);
                        if (i == 0 && blog.getAnhDaiDien() == null) {
                            blog.setAnhDaiDien(blogImage.getDuongDanAnh());
                        }
                    }
                }
                blogService.save(blog);
            }
            return "redirect:/blogs/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lưu blog: " + e.getMessage());
            return "uploadblog";
        }
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        blog blog = blogService.findById(id);
        if (blog == null) {
            return "redirect:/blogs/list";
        }
        BlogDTO blogDTO = convertToDTO(blog);
        model.addAttribute("blog", blogDTO);
        return "uploadblog";
    }

    @PostMapping("/update/{id}")
    public String updateBlog(
            @PathVariable Long id,
            @ModelAttribute("blog") BlogDTO blogDTO,
            @RequestParam("imageFiles") MultipartFile[] imageFiles,
            Model model) {
        try {
            blogDTO.setId(id);
            blog blog = convertToEntity(blogDTO);

            // Xử lý upload ảnh mới nếu có
            if (imageFiles != null && imageFiles.length > 0) {
                for (int i = 0; i < imageFiles.length; i++) {
                    if (!imageFiles[i].isEmpty()) {
                        String moTa = "Ảnh " + (i + 1) + " cập nhật cho bài viết: " + blogDTO.getTieuDe();
                        var blogImage = blogImageService.uploadAndSaveImage(imageFiles[i], id, moTa);
                        if (i == 0 && blog.getAnhDaiDien() == null) {
                            blog.setAnhDaiDien(blogImage.getDuongDanAnh());
                        }
                    }
                }
            }

            blogService.save(blog);
            return "redirect:/blogs/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi cập nhật blog: " + e.getMessage());
            return "uploadblog";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        blog blog = blogService.findById(id);
        if (blog != null) {
            List<com.example.assignment_java5.model.BlogImage> images = blogImageService.findByBlogId(id);
            images.forEach(image -> blogImageService.deleteBlogImage(image.getId()));
            blogService.deleteById(id);
        }
        return "redirect:/blogs/list";
    }

    @GetMapping("/nhanvien/{nhanVienId}")
    public String getBlogsByNhanVienId(@PathVariable Long nhanVienId, Model model) {
        List<blog> blogs = blogService.findByNhanVienId(nhanVienId);
        List<BlogDTO> blogDTOs = blogs.stream().map(this::convertToDTO).collect(Collectors.toList());
        model.addAttribute("blogs", blogDTOs);
        return "blog-list";
    }
    @GetMapping("/detail/{id}")
    public  String getBlogDetail(@PathVariable Long id , Model  model){
        blog Blog = blogService.findById(id);
        if (Blog == null){
            return "redirect:/blogs/list";
        }
        BlogDTO blogDTO = convertToDTO(Blog);
        model.addAttribute("blog", blogDTO);
        return "blogdetail";
    }
}