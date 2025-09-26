package com.unimag.services.mappers;

import com.unimag.api.dto.AirportDtos;
import com.unimag.dominio.entidades.Airport;

import java.util.stream.Collectors;

    public class AirportMapper {
        public static Airport ToEntity(AirportDtos.AirportCreateRequest request ) {
            return  Airport.builder().code(request.code()).name(request.name()).build();
        }
        public static  AirportDtos.AirportResponse ToResponse(Airport airport ) {
            return new AirportDtos.AirportResponse(airport.getId(), airport.getCode(),
                    airport.getName(), airport.getCity(),
                    airport.getOriginFlights() == null ? null :
                            airport.getOriginFlights().stream().map(FlightMapper::ToResponse).collect(Collectors.toSet()),
                    airport.getDestinationFlights() == null ? null:
                            airport.getDestinationFlights().stream().map(FlightMapper::ToResponse).collect(Collectors.toSet()));

        }
        public static void path(Airport entity, AirportDtos.AirportUpdateRequest request ) {
            if (request.code() != null) entity.setCode(request.code());
            if (request.name() != null) entity.setName(request.name());

        }
}
