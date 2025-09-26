package com.unimag.api.dto;

import java.io.Serializable;

public record SeatInventoryDtos() {

    public record  SeatInventoryRequest(
            Integer totalSeats,
            Integer availableSeats,
            Long flightId,
            String cabin
    )implements Serializable {}

    public record SeatInventoryResponse(
            Long id,
            Integer totalSeats,
            Integer availableSeats,
            Long flightId,
            String cabin
    )implements Serializable {}

    public record SeatInventoryUpdateRequest(
            Integer totalSeats,
            Integer availableSeats
    )implements Serializable {}

}
