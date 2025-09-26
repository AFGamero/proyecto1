package com.unimag.api.dto;

import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.Flight;

import java.io.Serializable;
import java.util.Set;

public record Tag() {
    public record TagRequest(
          String name,
          Set<FlightDtos.FlightCreateResponse> flights
    ) implements Serializable {}

    public record TagResponse(
          Long id,
          String name,
          Set<FlightDtos.FlightCreateResponse> flights
    ) implements Serializable {}




}
