package com.unimag.services;

import com.unimag.api.dto.FlightDtos.*;
import com.unimag.dominio.entidades.*;
import com.unimag.dominio.repositories.*;
import com.unimag.services.implmnts.FlightServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock FlightRepository flightRepo;
    @Mock AirlineRepository airlineRepo;
    @Mock AirportRepository airportRepo;
    @Mock TagRepository tagRepo;

    @InjectMocks
    FlightServiceImpl service;

    @Test
    void shouldCreateFlightWithRelations() {
        var now = OffsetDateTime.now();
        var airline = Airline.builder().id(1L).code("AV").name("Avianca").build();
        var origin = Airport.builder().id(10L).code("BOG").name("El Dorado").build();
        var destination = Airport.builder().id(20L).code("MDE").name("Rionegro").build();

        when(airlineRepo.findById(1L)).thenReturn(Optional.of(airline));
        when(airportRepo.findById(10L)).thenReturn(Optional.of(origin));
        when(airportRepo.findById(20L)).thenReturn(Optional.of(destination));
        when(flightRepo.save(any())).thenAnswer(inv -> {
            Flight f = inv.getArgument(0);
            f.setId(100L);
            return f;
        });

        var req = new FlightCreateRequest("AV123", now, now.plusHours(1));
        var res = service.create(1L, 10L, 20L, req);

        assertThat(res.id()).isEqualTo(100L);
        assertThat(res.number()).isEqualTo("AV123");
        assertThat(res.airline_id()).isEqualTo(1L);
        assertThat(res.origin_airport_id()).isEqualTo(10L);
        assertThat(res.destination_airport_id()).isEqualTo(20L);
    }

    @Test
    void shouldUpdateFlightViaPatch() {
        var now = OffsetDateTime.now();
        var entity = Flight.builder()
                .id(50L)
                .number("OLD123")
                .departureTime(now)
                .arrivalTime(now.plusHours(2))
                .build();

        when(flightRepo.findById(50L)).thenReturn(Optional.of(entity));

        var updateReq = new FlightUpdateRequest("NEW456",now.plusHours(1),now.plusHours(10),50l);
        var updated = service.update(50L, updateReq);

        assertThat(updated.number()).isEqualTo("NEW456");
        assertThat(updated.arrivalTime()).isEqualTo(now.plusHours(3));
    }

    @Test
    void shouldAddTagToFlight() {
        var flight = Flight.builder()
                .id(30L)
                .number("FL123")
                .tags(new HashSet<>())
                .build();

        var tag = Tag.builder()
                .id(5L)
                .name("Direct")
                .flights(new HashSet<>())
                .build();

        when(flightRepo.findById(30L)).thenReturn(Optional.of(flight));
        when(tagRepo.findById(5L)).thenReturn(Optional.of(tag));

        var res = service.addTag(30L, 5L);

        assertThat(res.tags()).hasSize(1);
        assertThat(res.tags()).extracting("name").contains("Direct");
    }


}