package com.elian.proximidade_certa.services;

import com.elian.proximidade_certa.dto.EstablishmentRequestDTO;
import com.elian.proximidade_certa.dto.EstablishmentResponseDTO;
import com.elian.proximidade_certa.entities.Establishment;
import com.elian.proximidade_certa.repositories.EstablishmentRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstablishmentService {
    @Autowired
    private EstablishmentRepository repository;

    // GeometryFactory é a ferramenta principal para criar objetos geométricos do JTS
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional
    public EstablishmentResponseDTO create(EstablishmentRequestDTO dto){
        // 1. Converter Latitude/Longitude do DTO em um objeto Point
        Point location = geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude()));
        location.setSRID(4326); // Define o sistema de coordenadas (WGS 84)

        // 2. Criar a nova entidade
        Establishment newEstablishment = new Establishment();
        newEstablishment.setName(dto.name());
        newEstablishment.setDescription(dto.description());
        newEstablishment.setCategory(dto.category());
        newEstablishment.setLocation(location);

        Establishment saveEstablishment = repository.save(newEstablishment);
        return new EstablishmentResponseDTO(saveEstablishment);
    }

    @Transactional(readOnly = true)
    public List<EstablishmentResponseDTO> findAll(){
        return repository.findAll().stream().map(EstablishmentResponseDTO::new).collect(Collectors.toList());
    }
}
