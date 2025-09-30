package com.unimag.services.mappers;

import com.unimag.api.dto.AirportDtos;
import com.unimag.dominio.entidades.Airport;

public class AirportMapper {
    public static Airport toEntity(AirportDtos.AirportCreateRequest request) {
        return Airport.builder()
                .code(request.code())
                .name(request.name())
                .city(request.city())
                .build();
    }

    public static AirportDtos.AirportResponse toResponse(Airport airport) {

        return new AirportDtos.AirportResponse(airport.getId(), airport.getCode(), airport.getName(), airport.getCity());
    }

    public static void path(Airport entity, AirportDtos.AirportUpdateRequest update) {
        String code = update.code();
        String name = update.name();
    }
}
