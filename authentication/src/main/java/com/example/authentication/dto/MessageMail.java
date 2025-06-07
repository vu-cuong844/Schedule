package com.example.authentication.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageMail {
    private String from;       
    private String[] to;        
    private String subject;   
    private String content;   
}