package com.unimag.api;


import com.unimag.api.dto.TagDtos;
import com.unimag.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Validated
public class TagController {
    private final TagService service;

    @PostMapping
    public ResponseEntity<TagDtos.TagResponse> create(
                                                     @Valid @RequestBody TagDtos.TagCreateRequest request,
                                                     UriComponentsBuilder uriBuilder) {
        var body = service.create(request);
        var location = uriBuilder.path("/api/tags/{id}")
                .buildAndExpand(body.id())
                .toUri();
        return ResponseEntity.created(location).body(body);

    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDtos.TagResponse> getById(@PathVariable Long id ) {
        return  ResponseEntity.ok(service.findById(id));

    }

    @GetMapping
    public ResponseEntity<TagDtos.TagResponse> getByName(@RequestParam String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
