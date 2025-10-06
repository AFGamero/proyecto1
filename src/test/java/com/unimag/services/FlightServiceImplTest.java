package com.unimag.services;

import com.unimag.api.dto.FlightDtos.*;
import com.unimag.api.dto.TagDtos;
import com.unimag.dominio.entidades.*;
import com.unimag.dominio.repositories.*;
import com.unimag.services.implmnts.FlightServiceImpl;
import com.unimag.services.mappers.FlightMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock FlightRepository flightRepo;
    @Mock AirlineRepository airlineRepo;
    @Mock AirportRepository airportRepo;
    @Mock TagRepository tagRepo;
    @Mock FlightMapper flightMapper;

    @InjectMocks
    FlightServiceImpl service;

    @Test
    void shouldCreateFlightWithRelations() {
        // ARRANGE
        var now = OffsetDateTime.now();
        var airline = Airline.builder().id(1L).code("AV").name("Avianca").build();
        var origin = Airport.builder().id(10L).code("BOG").name("El Dorado").build();
        var destination = Airport.builder().id(20L).code("MDE").name("Rionegro").build();

        var request = new FlightCreateRequest("AV123", now, now.plusHours(1));

        var flightToSave = Flight.builder().number("AV123").departureTime(now).arrivalTime(now.plusHours(1)).build();

        var savedFlight = Flight.builder().id(100L).number("AV123").departureTime(now).arrivalTime(now.plusHours(1))
                .airline(airline).origin(origin).destination(destination).tags(new HashSet<>()).build();

        var expectedResponse = new FlightResponse(
                100L, "AV123", now, now.plusHours(1), 1L, 10L, 20L, Set.of()
        );

        when(airlineRepo.findById(1L)).thenReturn(Optional.of(airline));
        when(airportRepo.findById(10L)).thenReturn(Optional.of(origin));
        when(airportRepo.findById(20L)).thenReturn(Optional.of(destination));

        when(flightMapper.toEntity(request)).thenReturn(flightToSave);

        when(flightRepo.save(any(Flight.class))).thenReturn(savedFlight);

        when(flightMapper.toResponse(savedFlight)).thenReturn(expectedResponse);

        // ACT
        var res = service.create(1L, 10L, 20L, request);

        // ASSERT
        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(100L);
        assertThat(res.number()).isEqualTo("AV123");
        assertThat(res.airline_id()).isEqualTo(1L);
        assertThat(res.origin_airport_id()).isEqualTo(10L);
        assertThat(res.destination_airport_id()).isEqualTo(20L);

        verify(airlineRepo).findById(1L);
        verify(airportRepo).findById(10L);
        verify(airportRepo).findById(20L);
        verify(flightMapper).toEntity(request);
        verify(flightRepo).save(any(Flight.class));
        verify(flightMapper).toResponse(savedFlight);
    }

    @Test
    void shouldUpdateFlightViaPatch() {
        // ARRANGE
        var now = OffsetDateTime.now();
        var entity = Flight.builder()
                .id(50L)
                .number("OLD123")
                .departureTime(now)
                .arrivalTime(now.plusHours(2))
                .tags(new HashSet<>())
                .build();

        var updateReq = new FlightUpdateRequest(
                "NEW456",
                now.plusHours(1),
                now.plusHours(3),
                50L
        );

        // Mock: Buscar vuelo existente
        when(flightRepo.findById(50L)).thenReturn(Optional.of(entity));

        // Mock: Patch modifica la entidad
        doAnswer(inv -> {
            FlightUpdateRequest req = inv.getArgument(0);
            Flight flight = inv.getArgument(1);
            if (req.number() != null) flight.setNumber(req.number());
            flight.setDepartureTime(req.departureTime());
            flight.setArrivalTime(req.arrivalTime());
            return null;
        }).when(flightMapper).patch(any(), any());

        // Mock: Repository guarda la entidad modificada
        when(flightRepo.save(entity)).thenReturn(entity);

        // Mock: Mapper convierte a response
        var expectedResponse = new FlightResponse(
                50L,
                "NEW456",
                now.plusHours(1),
                now.plusHours(3),
                null,
                null,
                50L,
                Set.of()
        );
        when(flightMapper.toResponse(entity)).thenReturn(expectedResponse);

        // ACT
        var updated = service.update(50L, updateReq);

        // ASSERT
        assertThat(updated).isNotNull();
        assertThat(updated.number()).isEqualTo("NEW456");
        assertThat(updated.departureTime()).isEqualTo(now.plusHours(1));
        assertThat(updated.arrivalTime()).isEqualTo(now.plusHours(3));

        verify(flightRepo).findById(50L);
        verify(flightMapper).patch(updateReq, entity);
        verify(flightRepo).save(entity);
        verify(flightMapper).toResponse(entity);
    }

    @Test
    void shouldFindFlightById() {
        // ARRANGE
        var now = OffsetDateTime.now();
        var flight = Flight.builder()
                .id(30L).number("FL123").departureTime(now).arrivalTime(now.plusHours(2)).tags(new HashSet<>()).build();

        when(flightRepo.findById(30L)).thenReturn(Optional.of(flight));

        var expectedResponse = new FlightResponse(
                30L, "FL123", now, now.plusHours(2), null, null, null, Set.of()
        );
        when(flightMapper.toResponse(flight)).thenReturn(expectedResponse);

        // ACT
        var res = service.findById(30L);

        // ASSERT
        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(30L);
        assertThat(res.number()).isEqualTo("FL123");

        verify(flightRepo).findById(30L);
        verify(flightMapper).toResponse(flight);
    }

    @Test
    void shouldFindAllFlights() {
        // ARRANGE
        var now = OffsetDateTime.now();
        var flight1 = Flight.builder()
                .id(1L).number("FL001").departureTime(now).arrivalTime(now.plusHours(1)).tags(new HashSet<>()).build();

        var flight2 = Flight.builder()
                .id(2L).number("FL002").departureTime(now.plusHours(2)).arrivalTime(now.plusHours(3)).tags(new HashSet<>()).build();

        when(flightRepo.findAll()).thenReturn(List.of(flight1, flight2));

        var response1 = new FlightResponse(1L, "FL001", now, now.plusHours(1), null, null, null, Set.of());
        var response2 = new FlightResponse(2L, "FL002", now.plusHours(2), now.plusHours(3), null, null, null, Set.of());

        when(flightMapper.toResponse(flight1)).thenReturn(response1);
        when(flightMapper.toResponse(flight2)).thenReturn(response2);

        // ACT
        var flights = service.findAll();

        // ASSERT
        assertThat(flights).hasSize(2);
        assertThat(flights).extracting(FlightResponse::number)
                .containsExactly("FL001", "FL002");

        verify(flightRepo).findAll();
        verify(flightMapper).toResponse(flight1);
        verify(flightMapper).toResponse(flight2);
    }

    @Test
    void shouldAddTagToFlight() {
        // ARRANGE
        var flight = Flight.builder()
                .id(30L).number("FL123").tags(new HashSet<>()).build();

        var tag = Tag.builder()
                .id(5L).name("Direct").flights(new HashSet<>()).build();

        // Mock: Buscar vuelo y tag
        when(flightRepo.findById(30L)).thenReturn(Optional.of(flight));
        when(tagRepo.findById(5L)).thenReturn(Optional.of(tag));

        // Mock: Mapper convierte a response con el tag agregado
        var tagResponse = new TagDtos.TagResponse(5L, "Direct");
        var expectedResponse = new FlightResponse(
                30L, "FL123", null, null, null,
                null, null, Set.of(tagResponse)
        );
        when(flightMapper.toResponse(flight)).thenReturn(expectedResponse);

        // ACT
        var res = service.addTag(30L, 5L);

        // ASSERT
        assertThat(res).isNotNull();
        assertThat(res.tags()).hasSize(1);
        assertThat(res.tags()).extracting(TagDtos.TagResponse::name)
                .contains("Direct");

        verify(flightRepo).findById(30L);
        verify(tagRepo).findById(5L);
        verify(flightMapper).toResponse(flight);
    }

    @Test
    void shouldRemoveTagFromFlight() {
        // ARRANGE
        var tag = Tag.builder().id(5L).name("Direct").flights(new HashSet<>()).build();

        var flight = Flight.builder().id(30L).number("FL123").tags(new HashSet<>(Set.of(tag))).build();

        tag.getFlights().add(flight);

        // Mock: Buscar vuelo y tag
        when(flightRepo.findById(30L)).thenReturn(Optional.of(flight));
        when(tagRepo.findById(5L)).thenReturn(Optional.of(tag));

        // Mock: Mapper convierte a response sin tags
        var expectedResponse = new FlightResponse(
                30L, "FL123", null, null, null, null, null, Set.of()
        );
        when(flightMapper.toResponse(flight)).thenReturn(expectedResponse);

        // ACT
        var res = service.removeTag(30L, 5L);

        // ASSERT
        assertThat(res).isNotNull();
        assertThat(res.tags()).isEmpty();

        verify(flightRepo).findById(30L);
        verify(tagRepo).findById(5L);
        verify(flightMapper).toResponse(flight);
    }

    @Test
    void shouldDeleteFlightById() {
        // ARRANGE
        // Mock: Repository elimina el vuelo
        doNothing().when(flightRepo).deleteById(50L);

        // ACT
        service.deleteById(50L);

        // ASSERT
        verify(flightRepo).deleteById(50L);
    }
}