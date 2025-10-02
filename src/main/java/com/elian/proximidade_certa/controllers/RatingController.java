package com.elian.proximidade_certa.controllers;

import com.elian.proximidade_certa.dto.RatingRequestDTO;
import com.elian.proximidade_certa.dto.RatingResponseDTO;
import com.elian.proximidade_certa.services.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/establishments/{establishmentId}/ratings")
public class RatingController {
    @Autowired
    private RatingService service;

    @PostMapping
    public ResponseEntity<RatingResponseDTO> create(
            @PathVariable Long establishmentId,
            @RequestBody @Valid RatingRequestDTO dto) {

        RatingResponseDTO newDto = service.create(establishmentId, dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDto.id()).toUri();

        return ResponseEntity.created(uri).body(newDto);
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<RatingResponseDTO> update(
            @PathVariable Long establishmentId,
            @PathVariable Long ratingId,
            @RequestBody @Valid RatingRequestDTO dto) {

        RatingResponseDTO updatedDto = service.update(establishmentId, ratingId, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long establishmentId,
            @PathVariable Long ratingId) {

        service.delete(establishmentId, ratingId);
        return ResponseEntity.noContent().build();
    }
}
