package com.unimag.services.mappers;

import com.unimag.api.dto.BookingDtos;
import com.unimag.api.dto.BookingDtos.BookingCreateRequest;
import com.unimag.api.dto.BookingDtos.BookingItemResponse;
import com.unimag.api.dto.BookingDtos.BookingResponse;
import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.entidades.BookingItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    @Mapping(target = "passenger", ignore = true)
    @Mapping(target = "items", ignore = true)
    Booking toEntity(BookingCreateRequest request);

    @Mapping(target = "passenger_name", expression = "java(booking.getPassenger() != null ? booking.getPassenger().getFullName() : null)")
    @Mapping(target = "passenger_email", expression = "java(booking.getPassenger() != null ? booking.getPassenger().getEmail() : null)")
    @Mapping(target = "items", expression = "java(mapBookingItems(booking.getItems()))")
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
