    package com.unimag.api.dto;

    import com.unimag.dominio.entidades.BookingItem;

    import java.io.Serializable;
    import java.time.OffsetDateTime;
    import java.util.List;

    public record BookingDtos() {
        public record BookingCreateRequest(
                OffsetDateTime createdAt,
                PassengerDtos passengerDtos,
                List<BookingItemDtos.BookingItemRequestDto> items
        )implements Serializable {}


    public record BookingCreateResponse(
            Long id,
            OffsetDateTime createdAt,
            PassengerDtos passenger,
            List<BookingItemDtos.BookingItemResponseDto> items
    )implements Serializable {}

    public record BookingUpdateRequest(
            OffsetDateTime createdAt,
            PassengerDtos passenger,
            List<BookingItemDtos.BookingItemRequestDto> items
    )implements Serializable {}


    }
