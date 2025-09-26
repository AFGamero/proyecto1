package com.unimag.api.dto;

import java.io.Serializable;

public record AirportDtos() {
    public record AirportCreateRequest(
            String name,
            String code,
            String city
    )implements Serializable {}

    public record  AirportResponse(
            Long id,
            String name,
            String code,
            String city
    )implements Serializable {}

    public record  AirportUpdateRequest(
            String name,
            String code,
            String city
    )implements Serializable {}

}
