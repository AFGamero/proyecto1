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
    void shouldThrowExceptionWhenFlightNotFoundOnCreate() {
        // ARRANGE
        when(flightRepo.findById(999L)).thenReturn(Optional.empty());

        var request = new SeatInventoryCreateRequest("ECONOMY", 100, 100);

        // ACT & ASSERT
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

        // ACT & ASSERT
        assertThatThrownBy(() -> service.create(5L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SeatInventory for flight 5 and cabin BUSINESS already exists");

        verify(flightRepo).findById(5L);
        verify(repo).findByFlightIdAndCabin(5L, Cabin.BUSINESS);
        verify(repo, never()).save(any());
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
    void shouldThrowExceptionWhenFlightNotFoundOnFindByFlightAndCabin() {
        // ARRANGE
        when(flightRepo.existsById(999L)).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.findByFlightAndCabin(999L, "ECONOMY"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Flight 999 not found");

        verify(flightRepo).existsById(999L);
        verify(repo, never()).findByFlightIdAndCabin(any(), any());
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
    void shouldReturnEmptyListWhenNoCabinsForFlight() {
        // ARRANGE
        when(flightRepo.existsById(30L)).thenReturn(true);
        when(repo.findByFlightIdAndCabin(30L, Cabin.ECONOMY)).thenReturn(Optional.empty());
        when(repo.findByFlightIdAndCabin(30L, Cabin.BUSINESS)).thenReturn(Optional.empty());
        when(repo.findByFlightIdAndCabin(30L, Cabin.PREMIUM)).thenReturn(Optional.empty());

        // ACT
        var response = service.findByFlightId(30L);

        // ASSERT
        assertThat(response).isEmpty();

        verify(flightRepo).existsById(30L);
        verifyNoInteractions(seatInventoryMapper);
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
    void shouldThrowExceptionWhenSeatInventoryNotFoundOnUpdate() {
        // ARRANGE
        when(repo.findById(999L)).thenReturn(Optional.empty());

        var updateReq = new SeatInventoryUpdateRequest("premium", 50, 10);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.update(999L, updateReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SeatInventory 999 not found");

        verify(repo).findById(999L);
        verify(repo, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAvailableSeatsExceedTotalSeats() {
        // ARRANGE
        var seatInventory = SeatInventory.builder()
                .id(60L)
                .totalSeats(100)
                .availableSeats(50)
                .build();

        // Asegúrate de stubear el mé
        when(repo.findById(60L)).thenReturn(Optional.of(seatInventory));

        // Este valor hace que exceda el total
        var updateReq = new SeatInventoryUpdateRequest("business", 80, 90);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.update(60L, updateReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("Available seats \\(\\d+\\) cannot exceed total seats \\(\\d+\\)");

        verify(repo).findById(60L);
    }



    @Test
    void shouldThrowExceptionWhenAvailableSeatsIsNegative() {
        // ARRANGE
        var seatInventory = SeatInventory.builder()
                .id(60L)
                .totalSeats(100)
                .availableSeats(50)
                .build();

        when(repo.findById(60L)).thenReturn(Optional.of(seatInventory));

        var updateReq = new SeatInventoryUpdateRequest("business", 100, -4);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.update(60L, updateReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Available seats cannot be negative");

        verify(repo).findById(60L);
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

    @Test
    void shouldThrowExceptionWhenSeatInventoryNotFoundOnDelete() {
        // ARRANGE
        when(repo.existsById(999L)).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.deleteById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SeatInventory 999 not found");

        verify(repo).existsById(999L);
        verify(repo, never()).deleteById(any());
    }

    @Test
    void shouldCheckAvailabilityAndReturnTrue() {
        // ARRANGE
        when(flightRepo.existsById(25L)).thenReturn(true);
        when(repo.hasMinimumSeatsAvailable(25L, Cabin.ECONOMY, 10)).thenReturn(true);

        // ACT
        var result = service.checkAvailability(25L, "ECONOMY", 10);

        // ASSERT
        assertThat(result).isTrue();

        verify(flightRepo).existsById(25L);
        verify(repo).hasMinimumSeatsAvailable(25L, Cabin.ECONOMY, 10);
    }

    @Test
    void shouldCheckAvailabilityAndReturnFalse() {
        // ARRANGE
        when(flightRepo.existsById(25L)).thenReturn(true);
        when(repo.hasMinimumSeatsAvailable(25L, Cabin.BUSINESS, 50)).thenReturn(false);

        // ACT
        var result = service.checkAvailability(25L, "BUSINESS", 50);

        // ASSERT
        assertThat(result).isFalse();

        verify(flightRepo).existsById(25L);
        verify(repo).hasMinimumSeatsAvailable(25L, Cabin.BUSINESS, 50);
    }

    @Test
    void shouldThrowExceptionWhenFlightNotFoundOnCheckAvailability() {
        // ARRANGE
        when(flightRepo.existsById(999L)).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.checkAvailability(999L, "ECONOMY", 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Flight 999 not found");

        verify(flightRepo).existsById(999L);
        verify(repo, never()).hasMinimumSeatsAvailable(any(), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenMinSeatsIsInvalid() {
        // ACT & ASSERT
        assertThatThrownBy(() -> service.checkAvailability(10L, "ECONOMY", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minSeats must be positive");

        assertThatThrownBy(() -> service.checkAvailability(10L, "ECONOMY", -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minSeats must be positive");
    }

    @Test
    void shouldReserveSeatsSuccessfully() {
        // ARRANGE
        var seatInventory = SeatInventory.builder()
                .id(80L)
                .cabin(Cabin.ECONOMY)
                .totalSeats(200)
                .availableSeats(150)
                .build();

        when(repo.findByFlightIdAndCabin(30L, Cabin.ECONOMY)).thenReturn(Optional.of(seatInventory));
        when(repo.save(seatInventory)).thenReturn(seatInventory);

        var expectedResponse = new SeatInventoryResponse(80L, "ECONOMY", 200, 130,20l);
        when(seatInventoryMapper.toResponse(seatInventory)).thenReturn(expectedResponse);

        // ACT
        var response = service.reserveSeats(30L, "ECONOMY", 20);
        repo.save(seatInventory);
        // ASSERT
        assertThat(response.availableSeats()).isEqualTo(130);
        assertThat(seatInventory.getAvailableSeats()).isEqualTo(130);

        verify(repo).findByFlightIdAndCabin(30L, Cabin.ECONOMY);
        verify(repo).save(seatInventory);
        verify(seatInventoryMapper).toResponse(seatInventory);
    }

    @Test
    void shouldThrowExceptionWhenSeatInventoryNotFoundOnReserve() {
        // ARRANGE
        when(repo.findByFlightIdAndCabin(30L, Cabin.ECONOMY)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.reserveSeats(30L, "ECONOMY", 20))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SeatInventory for flight 30 and cabin ECONOMY not found");

        verify(repo).findByFlightIdAndCabin(30L, Cabin.ECONOMY);
        verify(repo, never()).save(any());
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

        verify(repo).findByFlightIdAndCabin(35L, Cabin.BUSINESS);
        verify(repo, never()).save(any());
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
    @Test
    void shouldReleaseSeatsSuccessfully() {
        // ARRANGE
        // Simulamos un inventario inicial con 150 asientos disponibles de un total de 200
        var seatInventory = SeatInventory.builder()
                .id(100L)
                .cabin(Cabin.ECONOMY)
                .totalSeats(200)
                .availableSeats(150)
                .build();

        // Simulamos que el repositorio lo encuentra correctamente
        when(repo.findByFlightIdAndCabin(40L, Cabin.ECONOMY))
                .thenReturn(Optional.of(seatInventory));

        // Cuando se guarde, devolvemos el mismo objeto (mock de persistencia)
        when(repo.save(any(SeatInventory.class))).thenAnswer(inv -> inv.getArgument(0));

        // El mapper devuelve un DTO con los nuevos valores
        var expectedResponse = new SeatInventoryResponse(
                100L, "ECONOMY", 200, 160, 40L);
        when(seatInventoryMapper.toResponse(any(SeatInventory.class)))
                .thenReturn(expectedResponse);

        // ACT
        var response = service.releaseSeats(40L, "ECONOMY", 10);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.availableSeats()).isEqualTo(160);
        assertThat(seatInventory.getAvailableSeats()).isEqualTo(160);

        // Verificamos las interacciones
        verify(repo).findByFlightIdAndCabin(40L, Cabin.ECONOMY);
        verify(repo).save(seatInventory);
        verify(seatInventoryMapper).toResponse(seatInventory);
    }

    @Test
    void shouldThrowExceptionWhenSeatInventoryNotFoundOnRelease() {
        // ARRANGE
        when(repo.findByFlightIdAndCabin(40L, Cabin.ECONOMY)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.releaseSeats(40L, "ECONOMY", 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("SeatInventory for flight 40 and cabin ECONOMY not found");

        verify(repo).findByFlightIdAndCabin(40L, Cabin.ECONOMY);
        verify(repo, never()).save(any());
    }
    @Test
    void shouldThrowExceptionWhenReleasingWouldExceedTotalSeats() {
        // ARRANGE
        var seatInventory = SeatInventory.builder()
                .id(100L)
                .cabin(Cabin.ECONOMY)
                .totalSeats(90)
                .availableSeats(80)
                .build();

        when(repo.findByFlightIdAndCabin(40L, Cabin.ECONOMY))
                .thenReturn(Optional.of(seatInventory));

        // ACT & ASSERT
        assertThatThrownBy(() -> service.releaseSeats(40L, "ECONOMY", 20))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot release 20 seats. Would exceed total seats");
    }



    @Test
    void shouldThrowExceptionWhenReleasingInvalidNumberOfSeats() {
        // ACT & ASSERT
        assertThatThrownBy(() -> service.releaseSeats(10L, "ECONOMY", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seats must be positive");

        assertThatThrownBy(() -> service.releaseSeats(10L, "ECONOMY", -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seats must be positive");
    }
}