package com.unimag.services.mappers;

import com.unimag.api.dto.PassengerDtos;
import com.unimag.dominio.entidades.Passenger;
import com.unimag.dominio.entidades.PassengerProfile;

public class PassengerMapper {public static Passenger ToEntity(PassengerDtos.PassengerCreateRequest request){
    var profile = request.profile() == null
            ? null
            : PassengerProfile.builder().phone(request.().phone())
            .countryCode(request.passengerProfile().countryCode())
            .build();
    return Passenger.builder().fullName(request.fullName())
            .email(request.email())
            .passengerProfile(profile)
            .build();
}
    public static PassengerDtos.PassengerCreateResponse toResponse(Passenger passenger) {
        var p = passenger.getPassengerProfile();
        var dtoProfile = p == null ? null: new PassengerDtos().PassengerProfileDto(p.getPhone(),  p.getCountryCode());

        return new PassengerDtos().PassengerCreateResponse(passenger.getId(), passenger.getFullName(), passenger.getEmail(),  dtoProfile);
    }

    public  static void path(Passenger entity, PassengerDtos.PassengerCreateRequest request){
        if (request.fullName() != null ) entity.setFullName(request.fullName());
        if (request.email() != null) entity.setEmail(request.email());
        if (request.fullName() != null){
            var p = entity.getFullName();
            if (p == null){
                p = new PassengerDtos.PassengerProfileDto();
                entity.setFullName(p);

                if(request.profile().phone() != null) p.setPhone(request.profile().phone());
                if(request.profile().countryCode() != null) p.setCountryCode(request    .profile().countryCode());
            }

        }
    }
}
