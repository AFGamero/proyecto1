package com.unimag.api.dto;

import jakarta.annotation.Nonnull;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

public record SeatInventoryDtos() {
    public record SeatInventoryCreateRequest(@Nonnull String cabin,@Nonnull Integer totalSeats, @Nonnull Integer availableSeats)
            implements Serializable {}
    public record SeatInventoryUpdateRequest(String cabin, Integer totalSeats, Integer availableSeats)
            implements Serializable {}
    public record SeatInventoryResponse(Long id, String cabin, Integer totalSeats, Integer availableSeats, Long flight_id)
            implements Serializable {}
}
