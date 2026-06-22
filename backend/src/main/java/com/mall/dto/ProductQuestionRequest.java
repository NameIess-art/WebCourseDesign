package com.mall.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductQuestionRequest(@NotBlank String question) {
}
