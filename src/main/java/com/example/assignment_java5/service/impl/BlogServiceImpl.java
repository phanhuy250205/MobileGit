package com.example.assignment_java5.service.impl;

import com.example.assignment_java5.model.blog;
import com.example.assignment_java5.repository.BlogRepository;
import com.example.assignment_java5.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public List<blog> findAll() {
       return blogRepository.findAll();
    }

    @Override
    public blog findById(Long id) {
        Optional<blog> blog = blogRepository.findById(id);
        return blog.orElse(null);
    }

    @Override
    public blog save(blog Blog) {
        return blogRepository.save(Blog); // Tự động tạo hoặc cập nhật
    }

    @Override
    public void deleteById(Long id) {
        blogRepository.deleteById(id);

    }

    @Override
    public List<blog> findByNhanVienId(Long nhanVienId) {
        return blogRepository.findAllByNhanVienId(nhanVienId);
    }

    @Override
    public List<blog> findByTrangThai(String trangThai) {
        return blogRepository.findByTrangThai(trangThai);
    }
}
