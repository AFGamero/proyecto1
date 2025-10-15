package com.unimag.services.mappers;

import com.unimag.api.dto.FlightDtos.FlightCreateRequest;
import com.unimag.api.dto.FlightDtos.FlightResponse;
import com.unimag.api.dto.FlightDtos.FlightUpdateRequest;
import com.unimag.dominio.entidades.Flight;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TagMapper.class})
public interface FlightMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Flight toEntity(FlightCreateRequest request);

    @Mapping(source = "airline.id", target = "airline_id")
    @Mapping(source = "origin.id", target = "origin_airport_id")
    @Mapping(source = "destination.id", target = "destination_airport_id")
    @Mapping(source = "tags", target = "tags")
    FlightResponse toResponse(Flight flight);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void patch(FlightUpdateRequest request, @MappingTarget Flight entity);
}
