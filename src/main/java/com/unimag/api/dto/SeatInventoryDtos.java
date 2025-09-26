package com.unimag.api.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

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


    public record FlightDtos(
            Long id,
            String number,
            OffsetDateTime departureTime,
            OffsetDateTime arrivalTime,
            AirlineDtos airlineDtos,
            AirportDtos origin,
            AirportDtos destination,
            Set<TagDtos> tags
    )implements Serializable {}



}
