package com.unimag.services;

import com.unimag.api.dto.AirlineDtos;
import com.unimag.dominio.entidades.Airline;
import com.unimag.dominio.repositories.AirlineRepository;
import com.unimag.services.implmnts.AirlineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirlineServiceImplTest {

    @Mock
    AirlineRepository repo;

    @InjectMocks
    AirlineServiceImpl service;


    @Test
    void shouldCreateAirlineToResponse() {
        var request = new AirlineDtos.AirlineCreateRequest("ib","iberia");
        when(repo.save(any())).thenAnswer(inv ->{
            Airline a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        var res =service.create(request);

        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.name()).isEqualTo("ib");
        assertThat(res.name()).isEqualTo("iberia");

    }

    @Test
    void shouldUpdateAirlineViaPatch() {
        var entity = Airline.builder()
                .id(5L)
                .code("OLD")
                .name("Old Name")
                .build();

        when(repo.findById(5L)).thenReturn(Optional.of(entity));

        var updateReq = new AirlineDtos.AirlineUpdateRequest("NEW", "New Name");
        var updated = service.update(5L, updateReq);

        assertThat(updated.code()).isEqualTo("NEW");
        assertThat(updated.name()).isEqualTo("New Name");
    }

    @Test
    void ShoudListAllAirlines() {
        when(repo.findAll()).thenReturn(
                java.util.List.of(
                        Airline.builder().id(1L).code("AA").name("American Airlines").build(),
                        Airline.builder().id(2L).code("DL").name("Delta Airlines").build()
                )
        );

        var airlines = service.findAll();

        assertThat(airlines).hasSize(2);
        assertThat(airlines).extracting("code").containsExactlyInAnyOrder("AA", "DL");
    }
}