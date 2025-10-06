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
  private final AirportMapper airportMapper;

    @Override
    public AirportDtos.AirportResponse create(AirportDtos.AirportCreateRequest request) {
        Airport saved = repo.save(airportMapper.toEntity(request));
        return airportMapper.toResponse(saved);
    }

    @Override
    public AirportDtos.AirportResponse findById(Long id) {

        return repo.findById(id).map(airportMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Airport with id " + id + " not found"));
    }

    @Override
    public List<AirportDtos.AirportResponse> findAll() {
        return repo.findAll().stream().map(airportMapper::toResponse).toList();
    }

    @Override
    public AirportDtos.AirportResponse update(Long id, AirportDtos.AirportUpdateRequest request) {
        Airport airport = repo.findById(id).orElseThrow(() -> new RuntimeException("Airport with id " + id + " not found"));
        airportMapper.updateEntityFromRequest(request,airport);
        return airportMapper.toResponse(repo.save(airport));
    }

    @Override
    public void deleteById(Long id) {
    repo.deleteById(id);
    }
}
