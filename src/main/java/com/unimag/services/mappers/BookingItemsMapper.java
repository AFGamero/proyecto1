package com.unimag.services.mappers;

import com.unimag.api.dto.BookingItemDtos;
import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.entidades.BookingItem;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.Flight;

public class BookingItemsMapper {
    public static BookingItem ToEntity(BookingItemDtos.BookingItemRequestDto request) {
        String cabinTypeName = request.cabin().name();
        Cabin cabinEnum = Cabin.valueOf(cabinTypeName.toUpperCase());

        return BookingItem.builder()
                .cabin(cabinEnum)
                .price(request.price())
                .booking(Booking.builder().id(request.bookingDtos()).build())
                .flight(Flight.builder().id(request.flightId()).build())
                .build();
    }

    public static BookingItemDtos.BookingItemResponseDto ToResponse(BookingItem bookingItem) {
        return new BookingItemDtos().BookingItemResponseDto(
                bookingItem.getId(),
                bookingItem.getBooking() != null ? bookingItem.getBooking().getId() : null,
                bookingItem.getPrice(),
                bookingItem.getSegmentOrder(),
                bookingItem.getFlight() != null ? bookingItem.getFlight().getId() : null
        );
    }

    public static void path(BookingItemDtos entity, BookingItemDtos.BookingItemRequestDto request) {
        if (request.cabin() != null) {
            entity.setCabin(Cabin.valueOf(request.cabin().toUpperCas()));
        }
        if (request.price() != null) {
            entity.setPrice(request.price());
        }
        if (request.bookingId() != null) {
            entity.setBooking(Booking.builder().id(request.bookingDtos()).build());
        }
        if (request.flightId() != null) {
            entity.setFlight(Flight.builder().id(request.flightId()).build());
        }
    }


}
