package com.aivle.bookapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GenerateCoverRequest {

    @NotBlank(message = "API Key는 필수입니다.")
    private String apiKey;

    private String quality = "low";
}
