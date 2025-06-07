package com.example.timetabling.dto.request;

import java.util.List;

import com.example.timetabling.model.Subject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubjectRequest {
    private List<Subject> subjects;
}   
