package com.unimag.services;

import com.unimag.api.dto.PassengerDtos.PassengerResponse;

import com.unimag.api.dto.PassengerDtos.PassengerCreateRequest;


import com.unimag.api.dto.PassengerDtos.PassengerCreateUpdateRequest;


import java.awt.print.Pageable;
import java.util.List;

public interface PassengerService {

    PassengerResponse create(PassengerCreateRequest request);
    PassengerResponse update(Long ID, PassengerCreateUpdateRequest request);
    PassengerResponse getById(Long id);

    List<PassengerResponse> List(Pageable pagination);

    void  delete(Long id);
}
