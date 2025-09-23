package com.elian.proximidade_certa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EstablishmentRequestDTO(@NotBlank String name, String description, @NotBlank String category, @NotNull double latitude, @NotNull double longitude) {
}
