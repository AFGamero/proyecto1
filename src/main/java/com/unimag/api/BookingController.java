package com.unimag.api;

import com.unimag.api.dto.BookingDtos.*;
import com.unimag.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Validated

public class BookingController {
    private final BookingService service;
    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingCreateRequest req, UriComponentsBuilder builder)
    {
        var body = service.createBooking(req);
        var location = builder.path("/api/v1/bookings/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id)
    {
        return ResponseEntity.ok(service.getBooking(id));
    }

    @PatchMapping("/{id}/passenger/{passengerId}")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable Long id,
                                                         @PathVariable Long passengerId,
                                                         UriComponentsBuilder uriBuilder) {
        var body = service.updateBooking(id, passengerId);
        var location = uriBuilder.path("/api/v1/bookings/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.ok().location(location).body(body);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
