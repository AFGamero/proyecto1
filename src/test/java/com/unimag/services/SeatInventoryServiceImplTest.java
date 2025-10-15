package com.unimag.services;

import com.unimag.api.dto.SeatInventoryDtos.*;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.entidades.SeatInventory;
import com.unimag.dominio.repositories.FlightRepository;
import com.unimag.dominio.repositories.SeatInventoryRepository;
import com.unimag.exception.NotFoundException;
import com.unimag.services.implmnts.SeatInventoryServiceImpl;
import com.unimag.services.mappers.SeatInventoryMapper;
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
class SeatInventoryServiceImplTest {

    @Mock
    SeatInventoryRepository repo;

    @Mock
    SeatInventoryMapper seatInventoryMapper;

    @Mock
    FlightRepository flightRepo;

    @InjectMocks
    SeatInventoryServiceImpl service;

    @Test
    void shouldCreateSeatInventoryAndMapToResponse() {
        // ARRANGE
        var flight = Flight.builder()
                .id(10L)
                .number("AV123")
                .build();

        var seatInventory = SeatInventory.builder()
                .cabin(Cabin.ECONOMY)
                .totalSeats(150)
                .availableSeats(150)
                .flight(flight)
                .build();

        var savedSeatInventory = SeatInventory.builder()
                .id(100L)
                .cabin(Cabin.ECONOMY)
                .totalSeats(150)
                .availableSeats(150)
                .flight(flight)
                .build();

        var expectedResponse = new SeatInventoryResponse(100L, "ECONOMY", 150, 150, 10L);

        when(flightRepo.findById(10L)).thenReturn(Optional.of(flight));
        when(repo.findByFlightIdAndCabin(10L, Cabin.ECONOMY)).thenReturn(Optional.empty());
        when(seatInventoryMapper.toEntity(any(SeatInventoryCreateRequest.class))).thenReturn(seatInventory);
        when(repo.save(seatInventory)).thenReturn(savedSeatInventory);
        when(seatInventoryMapper.toResponse(savedSeatInventory)).thenReturn(expectedResponse);

        var request = new SeatInventoryCreateRequest("ECONOMY", 150, 150);

        // ACT
        var response = service.create(10L, request);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.cabin()).isEqualTo("ECONOMY");
        assertThat(response.totalSeats()).isEqualTo(150);
        assertThat(response.availableSeats()).isEqualTo(150);
        assertThat(response.flight_id()).isEqualTo(10L);

