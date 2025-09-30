package com.elian.proximidade_certa.controllers;

import com.elian.proximidade_certa.dto.EstablishmentRequestDTO;
import com.elian.proximidade_certa.dto.EstablishmentResponseDTO;
import com.elian.proximidade_certa.services.EstablishmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/establishments")
public class EstablishmentController {
    @Autowired
    EstablishmentService service;

    @PostMapping
    public ResponseEntity<EstablishmentResponseDTO> create(@RequestBody @Valid EstablishmentRequestDTO dto){
        EstablishmentResponseDTO newDto = service.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(newDto.id()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @GetMapping
    public ResponseEntity<Page<EstablishmentResponseDTO>> findAll(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "category", required = false) String category, Pageable pageable){
        Page<EstablishmentResponseDTO> page = service.findAll(name, category, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstablishmentResponseDTO> findById(@PathVariable Long id) {
        EstablishmentResponseDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstablishmentResponseDTO> update(@PathVariable Long id, @RequestBody @Valid EstablishmentRequestDTO dto){
        EstablishmentResponseDTO updatedDto = service.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
