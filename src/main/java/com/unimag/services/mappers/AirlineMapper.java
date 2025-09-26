package com.unimag.services.mappers;

import com.unimag.api.dto.AirlineDtos;
import com.unimag.dominio.entidades.Airline;
import com.unimag.dominio.entidades.Flight;

import java.util.stream.Collectors;


public class AirlineMapper {

    public static Airline toEntity(AirlineDtos.AirlineCreateRequest request) {
        return Airline.builder().code(request.code()).name(request.name()).build();
    }

    public static AirlineDtos.AirlineResponse toResponse(Airline airline) {
        return new AirlineDtos.AirlineResponse(airline.getId(), airline.getCode(), airline.getName(),
                    airline.getCode(),airline.getFlights() == null ? null: airline.getFlights().stream().map(FlightMapper::ToResponse).collect(Collectors.toList()));

    };

    public static void patch (Airline airline, AirlineDtos.AirlineUpdateRequest request) {
        if (request.code() != null) {
            airline.setCode(request.code());
        }
        if (request.name() != null) {
            airline.setName(request.name());
        }
    }

}
