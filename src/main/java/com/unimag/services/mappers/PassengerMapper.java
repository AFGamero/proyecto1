package com.unimag.services.mappers;

import com.unimag.api.dto.PassengerDtos;
import com.unimag.dominio.entidades.Passenger;
import com.unimag.dominio.entidades.PassengerProfile;

public class PassengerMapper {
    public static Passenger toentity(PassengerDtos.PassengerCreateRequest req) {
        var profile = req.profile() == null ? null :
                PassengerProfile.builder().phone(req.profile().phone())
                .countryCode(req.profile().countryCode()).build();

        return Passenger.builder().fullName(req.fullName())
                .email(req.email()).passengerProfile(profile).build();
    }

    public static PassengerDtos.PassengerResponse toResponse(Passenger entity) {
        var profile = entity.getPassengerProfile() == null ? null :
                new PassengerDtos.PassengerProfileDto(entity.getPassengerProfile().getPhone(),
                        entity.getPassengerProfile().getCountryCode());
        return new PassengerDtos.PassengerResponse(entity.getId(), entity.getFullName(),
                entity.getEmail(), profile);
    }

    public static void path(Passenger entity, PassengerDtos.PassengerCreateUpdateRequest req) {
        if (req.fullName() != null) entity.setFullName(req.fullName());
        if (req.email() != null) entity.setEmail(req.email());
        if (req.profile() != null) {
            var profile = entity.getPassengerProfile();
            if (profile == null) {
                profile = new PassengerProfile();
                entity.setPassengerProfile(profile);
            }
            if (req.profile().phone() != null) profile.setPhone(req.profile().phone());
            if (req.profile().countryCode() != null) profile.setCountryCode(req.profile().countryCode());
        }
    }
}
