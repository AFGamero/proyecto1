package com.unimag.services;

import com.unimag.api.dto.BookingDtos.BookingCreateRequest;
import com.unimag.api.dto.BookingDtos.BookingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponse createBooking(BookingCreateRequest request);
    BookingResponse getBooking(Long id);
    BookingResponse updateBooking(Long id, Long passenger_id);
    void deleteBooking(Long id);
}
