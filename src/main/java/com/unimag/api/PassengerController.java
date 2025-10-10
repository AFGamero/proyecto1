package com.unimag.api;

import com.unimag.api.dto.PassengerDtos.*;
import com.unimag.services.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@RestController
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
@Validated
public class PassengerController {

    private final PassengerService service;

    @PostMapping
    public ResponseEntity<PassengerResponse> create(@Valid @RequestBody PassengerCreateRequest req, UriComponentsBuilder uriBuilder) {
        var body = service.create(req);
        var location = uriBuilder.path("/api/v1/passengers/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/by-email")
    public ResponseEntity<PassengerResponse> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.getByEmail(email));
    }

    @GetMapping()
    public ResponseEntity<List<PassengerResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PassengerResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody PassengerUpdateRequest req) {
        return ResponseEntity.ok(service.update(id,req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
