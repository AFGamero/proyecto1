package com.unimag.services;

import com.unimag.api.dto.AirlineDtos.AirlineCreateRequest;
import com.unimag.api.dto.AirlineDtos.AirlineResponse;
import com.unimag.api.dto.AirlineDtos.AirlineUpdateRequest;

import java.util.List;

public interface AirlineService {
    AirlineResponse create(AirlineCreateRequest request);
    AirlineResponse findById(Long id);
    List<AirlineResponse> findAll();
    AirlineResponse update(Long id, AirlineUpdateRequest request);
    void deleteById(Long id);
}

