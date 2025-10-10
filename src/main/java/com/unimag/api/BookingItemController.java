package com.unimag.api;

import com.unimag.api.dto.BookingDtos.*;
import com.unimag.services.BookingItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings/{bookingId}/items")
@Validated
public class BookingItemController {
    private final BookingItemService bookingItemService;

    @PostMapping("/flight/{flightId}")
    public ResponseEntity<BookingItemResponse> addItem(@PathVariable Long bookingId,
                                                       @PathVariable Long flightId,
                                                       @RequestBody @Valid BookingItemCreateRequest req,
                                                       UriComponentsBuilder ucBuilder) {
    var body = bookingItemService.addItem(bookingId, flightId, req);
    var location = ucBuilder.path("/api/v1/bookings/{bookingId}/items/{itemId}").buildAndExpand(bookingId, body.id()).toUri();
    return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<BookingItemResponse> getBookingItem(@PathVariable Long itemId, @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingItemService.getBookingItem(itemId));
    }

    @GetMapping
    public ResponseEntity<List<BookingItemResponse>> listByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingItemService.listByBooking(bookingId));
    }

    @PatchMapping("/{itemId}/flight/{flightId}")
    public ResponseEntity<BookingItemResponse> updateItem(@PathVariable Long bookingId,
                                                          @PathVariable Long itemId,
                                                          @PathVariable Long flightId,
                                                          @RequestBody @Valid BookingItemUpdateRequest req,
                                                          UriComponentsBuilder ucBuilder) {
        var body = bookingItemService.updateItem(itemId, flightId, req);

        var location = ucBuilder.path("/api/v1/bookings/{bookingId}/items/{itemId}").buildAndExpand(bookingId, body.id()).toUri();
        return ResponseEntity.ok().location(location).body(body);
    }

    @DeleteMapping("/{itemId}") //BookingID porque me lo pedia
    public ResponseEntity<Void> deleteBookingItem(@PathVariable Long itemId, @PathVariable Long bookingId) {
        bookingItemService.deleteBookingItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
