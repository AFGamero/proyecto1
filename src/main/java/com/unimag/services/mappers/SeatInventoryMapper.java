package com.unimag.services.mappers;

import com.unimag.api.dto.SeatInventoryDtos;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.entidades.SeatInventory;

public class SeatInventoryMapper {
    public static SeatInventory toEntity(SeatInventoryDtos.SeatInventoryCreateRequest request) {
        return SeatInventory.builder().cabin(Cabin.valueOf(request.cabin())).availableSeats(request.availableSeats())
                .totalSeats(request.availableSeats()).build();
    }

    public static SeatInventoryDtos.SeatInventoryResponse toResponse(SeatInventory seatInventory) {
        return new SeatInventoryDtos.SeatInventoryResponse(
                seatInventory.getId(), seatInventory.getCabin().name(),
                seatInventory.getTotalSeats(), seatInventory.getAvailableSeats(), seatInventory.getFlight().getId()
        );
    }

    public static void patch(SeatInventory entity, SeatInventoryDtos.SeatInventoryUpdateRequest update) {
        if (update.cabin() != null) entity.setCabin(Cabin.valueOf(update.cabin()));
        if (update.totalSeats() != null) entity.setTotalSeats(update.totalSeats());
        if (update.availableSeats() != null) entity.setAvailableSeats(update.availableSeats());
    }
}
