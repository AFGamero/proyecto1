package com.unimag.services.mappers;
import com.unimag.api.dto.SeatInventoryDtos.*;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.SeatInventory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeatInventoryMapper {

    @Mapping(target = "flight", ignore = true)
    @Mapping(target = "cabin", source = "cabin")
    SeatInventory toEntity(SeatInventoryCreateRequest request);

    @Mapping(source = "flight.id", target = "flight_id")
    @Mapping(target = "cabin", expression = "java(seatInventory.getCabin().name())")
    SeatInventoryResponse toResponse(SeatInventory seatInventory);

    List<SeatInventoryResponse> toResponseList(List<SeatInventory> seatInventories);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "flight", ignore = true)
    void updateEntityFromRequest(SeatInventoryUpdateRequest request, @MappingTarget SeatInventory seatInventory);

    // Helper para convertir String a Cabin enum
    default Cabin mapCabin(String cabin) {
        return cabin != null ? Cabin.valueOf(cabin.toUpperCase()) : null;
    }
}