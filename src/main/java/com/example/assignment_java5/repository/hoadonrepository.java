package com.example.assignment_java5.repository;

import com.example.assignment_java5.model.hoadon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface hoadonrepository extends JpaRepository<hoadon, Integer> {
}
