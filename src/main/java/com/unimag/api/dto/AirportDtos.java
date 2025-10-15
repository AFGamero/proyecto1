package com.unimag.api.dto;

import jakarta.annotation.Nonnull;

import java.io.Serializable;

public record AirportDtos() {
    public record AirportCreateRequest(@Nonnull String code, @Nonnull String name, @Nonnull String city) implements Serializable {}
    public record AirportUpdateRequest(String code, @Nonnull String name) implements Serializable {}
    public record AirportResponse(Long id, String code, String name, String city ) implements Serializable {}

}
