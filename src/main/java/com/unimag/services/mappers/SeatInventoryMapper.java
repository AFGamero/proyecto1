package com.unimag.services.mappers;

import com.unimag.api.dto.SeatInventoryDtos.SeatInventoryCreateRequest;
import com.unimag.api.dto.SeatInventoryDtos.SeatInventoryResponse;
import com.unimag.api.dto.SeatInventoryDtos.SeatInventoryUpdateRequest;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.SeatInventory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeatInventoryMapper {

    @Mapping(target = "flight", ignore = true)
    @Mapping(target = "cabin", expression = "java(mapCabin(request.cabin()))")
    SeatInventory toEntity(SeatInventoryCreateRequest request);

    @Mapping(target = "flight_id", expression = "java(seatInventory.getFlight() != null ? seatInventory.getFlight().getId() : null)")
    @Mapping(target = "cabin", expression = "java(seatInventory.getCabin() != null ? seatInventory.getCabin().name() : null)")
    SeatInventoryResponse toResponse(SeatInventory seatInventory);

    List<SeatInventoryResponse> toResponseList(List<SeatInventory> seatInventories);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "flight", ignore = true)
    @Mapping(target = "cabin", expression = "java(mapCabin(request.cabin()))")
    void updateEntityFromRequest(SeatInventoryUpdateRequest request, @MappingTarget SeatInventory seatInventory);

    // Helper para convertir String a Cabin enum
    default Cabin mapCabin(String cabin) {
        return cabin != null ? Cabin.valueOf(cabin.toUpperCase()) : null;
    }
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flight", ignore = true)
    void patch(SeatInventoryUpdateRequest request, @MappingTarget SeatInventory entity);
}