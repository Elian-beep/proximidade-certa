package com.elian.proximidade_certa.dto;

public record EstablishmentUpdateDTO(String name,
                                     String description,
                                     String category,
                                     String street,
                                     String city,
                                     String state,
                                     String postalCode) {
}
