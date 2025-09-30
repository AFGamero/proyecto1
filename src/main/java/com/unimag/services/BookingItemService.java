package com.unimag.services;

import com.unimag.api.dto.BookingDtos.*;
import java.util.List;

public interface BookingItemService {
    BookingItemResponse create(Long bookingId, Long flightId, BookingItemCreateRequest request);
    BookingItemResponse findById(Long id);
    List<BookingItemResponse> findByBookingId(Long bookingId);
    BookingItemResponse update(Long id, BookingItemUpdateRequest request);
    void deleteById(Long id);
}
