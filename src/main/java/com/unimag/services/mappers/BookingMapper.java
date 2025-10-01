package com.unimag.services.mappers;

import com.unimag.api.dto.BookingDtos.*;
import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.entidades.BookingItem;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    @Mapping(target = "passenger", ignore = true)
    @Mapping(target = "items", ignore = true)
    Booking toEntity(BookingCreateRequest request);

    @Mapping(source = "passenger.fullName", target = "passenger_name")
    @Mapping(source = "passenger.email", target = "passenger_email")
    @Mapping(target = "items", expression = "java(mapBookingItems(booking.getItems()))")
    BookingResponse toResponse(Booking booking);

    List<BookingResponse> toResponseList(List<Booking> bookings);

    // Mapeo manual de BookingItems
    default List<BookingItemResponse> mapBookingItems(List<BookingItem> items) {
        if (items == null) return null;
        return items.stream()
                .map(item -> new BookingItemResponse(
                        item.getId(),
                        item.getCabin().name(),
                        item.getPrice(),
                        item.getSegmentOrder(),
                        item.getBooking().getId(),
                        item.getFlight().getId(),
                        item.getFlight().getNumber()
                ))
                .collect(Collectors.toList());
    }
}
