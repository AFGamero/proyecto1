package com.unimag.api.dto;

import java.io.Serializable;
import java.util.List;

public record AirlineDtos() {
    public record AirlineCreateRequest(
            String name,
            String code,
            List<FlightDtos.FligtCreateRequest> flights
    )implements Serializable {}

    public record AirlineUpdateRequest(
            String name,
            String code,
            List<FlightDtos.FligtCreateRequest> flights
    )implements Serializable {}

    public record AirlineResponse(
            Long id,
            String name,
            String code,
            List<FlightDtos.FlightCreateResponse> flights) implements Serializable {}


}
