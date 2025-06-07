package com.example.authentication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.authentication.model.Teacher;
import com.example.authentication.repository.TeacherRepository;
import com.example.authentication.utils.GenerateID;

@Service
public class TeacherService {
    
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GenerateID generateID;

    private final List<String> priority_tn = java.util.Arrays.asList("TS", "GVN", "ThS");
    private final List<String> priority_gd = java.util.Arrays.asList("PGS", "ThS", "TS");


    public Teacher addNewTeacher(Teacher teacher){
        String hoc_vi = teacher.getHoc_vi();

        String teacherCode = generateID.genIdFor(teacher);
        if (teacherCode == null){
            return null;
        }

        int index_tn = priority_tn.indexOf(hoc_vi) + 1;
        int index_gd = priority_gd.indexOf(hoc_vi) + 1;

        teacher.setPriority_tn(index_tn);
        teacher.setPriority_gd(index_gd);
        teacher.setTeacherCode(teacherCode);
        try {
            System.out.println("Thực thiện thêm " + teacher.getName());
            return teacherRepository.save(teacher);
        } catch (Exception e) {
            System.out.println("Lỗi " + e.getMessage());
            return null;
        }
    }
}
