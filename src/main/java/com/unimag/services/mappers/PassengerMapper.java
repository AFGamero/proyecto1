package com.unimag.services.mappers;

import com.unimag.api.dto.PassengerDtos;
import com.unimag.api.dto.PassengerDtos.PassengerCreateRequest;
import com.unimag.api.dto.PassengerDtos.PassengerProfileDto;
import com.unimag.api.dto.PassengerDtos.PassengerResponse;
import com.unimag.api.dto.PassengerDtos.PassengerUpdateRequest;
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
    void updateEntityFromRequest(PassengerUpdateRequest request, @MappingTarget Passenger passenger);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void patch(PassengerDtos.PassengerUpdateRequest request, @MappingTarget Passenger entity);
}