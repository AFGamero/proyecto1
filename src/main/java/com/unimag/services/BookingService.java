package com.unimag.services;

import com.unimag.api.dto.BookingDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingCreateRequest request);
    BookingResponse getBooking(Long id);

    Page<BookingResponse> listBookingsByPassengerEmailAndOrderedMostRecently(String passenger_email,
                                                                             Pageable pageable);
    BookingResponse getBookingWithAllDetails(Long id);
    BookingResponse updateBooking(Long id, Long passenger_id);
    void deleteBooking(Long id);
}
