package com.unimag.api.dto;

import com.unimag.dominio.entidades.Passenger;

import java.io.Serializable;

public record PassengerDtos extends Passenger {
    public record PassengerCreateRequest(
            String fullName,
            String email,
            PassengerProfileDto profile
    )implements Serializable {}

    public record PassengerCreateUpdateRequest(
            String fullName,
            String email,
            PassengerProfileDto profile
    )implements Serializable {}

    public record PassengerResponse(
            Long id,
            String fullName,
            String email,
            PassengerProfileDto profile
    )implements Serializable {}

    public record PassengerProfileDto(
            String phone,
            String countryCode
    )implements Serializable{}

}
