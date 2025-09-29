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

    public static Airport toResponse(Airport airport) {
        return Airport.builder()
                .id(airport.getId())
                .code(airport.getCode())
                .name(airport.getName())
                .city(airport.getCity())
                .build();
    }

    public static void path(Airport entity, String code, String name, String city, String country) {
        if (code != null) entity.setCode(code);
        if (name != null) entity.setName(name);
        if (city != null) entity.setCity(city);
    }



}
