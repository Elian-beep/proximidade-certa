package com.elian.proximidade_certa.services;

import com.elian.proximidade_certa.dto.EstablishmentRequestDTO;
import com.elian.proximidade_certa.dto.EstablishmentResponseDTO;
import com.elian.proximidade_certa.entities.Establishment;
import com.elian.proximidade_certa.exceptions.ResourceNotFoundException;
import com.elian.proximidade_certa.repositories.EstablishmentRepository;
import com.elian.proximidade_certa.specifications.EstablishmentSpecification;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    /* @Transactional(readOnly = true)
    public List<EstablishmentResponseDTO> findAll(){
        return repository.findAll().stream().map(EstablishmentResponseDTO::new).collect(Collectors.toList());
    } */
    @Transactional(readOnly = true)
    public Page<EstablishmentResponseDTO> findAll(String name, String category, Pageable pageable){
        //Specification para criar a query dinâmica
        Specification<Establishment> spec = EstablishmentSpecification.searchBy(name, category);

        Page<Establishment> page = repository.findAll(spec, pageable);

        return page.map(EstablishmentResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public EstablishmentResponseDTO findById(Long id) {
        Establishment entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com id: " + id));

        return new EstablishmentResponseDTO(entity);
    }

    @Transactional
    public EstablishmentResponseDTO update(Long id, EstablishmentRequestDTO dto){
        Establishment entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o id: "+id));

        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setCategory(dto.category());

        Point location = geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude()));
        location.setSRID(4326);
        entity.setLocation(location);

        entity = repository.save(entity);

        return new EstablishmentResponseDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Estabelecimento não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }

}
