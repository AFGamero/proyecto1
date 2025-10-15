package com.unimag.api.dto;

import jakarta.annotation.Nonnull;

import java.io.Serializable;
import java.util.Set;

public record TagDtos() {
    public record TagCreateRequest(@Nonnull String name) implements Serializable {}
    public record TagResponse(Long id, String name) implements Serializable{}

}
