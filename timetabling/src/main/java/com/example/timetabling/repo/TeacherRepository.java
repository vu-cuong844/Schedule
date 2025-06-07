package com.example.timetabling.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.timetabling.model.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
    
}
