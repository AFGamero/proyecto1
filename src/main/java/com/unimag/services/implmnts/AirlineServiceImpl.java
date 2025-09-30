package com.unimag.services.implmnts;

import com.unimag.api.dto.AirlineDtos;
import com.unimag.dominio.entidades.Airline;
import com.unimag.dominio.repositories.AirlineRepository;
import com.unimag.exception.NotFoundException;
import com.unimag.services.AirlineService;
import com.unimag.services.mappers.AirlineMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AirlineServiceImpl implements AirlineService {
    @Override
    public AirlineDtos.AirlineResponse create(AirlineDtos.AirlineCreateRequest request) {
        return AirlineMapper.toResponse(repo.save(AirlineMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public AirlineDtos.AirlineResponse findById(Long id) {
        return repo.findById(id).map(AirlineMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Airline with id " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AirlineDtos.AirlineResponse> findAll() {
        return repo.findAll().stream().map(AirlineMapper::toResponse).toList();
    }

        @Override
        public AirlineDtos.AirlineResponse update(Long id, AirlineDtos.AirlineUpdateRequest request) {
            Airline airline = repo.findById(id).orElseThrow(() -> new NotFoundException("Airline with id " + id + " not found"));
            AirlineMapper.patch(airline, request);
            return AirlineMapper.toResponse(airline);
        }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    private final AirlineRepository repo ;



}
