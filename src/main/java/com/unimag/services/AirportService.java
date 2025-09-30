package com.unimag.services;

import com.unimag.api.dto.AirportDtos.*;
import java.util.List;

public interface AirportService {
    AirportResponse create(AirportCreateRequest request);
    AirportResponse findById(Long id);
    List<AirportResponse> findAll();
    AirportResponse update(Long id, AirportUpdateRequest request);
    void deleteById(Long id);
}
