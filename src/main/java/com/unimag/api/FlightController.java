package com.unimag.api;


import com.unimag.api.dto.FlightDtos;
import com.unimag.services.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/airlines/{airlineId}/Flights")
@RequiredArgsConstructor
@Validated
public class FlightController {

    private final FlightService service;

    @PostMapping
    public ResponseEntity<FlightDtos.FlightResponse> create (@PathVariable long airlineId,
                                                             @RequestParam Long originAirportId,
                                                             @RequestParam Long destinationAirportId,
                                                             @Valid @RequestBody FlightDtos.FlightCreateRequest request,
                                                             UriComponentsBuilder ucBuilder) {
        var body = service.create(airlineId, originAirportId, destinationAirportId, request);
        var location = ucBuilder.path("/api/airlines/{airlineId}/Flights/{id}")
                .buildAndExpand(body.id())
                .toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("{id}")
    public ResponseEntity<FlightDtos.FlightResponse> getByid (@PathVariable long id,
                                                                   @RequestParam Long originAirportId,
                                                                   @RequestParam Long destinationAirportId,
                                                                   @Valid @RequestBody FlightDtos.FlightCreateRequest request,
                                                                   UriComponentsBuilder ucBuilder) {
            var body = service.create(id, originAirportId, destinationAirportId, request);
            var location = ucBuilder.path("/api/airlines/{airlineId}/Flights/{id}")
                    .buildAndExpand(body.id())
                    .toUri();
            return ResponseEntity.created(location).body(body);
    }

    @GetMapping
    public ResponseEntity<List<FlightDtos.FlightResponse>> getAll() {
        var result = service.findAll();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public  ResponseEntity<FlightDtos.FlightResponse> update(@PathVariable Long id,
                                                             @Valid @RequestBody FlightDtos.FlightUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/tags/{tagId}")
    public ResponseEntity<FlightDtos.FlightResponse> UpdateTags(@PathVariable long id,
                                                                @PathVariable long tagId) {
        return ResponseEntity.ok(service.addTag(id, tagId));
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public ResponseEntity<FlightDtos.FlightResponse> removeTag(@PathVariable long id,
                                                                @PathVariable long tagId) {
        return ResponseEntity.ok(service.removeTag(id, tagId));
    }
}
