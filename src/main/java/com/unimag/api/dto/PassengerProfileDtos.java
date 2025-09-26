package com.unimag.api.dto;

import java.io.Serializable;

public record PassengerProfileDtos() {

    public record PassengerProfileCreateRequest(
         String phone,
            String countryCode
    )implements Serializable {
    }
    public record PassengerProfileUpdateRequest(
            String phone,
                String countryCode
    )implements Serializable {
    }
    public record PassengerProfileCreateResponse(
            Long id,
            String phone,
            String countryCode
    )implements  Serializable {
    }
}
