package com.unimag.services;

import com.unimag.api.dto.BookingDtos.*;
import java.util.List;

public interface BookingItemService {
    //metodos crud para BookingItem
    BookingItemResponse addItem(Long bookingId, Long flightId, BookingItemCreateRequest req);
    BookingItemResponse getBookingItem(Long id);
    void deleteBookingItem(Long id);
    List<BookingItemResponse> listByBooking(Long bookingId);

    BookingItemResponse updateItem(Long bookingId,Long flightId ,BookingItemUpdateRequest req);

    void removeItem(Long itemId);
}
