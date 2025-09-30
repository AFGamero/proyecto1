package com.unimag.services.implmnts;

import com.unimag.api.dto.SeatInventoryDtos;
import com.unimag.api.dto.SeatInventoryDtos.*;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.entidades.SeatInventory;
import com.unimag.dominio.repositories.FlightRepository;
import com.unimag.dominio.repositories.SeatInventoryRepository;
import com.unimag.exception.NotFoundException;
import com.unimag.services.SeatInventoryService;
import com.unimag.services.mappers.SeatInventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatInventoryServiceImpl implements SeatInventoryService {

    // ═══════════════════════════════════════════════════════════
    // INYECCIÓN DE DEPENDENCIAS
    // ═══════════════════════════════════════════════════════════
    private final SeatInventoryRepository repo;
    private final FlightRepository flightRepo;

    // ═══════════════════════════════════════════════════════════
    // CREAR SEAT INVENTORY
    // ═══════════════════════════════════════════════════════════
    @Override
    public SeatInventoryResponse create(Long flightId, SeatInventoryCreateRequest request) {
        // 1. Validar que el vuelo existe
        Flight flight = flightRepo.findById(flightId)
                .orElseThrow(() -> new NotFoundException(
                        "Flight %d not found".formatted(flightId)
                ));


        Cabin cabin = Cabin.valueOf(request.cabin().toUpperCase());
        if (repo.findByFlightIdAndCabin(flightId, cabin).isPresent()) {
            throw new IllegalStateException(
                    "SeatInventory for flight %d and cabin %s already exists"
                            .formatted(flightId, cabin.name())
            );
        }


        SeatInventory seatInventory = SeatInventoryMapper.toEntity(request);
        seatInventory.setFlight(flight);

        return SeatInventoryMapper.toResponse(repo.save(seatInventory));
    }

    // ═══════════════════════════════════════════════════════════
    // BUSCAR POR ID
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional(readOnly = true)
    public SeatInventoryResponse findById(Long id) {
        return repo.findById(id)
                .map(SeatInventoryMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(
                        "SeatInventory %d not found".formatted(id)
                ));
    }

    // ═══════════════════════════════════════════════════════════
    // BUSCAR POR VUELO Y CABINA (ÚNICO - gracias a UK)
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional(readOnly = true)
    public SeatInventoryResponse findByFlightAndCabin(Long flightId, String cabin) {
        // Validar que el vuelo existe
        if (!flightRepo.existsById(flightId)) {
            throw new NotFoundException("Flight %d not found".formatted(flightId));
        }

        Cabin cabinEnum = Cabin.valueOf(cabin.toUpperCase());

        return repo.findByFlightIdAndCabin(flightId, cabinEnum)
                .map(SeatInventoryMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(
                        "SeatInventory for flight %d and cabin %s not found"
                                .formatted(flightId, cabin)
                ));
    }

    // ═══════════════════════════════════════════════════════════
    // LISTAR POR VUELO (TODAS LAS CABINAS)
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional(readOnly = true)
    public List<SeatInventoryResponse> findByFlightId(Long flightId) {

        if (!flightRepo.existsById(flightId)) {
            throw new NotFoundException("Flight %d not found".formatted(flightId));
        }

        return Arrays.stream(Cabin.values())
                .map(cabin -> repo.findByFlightIdAndCabin(flightId, cabin))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(SeatInventoryMapper::toResponse)
                .toList();
    }

    // ═══════════════════════════════════════════════════════════
    // ACTUALIZAR SEAT INVENTORY
    // ═══════════════════════════════════════════════════════════
    @Override
    public SeatInventoryResponse update(Long id, SeatInventoryUpdateRequest request) {

        SeatInventory seatInventory = repo.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "SeatInventory %d not found".formatted(id)
                ));

        Integer newTotal = request.totalSeats() != null
                ? request.totalSeats()
                : seatInventory.getTotalSeats();

        Integer newAvailable = request.availableSeats() != null
                ? request.availableSeats()
                : seatInventory.getAvailableSeats();

        if (newAvailable > newTotal) {
            throw new IllegalArgumentException(
                    "Available seats (%d) cannot exceed total seats (%d)"
                            .formatted(newAvailable, newTotal)
            );
        }

        if (newAvailable < 0) {
            throw new IllegalArgumentException(
                    "Available seats cannot be negative"
            );
        }

        SeatInventoryMapper.patch(seatInventory, request);

        return SeatInventoryMapper.toResponse(seatInventory);
    }

    @Override
    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("SeatInventory %d not found".formatted(id));
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(Long flightId, String cabin, Integer minSeats) {
        if (minSeats == null || minSeats <= 0) {
            throw new IllegalArgumentException("minSeats must be positive");
        }

        if (!flightRepo.existsById(flightId)) {
            throw new NotFoundException("Flight %d not found".formatted(flightId));
        }

        Cabin cabinEnum = Cabin.valueOf(cabin.toUpperCase());

        return repo.hasMinimumSeatsAvailable(flightId, cabinEnum, minSeats);
    }

    @Override
    public SeatInventoryResponse reserveSeats(Long flightId, String cabin, Integer seats) {
        // 1. Validar parámetros
        if (seats == null || seats <= 0) {
            throw new IllegalArgumentException("Seats must be positive");
        }
        Cabin cabinEnum = Cabin.valueOf(cabin.toUpperCase());
        SeatInventory inventory = repo.findByFlightIdAndCabin(flightId, cabinEnum)
                .orElseThrow(() -> new NotFoundException(
                        "SeatInventory for flight %d and cabin %s not found"
                                .formatted(flightId, cabin)
                ));

        if (inventory.getAvailableSeats() < seats) {
            throw new IllegalStateException(
                    "Not enough seats available. Requested: %d, Available: %d"
                            .formatted(seats, inventory.getAvailableSeats())
            );
        }

        // 4. Reducir asientos disponibles
        inventory.setAvailableSeats(inventory.getAvailableSeats() - seats);

        // 5. Retornar (dirty checking persiste el cambio)
        return SeatInventoryMapper.toResponse(inventory);
    }

    @Override
    public SeatInventoryResponse releaseSeats(Long flightId, String cabin, Integer seats) {
        if (seats == null || seats <= 0) {
            throw new IllegalArgumentException("Seats must be positive");
        }

        Cabin cabinEnum = Cabin.valueOf(cabin.toUpperCase());
        SeatInventory inventory = repo.findByFlightIdAndCabin(flightId, cabinEnum)
                .orElseThrow(() -> new NotFoundException(
                        "SeatInventory for flight %d and cabin %s not found"
                                .formatted(flightId, cabin)
                ));

        int newAvailable = inventory.getAvailableSeats() + seats;

        if (newAvailable > inventory.getTotalSeats()) {
            throw new IllegalStateException(
                    "Cannot release %d seats. Would exceed total seats. " +
                            "Current available: %d, Total: %d"
                                    .formatted(seats, inventory.getAvailableSeats(),
                                            inventory.getTotalSeats())
            );
        }

        inventory.setAvailableSeats(newAvailable);

        return SeatInventoryMapper.toResponse(inventory);
    }
}