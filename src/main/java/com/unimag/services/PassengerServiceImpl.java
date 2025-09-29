package com.unimag.services;


import com.unimag.api.dto.PassengerDtos;
import com.unimag.dominio.entidades.Passenger;
import com.unimag.dominio.repositories.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PassengerServiceImpl  implements PassengerService {
    private final PassengerRepository repo;

    @Override public PassengerDtos.PassengerResponse create (PassengerDtos.PassengerCreateRequest req) {
        Passenger passenger = repo.save();
    }
}
