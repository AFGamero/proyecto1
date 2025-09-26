package com.unimag.api.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

public record FlightDtos() {
    public record FligtCreateRequest(
            String number,
            OffsetDateTime departureTime,
            OffsetDateTime arrivalTime,
            AirlineDtos airlineDtos ,
            AirportDtos origin,
            AirportDtos destination

    )implements Serializable {}

    public record FlightCreateResponse(
            Long id,
            String number,
            OffsetDateTime departureTime,
            OffsetDateTime arrivalTime,
            AirlineDtos airlineDtos ,
            AirportDtos origin,
            AirportDtos destination,
            Set<SeatInventoryDtos.SeatInventoryRequest> seatInventorysList
    ) implements Serializable {}

    public record FlightUpdateRequest(
            String number,
            OffsetDateTime departureTime,
            OffsetDateTime arrivalTime,
            AirlineDtos airlineDtos ,
            AirportDtos origin,
            AirportDtos destination
    ) implements Serializable {}

    public record AirlineDtos(
            Long id,
            String name,
            String code
    ) implements Serializable {}

    public record AirportDtos(
            Long id,
            String code,
            String name,
            String city
    ) implements Serializable {}




}
