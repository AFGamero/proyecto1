package com.unimag.services;

import com.unimag.api.dto.SeatInventoryDtos.*;
import java.util.List;

public interface SeatInventoryService {
    SeatInventoryResponse create(Long flightId, SeatInventoryCreateRequest request);
    SeatInventoryResponse findById(Long id);
    SeatInventoryResponse findByFlightAndCabin(Long flightId, String cabin);
    List<SeatInventoryResponse> findByFlightId(Long flightId);
    SeatInventoryResponse update(Long id, SeatInventoryUpdateRequest request);
    void deleteById(Long id);
    boolean checkAvailability(Long flightId, String cabin, Integer minSeats);
    SeatInventoryResponse reserveSeats(Long flightId, String cabin, Integer seats);
    SeatInventoryResponse releaseSeats(Long flightId, String cabin, Integer seats);
}