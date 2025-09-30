package com.unimag.services.mappers;

import com.unimag.api.dto.AirlineDtos;
import com.unimag.dominio.entidades.Airline;
import com.unimag.dominio.entidades.Flight;
import java.util.stream.Collectors;


public class AirlineMapper {

    public static Airline toEntity(AirlineDtos.AirlineCreateRequest request){
        return Airline.builder().code(request.code()).name(request.name()).build();
    }

    public static AirlineDtos.AirlineResponse toResponse(Airline entity){
        //Mm, this is something weird, i´ll check it out later
        return new AirlineDtos.AirlineResponse(entity.getId(), entity.getCode(), entity.getName());
    }

    public static void patch(Airline entity, AirlineDtos.AirlineUpdateRequest request){
        if(request.code() != null) entity.setCode(request.code());
        if(request.name() != null) entity.setName(request.name());
    }


}
