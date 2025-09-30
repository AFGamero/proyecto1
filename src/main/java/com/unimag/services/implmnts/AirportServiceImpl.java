package com.unimag.services.implmnts;
import com.unimag.api.dto.AirportDtos;
import com.unimag.dominio.entidades.Airport;
import com.unimag.dominio.repositories.AirportRepository;
import com.unimag.services.AirportService;
import com.unimag.services.mappers.AirportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AirportServiceImpl implements AirportService  {
  private final AirportRepository repo;

    @Override
    public AirportDtos.AirportResponse create(AirportDtos.AirportCreateRequest request) {
        Airport saved = repo.save(AirportMapper.toEntity(request));
        return AirportMapper.toResponse(saved);
    }

    @Override
    public AirportDtos.AirportResponse findById(Long id) {

        return repo.findById(id).map(AirportMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Airport with id " + id + " not found"));
    }

    @Override
    public List<AirportDtos.AirportResponse> findAll() {
        return repo.findAll().stream().map(AirportMapper::toResponse).toList();
    }

    @Override
    public AirportDtos.AirportResponse update(Long id, AirportDtos.AirportUpdateRequest request) {
        Airport airport = repo.findById(id).orElseThrow(() -> new RuntimeException("Airport with id " + id + " not found"));
        AirportMapper.path(airport, request);
        return AirportMapper.toResponse(repo.save(airport));
    }

    @Override
    public void deleteById(Long id) {
    repo.deleteById(id);
    }
}
