package com.unimag.services;

import com.unimag.api.dto.BookingDtos.*;
import java.math.BigDecimal;
import java.util.List;

public interface BookingService {
    BookingResponse create(BookingCreateRequest request);
    BookingResponse findById(Long id);
    List<BookingResponse> findAll();
    List<BookingResponse> findByPassengerId(Long passengerId);
    void deleteById(Long id);
    BigDecimal calculateTotal(Long bookingId);
}
