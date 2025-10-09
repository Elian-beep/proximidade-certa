package com.elian.proximidade_certa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EstablishmentRequestDTO(@NotBlank String name, String description, @NotBlank String category,
                                      @NotBlank(message = "O endereço (rua, número) é obrigatório") String street,
                                      @NotBlank(message = "A cidade é obrigatória") String city,
                                      @NotBlank(message = "O estado é obrigatório") String state, String postalCode) {
}
