package com.unimag.services.mappers;

import com.unimag.api.dto.BookingDtos;
import com.unimag.api.dto.BookingItemDtos;
import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.entidades.Passenger;

import java.util.List;

public class BookingMapper {
    public static Booking ToEntity(BookingDtos.BookingCreateRequest request, Passenger passenger, List<BookingItemDtos> items) {
        return Booking.builder().createdAt(request.createdAt())
                .passenger(passenger).bookingsItems(items).build();
    }
    public static BookingDtos.BookingCreateResponse toResponse(Booking entity) {
        return new BookingDtos.BookingCreateResponse(entity.getId(), entity.getCreatedAt(),
                entity.getBookingsItems().stream().map(BookingItemsMapper::ToResponse).toList(),
                entity.getPassenger().getId());
    }
    public static void patch(Booking entity, BookingDtos.BookingUpdateRequest request, Passenger passenger, List<BookingItemDtos> items) {
        if (request.updatedAt() != null) entity.setCreatedAt(request.updatedAt());
        if (request.passengerId() != null) entity.setPassenger(passenger);
        if (request.BookItemId() != null && items != null && !items.isEmpty()) entity.setItems(items);
    }
}
