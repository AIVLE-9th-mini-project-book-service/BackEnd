package com.aivle.bookapp.dto;

import jakarta.validation.constraints.NotBlank;

public record AiBookSummaryRequest(
        @NotBlank(message = "API Key는 필수입니다.")
        String apiKey
) {
}