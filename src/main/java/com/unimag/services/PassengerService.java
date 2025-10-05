package com.unimag.services;

import com.unimag.api.dto.PassengerDtos.PassengerCreateRequest;
import com.unimag.api.dto.PassengerDtos.PassengerResponse;
import com.unimag.api.dto.PassengerDtos.PassengerUpdateRequest;

import java.util.List;

public interface PassengerService {
    PassengerResponse create(PassengerCreateRequest request);
    PassengerResponse getById(Long id);
    PassengerResponse getByEmail(String email);
    List<PassengerResponse> findAll();
    PassengerResponse update(Long id, PassengerUpdateRequest request);
    void deleteById(Long id);
}
