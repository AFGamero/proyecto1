package com.unimag.services;

import com.unimag.api.dto.SeatInventoryDtos.*;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.entidades.SeatInventory;
import com.unimag.dominio.repositories.FlightRepository;
import com.unimag.dominio.repositories.SeatInventoryRepository;
import com.unimag.exception.NotFoundException;
import com.unimag.services.implmnts.SeatInventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatInventoryServiceImplTest {

    @Mock
    SeatInventoryRepository repo;

    @Mock
    FlightRepository flightRepo;

    @InjectMocks
    SeatInventoryServiceImpl service;


    @Test
    void shouldCreateSeatInventoryAndMapToResponse() {
        var flight = Flight.builder()
                .id(10L)
                .number("AV123")
                .build();

        when(flightRepo.findById(10L)).thenReturn(Optional.of(flight));
        when(repo.findByFlightIdAndCabin(10L, Cabin.ECONOMY)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> {
            SeatInventory s = inv.getArgument(0);
            s.setId(100L);
            return s;
        });

        var request = new SeatInventoryCreateRequest("ECONOMY", 150, 150);

        var response = service.create(10L, request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.cabin()).isEqualTo("ECONOMY");
        assertThat(response.totalSeats()).isEqualTo(150);
        assertThat(response.availableSeats()).isEqualTo(150);

        verify(flightRepo).findById(10L);
        verify(repo).findByFlightIdAndCabin(10L, Cabin.ECONOMY);
        verify(repo).save(any(SeatInventory.class));
    }

    @Test
    void shouldThrowExceptionWhenFlightNotFoundOnCreate() {

        when(flightRepo.findById(999L)).thenReturn(Optional.empty());

        var request = new SeatInventoryCreateRequest("ECONOMY", 100, 100);

        assertThatThrownBy(() -> service.create(999L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Flight 999 not found");

        verify(flightRepo).findById(999L);
        verify(repo, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSeatInventoryAlreadyExists() {
        // ARRANGE
        var flight = Flight.builder().id(5L).build();
        var existing = SeatInventory.builder()
                .id(50L)
                .cabin(Cabin.BUSINESS)
                .build();

        when(flightRepo.findById(5L)).thenReturn(Optional.of(flight));
        when(repo.findByFlightIdAndCabin(5L, Cabin.BUSINESS)).thenReturn(Optional.of(existing));

        var request = new SeatInventoryCreateRequest("BUSINESS", 50, 50);

        assertThatThrownBy(() -> service.create(5L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SeatInventory for flight 5 and cabin BUSINESS already exists");

        verify(repo, never()).save(any());
    }

    @Test
    void shouldFindSeatInventoryById() {
        var seatInventory = SeatInventory.builder()
                .id(20L)
                .cabin(Cabin.ECONOMY)
                .totalSeats(200)
                .availableSeats(150)
                .build();

        when(repo.findById(20L)).thenReturn(Optional.of(seatInventory));

        var response = service.findById(20L);

        assertThat(response.id()).isEqualTo(20L);
        assertThat(response.cabin()).isEqualTo("ECONOMY");
        assertThat(response.totalSeats()).isEqualTo(200);
        assertThat(response.availableSeats()).isEqualTo(150);

        verify(repo).findById(20L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSeatInventoryNotFound() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SeatInventory 999 not found");

        verify(repo).findById(999L);
    }

    @Test
    void shouldFindByFlightAndCabin() {

        var seatInventory = SeatInventory.builder()
                .id(30L)
                .cabin(Cabin.BUSINESS)
                .totalSeats(50)
                .availableSeats(40)
                .build();

        when(flightRepo.existsById(15L)).thenReturn(true);
        when(repo.findByFlightIdAndCabin(15L, Cabin.BUSINESS)).thenReturn(Optional.of(seatInventory));

        var response = service.findByFlightAndCabin(15L, "BUSINESS");

        assertThat(response.id()).isEqualTo(30L);
        assertThat(response.cabin()).isEqualTo("BUSINESS");
        assertThat(response.totalSeats()).isEqualTo(50);
        assertThat(response.availableSeats()).isEqualTo(40);

        verify(flightRepo).existsById(15L);
        verify(repo).findByFlightIdAndCabin(15L, Cabin.BUSINESS);
    }

    @Test
    void shouldThrowExceptionWhenFlightNotFoundOnFindByFlightAndCabin() {
        when(flightRepo.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> service.findByFlightAndCabin(999L, "ECONOMY"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Flight 999 not found");

        verify(flightRepo).existsById(999L);
        verify(repo, never()).findByFlightIdAndCabin(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenSeatInventoryNotFoundForFlightAndCabin() {
        when(flightRepo.existsById(10L)).thenReturn(true);
        when(repo.findByFlightIdAndCabin(10L, Cabin.BUSINESS)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByFlightAndCabin(10L, "FIRST_CLASS"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SeatInventory for flight 10 and cabin FIRST_CLASS not found");

        verify(repo).findByFlightIdAndCabin(10L, Cabin.BUSINESS);
    }

    @Test
    void shouldFindAllCabinsByFlightId() {
        when(flightRepo.existsById(20L)).thenReturn(true);
        when(repo.findByFlightIdAndCabin(20L, Cabin.ECONOMY))
                .thenReturn(Optional.of(SeatInventory.builder()
                        .id(1L).cabin(Cabin.ECONOMY).totalSeats(200).availableSeats(150).build()));
        when(repo.findByFlightIdAndCabin(20L, Cabin.BUSINESS))
                .thenReturn(Optional.of(SeatInventory.builder()
                        .id(2L).cabin(Cabin.BUSINESS).totalSeats(50).availableSeats(30).build()));
        when(repo.findByFlightIdAndCabin(20L, Cabin.PREMIUM)).thenReturn(Optional.empty());

        var response = service.findByFlightId(20L);

        assertThat(response).hasSize(2);
        assertThat(response).extracting(SeatInventoryResponse::cabin)
                .containsExactlyInAnyOrder("ECONOMY", "BUSINESS");

        verify(flightRepo).existsById(20L);
    }

    @Test
    void shouldThrowExceptionWhenFlightNotFoundOnFindByFlightId() {
        when(flightRepo.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.findByFlightId(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Flight 999 not found");

        verify(flightRepo).existsById(999L);
    }

    @Test
    void shouldUpdateSeatInventory() {

        var seatInventory = SeatInventory.builder()
                .id(40L)
                .cabin(Cabin.ECONOMY)
                .totalSeats(200)
                .availableSeats(150)
                .build();

        when(repo.findById(40L)).thenReturn(Optional.of(seatInventory));

        var updateReq = new SeatInventoryUpdateRequest("premiun", 200,10);

        var response = service.update(40L, updateReq);

        assertThat(response.totalSeats()).isEqualTo(250);
        assertThat(response.availableSeats()).isEqualTo(200);

        verify(repo).findById(40L);
    }

    @Test
    void shouldThrowExceptionWhenAvailableSeatsExceedTotalSeats() {

        var seatInventory = SeatInventory.builder()
                .id(50L)
                .totalSeats(100)
                .availableSeats(50)
                .build();

        when(repo.findById(50L)).thenReturn(Optional.of(seatInventory));

        var updateReq = new SeatInventoryUpdateRequest( "premiun" , 150,20);

        assertThatThrownBy(() -> service.update(50L, updateReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Available seats (150) cannot exceed total seats (100)");
    }

    @Test
    void shouldThrowExceptionWhenAvailableSeatsIsNegative() {
        var seatInventory = SeatInventory.builder()
                .id(60L)
                .totalSeats(100)
                .availableSeats(50)
                .build();

        when(repo.findById(60L)).thenReturn(Optional.of(seatInventory));

        var updateReq = new SeatInventoryUpdateRequest("bussines", 10,4);

        assertThatThrownBy(() -> service.update(60L, updateReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Available seats cannot be negative");
    }

    @Test
    void shouldDeleteSeatInventory() {

        when(repo.existsById(70L)).thenReturn(true);

        service.deleteById(70L);

        verify(repo).existsById(70L);
        verify(repo).deleteById(70L);
    }

    @Test
    void shouldThrowExceptionWhenSeatInventoryNotFoundOnDelete() {
        when(repo.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SeatInventory 999 not found");

        verify(repo).existsById(999L);
        verify(repo, never()).deleteById(any());
    }

    @Test
    void shouldCheckAvailabilityAndReturnTrue() {
        when(flightRepo.existsById(25L)).thenReturn(true);
        when(repo.hasMinimumSeatsAvailable(25L, Cabin.ECONOMY, 10)).thenReturn(true);

        var result = service.checkAvailability(25L, "ECONOMY", 10);

        assertThat(result).isTrue();

        verify(flightRepo).existsById(25L);
        verify(repo).hasMinimumSeatsAvailable(25L, Cabin.ECONOMY, 10);
    }

    @Test
    void shouldCheckAvailabilityAndReturnFalse() {

        when(flightRepo.existsById(25L)).thenReturn(true);
        when(repo.hasMinimumSeatsAvailable(25L, Cabin.BUSINESS, 50)).thenReturn(false);

        var result = service.checkAvailability(25L, "BUSINESS", 50);

        assertThat(result).isFalse();

        verify(repo).hasMinimumSeatsAvailable(25L, Cabin.BUSINESS, 50);
    }

    @Test
    void shouldThrowExceptionWhenMinSeatsIsInvalid() {
        assertThatThrownBy(() -> service.checkAvailability(10L, "ECONOMY", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minSeats must be positive");

        assertThatThrownBy(() -> service.checkAvailability(10L, "ECONOMY", -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minSeats must be positive");
    }

    @Test
    void shouldReserveSeatsSuccessfully() {
        var seatInventory = SeatInventory.builder()
                .id(80L)
                .cabin(Cabin.ECONOMY)
                .totalSeats(200)
                .availableSeats(150)
                .build();

        when(repo.findByFlightIdAndCabin(30L, Cabin.ECONOMY)).thenReturn(Optional.of(seatInventory));

        var response = service.reserveSeats(30L, "ECONOMY", 20);

        assertThat(response.availableSeats()).isEqualTo(130);
        assertThat(seatInventory.getAvailableSeats()).isEqualTo(130);

        verify(repo).findByFlightIdAndCabin(30L, Cabin.ECONOMY);
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughSeatsAvailable() {
        // ARRANGE
        var seatInventory = SeatInventory.builder()
                .id(90L)
                .availableSeats(10)
                .build();

        when(repo.findByFlightIdAndCabin(35L, Cabin.BUSINESS)).thenReturn(Optional.of(seatInventory));

        // ACT & ASSERT
        assertThatThrownBy(() -> service.reserveSeats(35L, "BUSINESS", 20))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not enough seats available. Requested: 20, Available: 10");
    }

    @Test
    void shouldThrowExceptionWhenReservingInvalidNumberOfSeats() {
        // ACT & ASSERT
        assertThatThrownBy(() -> service.reserveSeats(10L, "ECONOMY", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seats must be positive");

        assertThatThrownBy(() -> service.reserveSeats(10L, "ECONOMY", -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seats must be positive");
    }

    // ═══════════════════════════════════════════════════════════
    // RELEASE SEATS
    // ═══════════════════════════════════════════════════════════

    @Test
    void shouldReleaseSeatsSuccessfully() {
        // ARRANGE
        var seatInventory = SeatInventory.builder()
                .id(100L)
                .cabin(Cabin.ECONOMY)
                .totalSeats(200)
                .availableSeats(150)
                .build();

        when(repo.findByFlightIdAndCabin(40L, Cabin.ECONOMY)).thenReturn(Optional.of(seatInventory));

        // ACT
        var response = service.releaseSeats(40L, "ECONOMY", 10);

        // ASSERT
        assertThat(response.availableSeats()).isEqualTo(160);
        assertThat(seatInventory.getAvailableSeats()).isEqualTo(160);

        verify(repo).findByFlightIdAndCabin(40L, Cabin.ECONOMY);
    }

    @Test
    void shouldThrowExceptionWhenReleasingWouldExceedTotalSeats() {
        // ARRANGE
        var seatInventory = SeatInventory.builder()
                .id(110L)
                .totalSeats(100)
                .availableSeats(90)
                .build();

        when(repo.findByFlightIdAndCabin(45L, Cabin.BUSINESS)).thenReturn(Optional.of(seatInventory));

        // ACT & ASSERT
        assertThatThrownBy(() -> service.releaseSeats(45L, "BUSINESS", 20))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot release 20 seats. Would exceed total seats");
    }

    @Test
    void shouldThrowExceptionWhenReleasingInvalidNumberOfSeats() {

        assertThatThrownBy(() -> service.releaseSeats(10L, "ECONOMY", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seats must be positive");

        assertThatThrownBy(() -> service.releaseSeats(10L, "ECONOMY", -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seats must be positive");
    }
}