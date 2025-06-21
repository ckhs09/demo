package com.example.demo.repository;

import java.util.Optional;
import com.example.demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    boolean existsByStudentId(String studentId);
    Optional<Student> findByStudentId(String studentId);
}