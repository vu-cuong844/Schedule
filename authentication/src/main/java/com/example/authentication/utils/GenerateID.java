package com.example.authentication.utils;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.authentication.model.Teacher;

@Component
public class GenerateID {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String genIdFor(Teacher teacher) {
        if (teacher.getTeacherCode() != null) {
            return teacher.getTeacherCode();
        }

        String institute = teacher.getInstitute();
        String department = teacher.getDepartment();

        String sql = "SELECT pre, institute_code, department_code, count " +
                "FROM generate_id WHERE institute = ? AND department = ?";

        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql, institute, department);

            jdbcTemplate.update("UPDATE generate_id SET count = count + 1 WHERE institute = ? AND department = ?",
                    institute, department);

            // Tạo mã mới
            String pre = (String) row.get("pre");
            String instituteCode = (String) row.get("institute_code");
            String departmentCode = (String) row.get("department_code");
            int count = (int) row.get("count") + 1; // tăng lên 1

            return String.format("%s.%s.%s.%03d", pre, instituteCode, departmentCode, count);
        } catch (Exception e) {
            return null;
        }

    }

}
