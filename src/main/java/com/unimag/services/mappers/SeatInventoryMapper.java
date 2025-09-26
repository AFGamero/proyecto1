package com.unimag.services.mappers;

import com.unimag.api.dto.SeatInventoryDtos;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.entidades.SeatInventory;

public class SeatInventoryMapper {
    public static SeatInventory ToEntity(SeatInventoryDtos.SeatInventoryRequest request ) {
        return SeatInventory.builder().Cabin(request.cabin()).availableSeats(request.availableSeats())
                .totalSeats(request.availableSeats())
                .flight(Flight.builder().id(request.flightId()).build()).build();
    }
    public static SeatInventoryDtos.SeatInventoryResponse ToResponse(SeatInventory seatInventory) {
        return new SeatInventoryDtos.SeatInventoryResponse(
                seatInventory.getId(), seatInventory.getCabin(),
                seatInventory.getTotalSeats(), seatInventory.getAvailableSeats(),
                seatInventory.getFlight() == null ? seatInventory.getFlight().getId() : null
        );
    }

    public static void path(SeatInventory entity, SeatInventoryDtos.SeatInventoryUpdateRequest update) {
        if (update.cabin() != null) entity.setCabin(update.cabin());
        if (update.availableSeats() != null) entity.setAvailableSeats(update.availableSeats());
        if (update.flightId() != 0) entity.setFlight(Flight.builder().id(update.flightId()).build());
    }
}
