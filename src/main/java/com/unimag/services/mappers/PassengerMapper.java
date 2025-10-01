package com.unimag.services.mappers;

import com.unimag.api.dto.PassengerDtos.*;
import com.unimag.dominio.entidades.Passenger;
import com.unimag.dominio.entidades.PassengerProfile;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PassengerMapper {
    Passenger toEntity(PassengerCreateRequest request);
    PassengerResponse toResponse(Passenger passenger);
    List<PassengerResponse> toResponseList(List<Passenger> passengers);

    PassengerProfileDto toProfileDto(PassengerProfile profile);
    PassengerProfile toProfileEntity(PassengerProfileDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(PassengerCreateUpdateRequest request, @MappingTarget Passenger passenger);
}