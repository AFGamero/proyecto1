package com.unimag.services.mappers;

import com.unimag.api.dto.AirportDtos.*;
import com.unimag.dominio.entidades.Airport;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AirportMapper {
    Airport toEntity(AirportCreateRequest request);
    AirportResponse toResponse(Airport airport);
    List<AirportResponse> toResponseList(List<Airport> airports);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(AirportUpdateRequest request, @MappingTarget Airport airport);
}
