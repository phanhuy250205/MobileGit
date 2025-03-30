package com.example.assignment_java5.service.impl;

import com.example.assignment_java5.model.BlogImage;
import com.example.assignment_java5.model.blog;
import com.example.assignment_java5.repository.BlogImageRepository;
import com.example.assignment_java5.repository.BlogRepository;
import com.example.assignment_java5.service.BlogImageService;
import com.example.assignment_java5.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class BlogImageServiceImpl implements BlogImageService {

    private static final Logger logger = LoggerFactory.getLogger(BlogImageServiceImpl.class);

    @Autowired
    private BlogImageRepository blogImageRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    public BlogImage saveBlogImage(BlogImage blogImage) {
        logger.info("Lưu thông tin ảnh blog: {}", blogImage.getMoTa());
        return blogImageRepository.save(blogImage);
    }

    @Override
    public BlogImage uploadAndSaveImage(MultipartFile file, Long blogId, String moTa) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("❌ File ảnh không được để trống!");
        }
        blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("❌ Không tìm thấy blog với ID: " + blogId));
        String subFolder = "blog_images";
        String filePath = fileUploadService.uploadFile(file, subFolder);
        System.out.println("Lưu ảnh: " + filePath + " cho blog ID: " + blogId);

        BlogImage blogImage = BlogImage.builder()
                .duongDanAnh(filePath)
                .blog(blog)
                .moTa(moTa)
                .trangThai("Hoạt động")
                .build();
        BlogImage savedImage = blogImageRepository.save(blogImage);
        System.out.println("Đã lưu ảnh vào DB với ID: " + savedImage.getId());
        return savedImage;
    }
    @Override
    public BlogImage findById(Long id) {
        return  blogImageRepository.findById(id).orElse(null);
    }


    @Override
    public List<BlogImage> findByBlogId(Long blogId) {
        return blogImageRepository.findByBlogId(blogId);
    }

    @Override
    public void deleteBlogImage(Long id) {
        Optional<BlogImage> optionalBlogImage = blogImageRepository.findById(id);
        if (optionalBlogImage.isPresent()) {
            BlogImage blogImage = optionalBlogImage.get();
            try {
                fileUploadService.deleteFile(blogImage.getDuongDanAnh());
                logger.info("Xóa file ảnh thành công: {}", blogImage.getDuongDanAnh());
            } catch (Exception e) {
                logger.warn("Không thể xóa file ảnh: {}. Tiếp tục xóa bản ghi DB.", blogImage.getDuongDanAnh());
            }
            blogImageRepository.deleteById(id);
            logger.info("Xóa ảnh blog ID: {} thành công", id);
        } else {
            logger.warn("Không tìm thấy ảnh blog với ID: {}", id);
        }
    }

    @Override
    public void updateTrangThai(Long id, String trangThai) {
        Optional<BlogImage> optionalBlogImage = blogImageRepository.findById(id);
        if (optionalBlogImage.isPresent()) {
            BlogImage blogImage = optionalBlogImage.get();
            blogImage.setTrangThai(trangThai);
            blogImageRepository.save(blogImage);
            logger.info("Cập nhật trạng thái ảnh blog ID: {} thành {}", id, trangThai);
        } else {
            logger.warn("Không tìm thấy ảnh blog với ID: {}", id);
        }
    }
}