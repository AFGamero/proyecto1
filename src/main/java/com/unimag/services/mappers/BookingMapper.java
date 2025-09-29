package com.unimag.services.mappers;

import com.unimag.api.dto.BookingDtos;
import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.entidades.BookingItem;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.Passenger;

import java.util.List;

public class BookingMapper {
    public static BookingDtos.BookingResponse toResponse(Booking entity) {
        var items = entity.getItems() == null? List.<BookingDtos.BookingItemResponse>of() : entity.getItems().stream().map(BookingMapper::toItemResponse).toList();
        var passengerName = entity.getPassenger() == null? null: entity.getPassenger().getFullName();
        var passengerEmail = entity.getPassenger() == null? null: entity.getPassenger().getEmail();

        return new BookingDtos.BookingResponse(entity.getId(), entity.getCreatedAt(), passengerName, passengerEmail, items);
    }

    /*----------------------------------------------------------------------------------------------------*/
    //ToEntity method is service's responsibility

    public static BookingDtos.BookingItemResponse toItemResponse(BookingItem entity) {
        return new BookingDtos.BookingItemResponse(entity.getId(), entity.getCabin().name(), entity.getPrice(), entity.getSegmentOrder(),
                entity.getFlight().getId(), entity.getFlight().getNumber());
    }

    public static void itemPatch(BookingItem entity, BookingDtos.BookingItemUpdateRequest request) {
        if (request.cabin() != null) entity.setCabin(Cabin.valueOf(request.cabin().toUpperCase()));
        if (request.price() != null) entity.setPrice(request.price());
        if (request.segmentOrder() != null) entity.setSegmentOrder(request.segmentOrder());
    }

    public static void addItem(BookingItem item, Booking booking){ booking.addItem(item); }
}
