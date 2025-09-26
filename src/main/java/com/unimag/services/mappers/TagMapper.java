package com.unimag.services.mappers;

import com.unimag.api.dto.FlightDtos;
import com.unimag.api.dto.TagDtos;

import java.util.Set;
import java.util.stream.Collectors;

public class TagMapper {
    public static TagDtos ToEntity(TagDtos.TagRequest request){
        return  com.unimag.dominio.entidades.Tag().name(request.name()).build();
    }
    public static TagDtos.TagResponse toResponse(TagDtos tag){
        return new TagDtos.TagResponse(
                tag.getId(), tag.getName(),
                tag.getFlights() == null ? Set.of():
                        tag.getFlights().stream().map(flight ->
                                new FlightDtos.FlightResponse(
                                        flight.getId(),flight.getNumber(),
                                        flight.getDepartureTime(),flight.getArrivalTime(),
                                        flight.getAirline() != null ? flight.getAirline().getId() : null,
                                        flight.getOrigin() != null ? flight.getOrigin().getCode() : null,
                                        flight.getDestination() != null ? flight.getDestination().getCode() : null,
                                        null
                                )).collect(Collectors.toSet())
        );
    }
    public static void path(TagDtos entity, TagDtos.TagCreateRequest request){
        if(entity.getName() != null) entity.setName(entity.getName());
    }
}
