package com.unimag.services;

import com.unimag.api.dto.SeatInventoryDtos.SeatInventoryCreateRequest;
import com.unimag.api.dto.SeatInventoryDtos.SeatInventoryResponse;
import com.unimag.api.dto.SeatInventoryDtos.SeatInventoryUpdateRequest;

import java.util.List;

public interface SeatInventoryService {
    SeatInventoryResponse create(Long flightId, SeatInventoryCreateRequest request);
    SeatInventoryResponse findById(Long id);
    SeatInventoryResponse findByFlightAndCabin(Long flightId, String cabin);
    List<SeatInventoryResponse> findByFlightId(Long flightId);
    SeatInventoryResponse update(Long id, SeatInventoryUpdateRequest request);
    void deleteById(Long id);
}