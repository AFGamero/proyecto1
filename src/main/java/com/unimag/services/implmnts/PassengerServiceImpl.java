package com.unimag.services.implmnts;


import com.unimag.api.dto.PassengerDtos;
import com.unimag.dominio.entidades.Passenger;
import com.unimag.dominio.repositories.PassengerRepository;
import com.unimag.services.PassengerService;
import com.unimag.services.mappers.PassengerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PassengerServiceImpl implements PassengerService {
    @Override
    public PassengerDtos.PassengerResponse create(PassengerDtos.PassengerCreateRequest request) {
        Passenger saved = repo.save(PassengerMapper.toEntity(request));
        return PassengerMapper.toResponse(saved);

    }

    @Override
    public PassengerDtos.PassengerResponse update(Long ID, PassengerDtos.PassengerCreateUpdateRequest request) {
       Passenger passenger = repo.findById(ID)
               .orElseThrow(() -> new RuntimeException("Passenger with id " + ID + " not found"));
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerDtos.PassengerResponse getByEmail(String email) {
        return repo.findByEmailIgnoreCase(email).map(PassengerMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Passenger with email " + email + " not found"));
    }

    @Override
    public List<PassengerDtos.PassengerResponse> findAll() {
        //To Do
        return List.of();
    }

    @Override
    public PassengerDtos.PassengerResponse getById(Long id) {
        return repo.findById(id).map(PassengerMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Passenger with id " + id + " not found"));
    }

    @Override
    public void deleteById(Long id) {
        //To Do
    }


    private final PassengerRepository repo;


}
