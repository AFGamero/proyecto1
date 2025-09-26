package com.unimag.services.mappers;

import com.unimag.api.dto.FlightDtos;
import com.unimag.api.dto.TagDtos;
import com.unimag.dominio.entidades.Flight;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class FlightMapper {
    public static Flight ToEntity(FlightDtos.FlightCreateResponse request) {
        return Flight.builder().number(request.number()).build();
    }
    public static FlightDtos.FlightCreateResponse ToResponse(Flight flight) {
        Set<TagDtos.TagResponse> tagResponses = flight.getTags() == null ? Collections.emptySet() :
                flight.getTags().stream()
                        .map(tag -> new TagDtos.TagResponse(tag.getId(), tag.getName(), null))
                        .collect(Collectors.toSet());

        return  new FlightDtos.FlightCreateResponse(
                flight.getId(), flight.getNumber(), flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getAirline() != null ? flight.getAirline().getId() : null,
                flight.getOrigin() != null ? flight.getOrigin().getCode() : null,
                flight.getDestination() != null ? flight.getDestination().getCode() : null,
                tagResponses
        );



    }

    public static void path(Flight entity, FlightDtos.FlightUpdateRequest request ) {
        if (request.name() != null ) entity.setNumber(request.name());
        if (request.departureTime() != null ) entity.setDepartureTime(request.departureTime());
        if (request.arrivalTime() != null ) entity.setArrivalTime(request.arrivalTime());
    }
}
