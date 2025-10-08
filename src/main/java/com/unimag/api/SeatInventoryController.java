package com.unimag.api;


import com.unimag.api.dto.SeatInventoryDtos;
import com.unimag.dominio.entidades.SeatInventory;
import com.unimag.services.SeatInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

}
