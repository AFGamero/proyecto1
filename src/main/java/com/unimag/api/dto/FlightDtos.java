package com.unimag.api.dto;

import jakarta.annotation.Nonnull;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

public record FlightDtos() {
    public record FlightCreateRequest(@Nonnull String number, @Nonnull OffsetDateTime departureTime, @Nonnull OffsetDateTime arrivalTime) implements Serializable {}

    public record FlightUpdateRequest(String number, @Nonnull OffsetDateTime departureTime, @Nonnull OffsetDateTime arrivalTime, Long destination_airport_id) implements Serializable {}

    //IDK if these are the right parameters
    public record FlightResponse(Long id, String number, OffsetDateTime departureTime, OffsetDateTime arrivalTime,
                                 Long airline_id, Long origin_airport_id, Long destination_airport_id,
                                 Set<TagDtos.TagResponse> tags) implements Serializable {}
}
