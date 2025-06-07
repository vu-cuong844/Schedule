package com.example.authentication.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendEmailResponse {
    private boolean isSended;
    private String message;
}
