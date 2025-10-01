package com.unimag.services.mappers;

import com.unimag.api.dto.AirlineDtos.*;
import com.unimag.dominio.entidades.Airline;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AirlineMapper {
    Airline toEntity(AirlineCreateRequest request);
    AirlineResponse toResponse(Airline airline);
    List<AirlineResponse> toResponseList(List<Airline> airlines);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(AirlineUpdateRequest request, @MappingTarget Airline airline);
}