        verify(flightRepo).findById(10L);
        verify(repo).findByFlightIdAndCabin(10L, Cabin.ECONOMY);
        verify(seatInventoryMapper).toEntity(any(SeatInventoryCreateRequest.class));
        verify(repo).save(seatInventory);
        verify(seatInventoryMapper).toResponse(savedSeatInventory);
    }


    @Test
    void shouldFindSeatInventoryById() {
        // ARRANGE
        var seatInventory = SeatInventory.builder()
                .id(20L)
                .cabin(Cabin.ECONOMY)
                .totalSeats(200)
                .availableSeats(150)
                .build();

        var expectedResponse = new SeatInventoryResponse(20L, "ECONOMY", 200, 150, null);

        when(repo.findById(20L)).thenReturn(Optional.of(seatInventory));
        when(seatInventoryMapper.toResponse(seatInventory)).thenReturn(expectedResponse);

        // ACT
        var response = service.findById(20L);

        // ASSERT
        assertThat(response.id()).isEqualTo(20L);
        assertThat(response.cabin()).isEqualTo("ECONOMY");
        assertThat(response.totalSeats()).isEqualTo(200);
        assertThat(response.availableSeats()).isEqualTo(150);

        verify(repo).findById(20L);
        verify(seatInventoryMapper).toResponse(seatInventory);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSeatInventoryNotFound() {
        // ARRANGE
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SeatInventory 999 not found");

        verify(repo).findById(999L);
        verifyNoInteractions(seatInventoryMapper);
    }

    @Test
    void shouldFindByFlightAndCabin() {
        // ARRANGE
        var seatInventory = SeatInventory.builder()
                .id(30L)
                .cabin(Cabin.BUSINESS)
                .totalSeats(50)
                .availableSeats(40)
                .build();

        var expectedResponse = new SeatInventoryResponse(30L, "BUSINESS", 50, 40, null);

        when(flightRepo.existsById(15L)).thenReturn(true);
        when(repo.findByFlightIdAndCabin(15L, Cabin.BUSINESS)).thenReturn(Optional.of(seatInventory));
        when(seatInventoryMapper.toResponse(seatInventory)).thenReturn(expectedResponse);

        // ACT
        var response = service.findByFlightAndCabin(15L, "BUSINESS");

        // ASSERT
        assertThat(response.id()).isEqualTo(30L);
        assertThat(response.cabin()).isEqualTo("BUSINESS");
        assertThat(response.totalSeats()).isEqualTo(50);
        assertThat(response.availableSeats()).isEqualTo(40);

        verify(flightRepo).existsById(15L);
        verify(repo).findByFlightIdAndCabin(15L, Cabin.BUSINESS);
        verify(seatInventoryMapper).toResponse(seatInventory);
    }


    @Test
    void shouldThrowExceptionWhenSeatInventoryNotFoundForFlightAndCabin() {
        // ARRANGE
        when(flightRepo.existsById(10L)).thenReturn(true);
        when(repo.findByFlightIdAndCabin(10L, Cabin.BUSINESS)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.findByFlightAndCabin(10L, "BUSINESS"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SeatInventory for flight 10 and cabin BUSINESS not found");

        verify(flightRepo).existsById(10L);
        verify(repo).findByFlightIdAndCabin(10L, Cabin.BUSINESS);
    }

    @Test
    void shouldFindAllCabinsByFlightId() {
        // ARRANGE
        var seatInventory1 = SeatInventory.builder()
                .id(1L).cabin(Cabin.ECONOMY).totalSeats(200).availableSeats(150).build();
        var seatInventory2 = SeatInventory.builder()
                .id(2L).cabin(Cabin.BUSINESS).totalSeats(50).availableSeats(30).build();

        var response1 = new SeatInventoryResponse(1L, "ECONOMY", 200, 150,23l);
        var response2 = new SeatInventoryResponse(2L, "BUSINESS", 50, 30,30l);

        when(flightRepo.existsById(20L)).thenReturn(true);
        when(repo.findByFlightIdAndCabin(20L, Cabin.ECONOMY)).thenReturn(Optional.of(seatInventory1));
        when(repo.findByFlightIdAndCabin(20L, Cabin.BUSINESS)).thenReturn(Optional.of(seatInventory2));
        when(repo.findByFlightIdAndCabin(20L, Cabin.PREMIUM)).thenReturn(Optional.empty());
        when(seatInventoryMapper.toResponse(seatInventory1)).thenReturn(response1);
        when(seatInventoryMapper.toResponse(seatInventory2)).thenReturn(response2);

        // ACT
        var response = service.findByFlightId(20L);

        // ASSERT
        assertThat(response).hasSize(2);
        assertThat(response).extracting(SeatInventoryResponse::cabin)
                .containsExactlyInAnyOrder("ECONOMY", "BUSINESS");

        verify(flightRepo).existsById(20L);
        verify(seatInventoryMapper).toResponse(seatInventory1);
        verify(seatInventoryMapper).toResponse(seatInventory2);
    }

    @Test
    void shouldThrowExceptionWhenFlightNotFoundOnFindByFlightId() {
        // ARRANGE
        when(flightRepo.existsById(999L)).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.findByFlightId(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Flight 999 not found");

        verify(flightRepo).existsById(999L);
        verify(repo, never()).findByFlightIdAndCabin(any(), any());
    }

    @Test
    void shouldUpdateSeatInventory() {
        // ARRANGE
        var seatInventory = SeatInventory.builder()
                .id(40L)
                .cabin(Cabin.ECONOMY)
                .totalSeats(200)
                .availableSeats(150)
                .build();

        when(repo.findById(40L)).thenReturn(Optional.of(seatInventory));

        doAnswer(inv -> {
            SeatInventoryUpdateRequest req = inv.getArgument(0);
            SeatInventory entity = inv.getArgument(1);
            entity.setTotalSeats(entity.getTotalSeats() + req.totalSeats());
            entity.setAvailableSeats(entity.getAvailableSeats() + req.totalSeats());
            return null;
        }).when(seatInventoryMapper).patch(any(), any());

        when(repo.save(seatInventory)).thenReturn(seatInventory);

        var expectedResponse = new SeatInventoryResponse(40L, "ECONOMY", 250, 200,30l);
        when(seatInventoryMapper.toResponse(seatInventory)).thenReturn(expectedResponse);

        var updateReq = new SeatInventoryUpdateRequest("premium", 200, 10);

        // ACT
        var response = service.update(40L, updateReq);

        // ASSERT
        assertThat(response.totalSeats()).isEqualTo(250);
        assertThat(response.availableSeats()).isEqualTo(200);

        verify(repo).findById(40L);
        verify(seatInventoryMapper).patch(updateReq, seatInventory);
        verify(repo).save(seatInventory);
        verify(seatInventoryMapper).toResponse(seatInventory);
    }


    @Test
    void shouldDeleteSeatInventory() {
        // ARRANGE
        when(repo.existsById(70L)).thenReturn(true);

        // ACT
        service.deleteById(70L);

        // ASSERT
        verify(repo).existsById(70L);
        verify(repo).deleteById(70L);
    }
}