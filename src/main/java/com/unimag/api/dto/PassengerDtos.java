package com.unimag.api.dto;

import java.io.Serializable;

public record PassengerDtos() {
    public record PassengerCreateRequest(
            String fullName,
            String email
    )implements Serializable {}

    public record PassengerCreateUpdateRequest(
            String fullName,
            String email
    )implements Serializable {}

    public record  PassengerCreateResponse(
            Long id,
            String fullName,
            String email
    )implements Serializable {}

    public record PassengerProfileDto(
            String phone,
            String countryCode
    )implements Serializable{}

}
