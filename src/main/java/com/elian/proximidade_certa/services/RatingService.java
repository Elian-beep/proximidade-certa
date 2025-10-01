package com.elian.proximidade_certa.services;

import com.elian.proximidade_certa.dto.RatingRequestDTO;
import com.elian.proximidade_certa.dto.RatingResponseDTO;
import com.elian.proximidade_certa.entities.Establishment;
import com.elian.proximidade_certa.entities.Rating;
import com.elian.proximidade_certa.exceptions.ResourceNotFoundException;
import com.elian.proximidade_certa.repositories.EstablishmentRepository;
import com.elian.proximidade_certa.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Transactional
    public RatingResponseDTO create(Long establishmentId, RatingRequestDTO dto) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com id: " + establishmentId));

        Rating newRating = new Rating();
        newRating.setScore(dto.score());
        newRating.setComment(dto.comment());
        newRating.setAuthorName(dto.authorName());

        newRating.setEstablishment(establishment);

        Rating savedRating = ratingRepository.save(newRating);

        return new RatingResponseDTO(savedRating);
    }
}
