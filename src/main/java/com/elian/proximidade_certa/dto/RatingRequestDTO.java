package com.elian.proximidade_certa.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RatingRequestDTO(
        @NotNull(message = "A nota é obrigatória") @Min(value = 1, message = "A nota mínima é 1") @Max(value = 5, message = "A nota máxima é 5") Integer score,
        @Size(max = 500, message = "O comentário não pode exceder 500 caracteres")
        String comment, @Size(max = 100, message = "O nome do autor não pode exceder 100 caracteres")
        String authorName) {
}
