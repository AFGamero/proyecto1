package com.unimag.api.dto;

import com.unimag.dominio.entidades.Cabin;

import java.math.BigDecimal;

public record BookingItemDtos() {

    public record BookingItemRequestDto(
            Cabin cabin,
            BigDecimal price,
            Integer segmentOrder,
            BookingDtos bookingDtos

    ) {
    }

    public record BookingItemResponseDto(
            Long id,
            Cabin cabin,
            BigDecimal price,
            Integer segmentOrder,
            BookingDtos bookingResponseDto
    ) {
    }

}
