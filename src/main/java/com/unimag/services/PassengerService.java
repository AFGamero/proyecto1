package com.unimag.services;

import com.unimag.api.dto.PassengerDtos.PassengerResponse;

import com.unimag.api.dto.PassengerDtos.PassengerCreateRequest;


import com.unimag.api.dto.PassengerDtos.PassengerCreateUpdateRequest;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PassengerService {

    PassengerResponse create(PassengerCreateRequest request);
    PassengerResponse update(Long ID, PassengerCreateUpdateRequest request);
    PassengerResponse getByEmail(String email);
    PassengerResponse getById(Long id);

    List<PassengerResponse> List(Pageable pagination);

    void  delete(Long id);
}
