package com.unimag.api;


import com.unimag.api.dto.SeatInventoryDtos;
import com.unimag.services.SeatInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/flights/{flightId}/seat-inventories")
@RequiredArgsConstructor
@Validated
public class SeatInventoryController {
    private final SeatInventoryService service;

    @PostMapping
    public ResponseEntity<String> addSeat(@PathVariable long flightId,
                                          @Valid @RequestBody
                                          SeatInventoryDtos.SeatInventoryCreateRequest request,
                                          UriComponentsBuilder uriBuilder) {

        var body = service.create(flightId, request );
        var location = uriBuilder.path("/api/flights/{flightId}/seat-inventories")
                .buildAndExpand(body.id())
                .toUri();

        return ResponseEntity.created(location).body("Seat inventory created with ID: " + body.id());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatInventoryDtos.SeatInventoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<SeatInventoryDtos.SeatInventoryResponse>> getByFlightId(@PathVariable Long flightId) {
        var result = service.findByFlightId(flightId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<SeatInventoryDtos.SeatInventoryResponse> getByFlightIdAndCabin(
            @PathVariable Long flightId,
            @RequestParam String cabin) {
        var result = service.findByFlightAndCabin(flightId, cabin);
        return ResponseEntity.ok(result);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<SeatInventoryDtos.SeatInventoryResponse> update(@PathVariable Long id,
                                                                         @Valid @RequestBody SeatInventoryDtos.SeatInventoryUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
