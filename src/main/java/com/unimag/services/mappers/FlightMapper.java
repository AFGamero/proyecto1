package com.unimag.services.mappers;

import com.unimag.api.dto.FlightDtos.FlightCreateRequest;
import com.unimag.api.dto.FlightDtos.FlightResponse;
import com.unimag.api.dto.FlightDtos.FlightUpdateRequest;
import com.unimag.dominio.entidades.Flight;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TagMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FlightMapper {

    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    Flight toEntity(FlightCreateRequest request);

    @Mapping(target = "airline_id", expression = "java(flight.getAirline() != null ? flight.getAirline().getId() : null)")
    @Mapping(target = "origin_airport_id", expression = "java(flight.getOrigin() != null ? flight.getOrigin().getId() : null)")
    @Mapping(target = "destination_airport_id", expression = "java(flight.getDestination() != null ? flight.getDestination().getId() : null)")
    FlightResponse toResponse(Flight flight);

    List<FlightResponse> toResponseList(List<Flight> flights);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    void updateEntityFromRequest(FlightUpdateRequest request, @MappingTarget Flight flight);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void patch(FlightUpdateRequest request, @MappingTarget Flight entity);
}