package com.unimag.services.mappers;

import com.unimag.api.dto.AirlineDtos.AirlineCreateRequest;
import com.unimag.api.dto.AirlineDtos.AirlineResponse;
import com.unimag.api.dto.AirlineDtos.AirlineUpdateRequest;
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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flights", ignore = true)
    void patch(AirlineUpdateRequest request, @MappingTarget Airline entity);
}
