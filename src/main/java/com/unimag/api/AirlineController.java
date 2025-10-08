package com.unimag.api;


import com.unimag.api.dto.AirlineDtos.*;
import com.unimag.services.AirlineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/airlines")
@RequiredArgsConstructor
@Validated
public class AirlineController {
    private final AirlineService service;

    @PostMapping
    public ResponseEntity<AirlineResponse> create(@Valid @RequestBody
                                                  AirlineCreateRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        var body = service.create(request);
        var location = uriBuilder.path("/api/airlines/{id}")
                .buildAndExpand(body.id())
                .toUri();
        return ResponseEntity.created(location).body(body);

    }

    @GetMapping("/{id}")
    public ResponseEntity<AirlineResponse> getById(@PathVariable Long id) {
        return  ResponseEntity.ok(service.findById(id));
    }
    @GetMapping
    public ResponseEntity<List<AirlineResponse>> getAll() {

        var result = service.findAll();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AirlineResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody AirlineUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
