package com.unimag.services;

import com.unimag.api.dto.AirlineDtos;
import com.unimag.dominio.entidades.Airline;
import com.unimag.dominio.repositories.AirlineRepository;
import com.unimag.services.implmnts.AirlineServiceImpl;
import com.unimag.services.mappers.AirlineMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirlineServiceImplTest {

    @Mock
    AirlineRepository repo;

    @Mock
    AirlineMapper airlineMapper;

    @InjectMocks
    AirlineServiceImpl service;

    @Test
    void shouldCreateAirlineToResponse() {
        // ARRANGE
        var request = new AirlineDtos.AirlineCreateRequest("ib", "iberia");

        var airline = Airline.builder()
                .code("ib")
                .name("iberia")
                .build();

        var savedAirline = Airline.builder()
                .id(1L)
                .code("ib")
                .name("iberia")
                .build();

        var response = new AirlineDtos.AirlineResponse(1L, "ib", "iberia");

        // Mock del mapper.toEntity()
        when(airlineMapper.toEntity(request)).thenReturn(airline);

        // Mock del repo.save()
        when(repo.save(airline)).thenReturn(savedAirline);

        // Mock del mapper.toResponse()
        when(airlineMapper.toResponse(savedAirline)).thenReturn(response);

        // ACT
        var res = service.create(request);

        // ASSERT
        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.code()).isEqualTo("ib");
        assertThat(res.name()).isEqualTo("iberia");

        verify(airlineMapper).toEntity(request);
        verify(repo).save(airline);
        verify(airlineMapper).toResponse(savedAirline);
    }

    @Test
    void shouldUpdateAirlineViaPatch() {
        // ARRANGE
        var entity = Airline.builder()
                .id(5L)
                .code("OLD")
                .name("Old Name")
                .build();

        when(repo.findById(5L)).thenReturn(Optional.of(entity));

        // Mock del patch - no hace nada, solo modifica la entidad
        doAnswer(inv -> {
            Airline a = inv.getArgument(1);
            AirlineDtos.AirlineUpdateRequest req = inv.getArgument(0);
            a.setCode(req.code());
            a.setName(req.name());
            return null;
        }).when(airlineMapper).patch(any(), any());

        var response = new AirlineDtos.AirlineResponse(5L, "NEW", "New Name");
        when(airlineMapper.toResponse(entity)).thenReturn(response);

        var updateReq = new AirlineDtos.AirlineUpdateRequest("NEW", "New Name");

        // ACT
        var updated = service.update(5L, updateReq);

        // ASSERT
        assertThat(updated.code()).isEqualTo("NEW");
        assertThat(updated.name()).isEqualTo("New Name");

        verify(repo).findById(5L);
        verify(airlineMapper).patch(updateReq, entity);
        verify(airlineMapper).toResponse(entity);
    }

    @Test
    void ShouldListAllAirlines() {
        // ARRANGE
        var airline1 = Airline.builder().id(1L).code("AA").name("American Airlines").build();
        var airline2 = Airline.builder().id(2L).code("DL").name("Delta Airlines").build();

        when(repo.findAll()).thenReturn(List.of(airline1, airline2));

        var response1 = new AirlineDtos.AirlineResponse(1L, "AA", "American Airlines");
        var response2 = new AirlineDtos.AirlineResponse(2L, "DL", "Delta Airlines");

        when(airlineMapper.toResponse(airline1)).thenReturn(response1);
        when(airlineMapper.toResponse(airline2)).thenReturn(response2);

        // ACT
        var airlines = service.findAll();

        // ASSERT
        assertThat(airlines).hasSize(2);
        assertThat(airlines).extracting(AirlineDtos.AirlineResponse::code)
                .containsExactlyInAnyOrder("AA", "DL");

        verify(repo).findAll();
        verify(airlineMapper).toResponse(airline1);
        verify(airlineMapper).toResponse(airline2);
    }
}