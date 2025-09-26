package com.unimag.api.dto;

import java.io.Serializable;
import java.util.Set;

public record TagDtos() {
    public record TagRequest(
          String name,
          Set<FlightDtos.FlightCreateResponse> flights
    ) implements Serializable {}

    public record TagResponse(
          Long id,
          String name,
          Set<FlightDtos.FlightCreateResponse> flights
    ) implements Serializable {}

    public record TagResponseDto(
            Long id,
            String name,
            Set<FlightDtos> flights
    )implements Serializable{}


}
