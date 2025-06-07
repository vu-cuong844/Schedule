package com.example.timetabling.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.timetabling.dto.request.SearcheSubjectRequest;
import com.example.timetabling.dto.response.Response;
import com.example.timetabling.model.Subject;
import com.example.timetabling.repo.SubjectRepository;

@Service
public class SubjectSevice {
    @Autowired
    private SubjectRepository subjectRepository;

    public List<Subject> getAllSubject() {
        return subjectRepository.findAll();
    }

    public Response<List<Subject>> getAllSubjects() {
        try {
            List<Subject> subjects = subjectRepository.findAll();
            return Response.<List<Subject>>builder()
                    .data(subjects)
                    .success(true)
                    .message("Find all " + subjects.size() + " subject" + (subjects.size() > 1 ? "s." : "."))
                    .build();
        } catch (Exception e) {
            return Response.<List<Subject>>builder()
                    .data(null)
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }

    public Response<List<Subject>> createSubbjects(List<Subject> subjects) {
        try {
            List<Subject> savedSubjects = subjectRepository.saveAll(subjects);
            return Response.<List<Subject>>builder()
                    .data(savedSubjects)
                    .success(true)
                    .message("Create successfully " + savedSubjects.size() + " record.")
                    .build();
        } catch (Exception e) {
            return Response.<List<Subject>>builder()
                    .data(null)
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public Response<Subject> updateSubject(Subject subject) {
        try {
            if (subject.getSubjectCode() == null || subject.getSubjectCode().trim().isEmpty()) {
                return Response.<Subject>builder()
                        .data(null)
                        .success(false)
                        .message("Subject code must not be null or empty")
                        .build();
            }

            Subject existingSubject = subjectRepository.findById(subject.getSubjectCode()).orElse(null);
            if (existingSubject == null) {
                return Response.<Subject>builder()
                        .data(null)
                        .success(false)
                        .message("Not found subject with id = " + subject.getSubjectCode())
                        .build();
            }

            Subject updatedSubject = subjectRepository.save(subject);
            return Response.<Subject>builder()
                    .data(updatedSubject)
                    .success(true)
                    .message("Update successfully")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Update subject failed: " + e.getMessage());
        }
    }

    public Response<List<Subject>> searchSubject(SearcheSubjectRequest request) {
        try {
            if (request.getSubjectCode() == null || request.getSubjectCode().isEmpty()) {
                List<Subject> subjects = subjectRepository.findByNameOrEnglishName(request.getSubjectName());
                return Response.<List<Subject>>builder()
                        .data(subjects)
                        .success(true)
                        .message("Found " + subjects.size() + " records")
                        .build();
            }
            Subject subject = subjectRepository.findById(request.getSubjectCode()).orElse(null);
            if (subject == null) {
                return Response.<List<Subject>>builder()
                        .data(null)
                        .success(true)
                        .message("Found 0 records")
                        .build();
            }

            return Response.<List<Subject>>builder()
                    .data(Collections.singletonList(subject))
                    .success(true)
                    .message("Found 1 records")
                    .build();

        } catch (Exception e) {
            return Response.<List<Subject>>builder()
                    .data(null)
                    .success(false)
                    .message("Eror: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public Response<String> deleteSubject(String id) {
        try {
            subjectRepository.deldeleteBySubjectCode(id);
            return Response.<String>builder()
                    .data("")
                    .success(true)
                    .message("Deleted subject with id = " + id)
                    .build();
        } catch (Exception e) {
            return Response.<String>builder()
                    .data("")
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }

}
