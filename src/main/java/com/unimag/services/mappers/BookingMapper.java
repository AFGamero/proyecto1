package com.unimag.services.mappers;

import com.unimag.api.dto.BookingDtos;
import com.unimag.api.dto.BookingDtos.BookingCreateRequest;
import com.unimag.api.dto.BookingDtos.BookingItemResponse;
import com.unimag.api.dto.BookingDtos.BookingResponse;
import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.entidades.BookingItem;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface BookingMapper {
    // Booking
    @Mapping(source = "passenger.fullName", target = "passenger_name")
    @Mapping(source = "passenger.email", target = "passenger_email")
    @Mapping(source = "items", target = "items")
    BookingResponse toResponse(Booking booking);

    //Booking Item
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "flight", ignore = true)
    BookingItem toItemEntity(BookingDtos.BookingItemCreateRequest request);

    @Mapping(source = "booking.id", target = "booking_id")
    @Mapping(source = "flight.id", target = "flight_id")
    @Mapping(source = "flight.number", target = "flight_number")
    BookingItemResponse toItemResponse(BookingItem item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "flight", ignore = true)
    void patch(BookingDtos.BookingItemUpdateRequest request, @MappingTarget BookingItem item);

}