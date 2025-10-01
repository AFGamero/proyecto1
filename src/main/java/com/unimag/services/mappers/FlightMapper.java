package com.unimag.services.mappers;

import com.unimag.api.dto.FlightDtos.*;
import com.unimag.dominio.entidades.Flight;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TagMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FlightMapper {

    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    Flight toEntity(FlightCreateRequest request);

    @Mapping(source = "airline.id", target = "airline_id")
    @Mapping(source = "origin.id", target = "origin_airport_id")
    @Mapping(source = "destination.id", target = "destination_airport_id")
    FlightResponse toResponse(Flight flight);

    List<FlightResponse> toResponseList(List<Flight> flights);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    void updateEntityFromRequest(FlightUpdateRequest request, @MappingTarget Flight flight);
}