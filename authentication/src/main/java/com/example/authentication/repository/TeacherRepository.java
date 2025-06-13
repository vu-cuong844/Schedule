package com.example.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authentication.model.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String>{
    
}
