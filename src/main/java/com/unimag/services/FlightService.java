package com.unimag.services;

import com.unimag.api.dto.FlightDtos.*;
import java.util.List;

public interface FlightService {
    FlightResponse create(Long airlineId, Long originId, Long destinationId, FlightCreateRequest request);
    FlightResponse findById(Long id);
    List<FlightResponse> findAll();
    FlightResponse update(Long id, FlightUpdateRequest request);
    void deleteById(Long id);
    FlightResponse addTag(Long flightId, Long tagId);
    FlightResponse removeTag(Long flightId, Long tagId);
}