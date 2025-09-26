package com.unimag.api.dto;

import com.unimag.dominio.entidades.Cabin;

import java.io.Serializable;

public record CabinDtos() {
    public record CabinRequest(Cabin cabin) implements Serializable {}
    public record CabinResponse(Cabin cabin) implements Serializable {}
}
