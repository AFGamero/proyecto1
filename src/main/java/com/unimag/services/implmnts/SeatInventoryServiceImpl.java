package com.unimag.services.implmnts;

import com.unimag.api.dto.SeatInventoryDtos.SeatInventoryCreateRequest;
import com.unimag.api.dto.SeatInventoryDtos.SeatInventoryResponse;
import com.unimag.api.dto.SeatInventoryDtos.SeatInventoryUpdateRequest;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.entidades.SeatInventory;
import com.unimag.dominio.repositories.FlightRepository;
import com.unimag.dominio.repositories.SeatInventoryRepository;
import com.unimag.exception.NotFoundException;
import com.unimag.services.SeatInventoryService;
import com.unimag.services.mappers.SeatInventoryMapper;
import lombok.NonNull;
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

    private final SeatInventoryRepository repo;
    private final FlightRepository flightRepo;
    private final SeatInventoryMapper seatInventoryMapper;

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


        SeatInventory seatInventory = seatInventoryMapper.toEntity(request);
        seatInventory.setFlight(flight);

        return seatInventoryMapper.toResponse(repo.save(seatInventory));
    }

    @Override
    @Transactional(readOnly = true)
    public SeatInventoryResponse findById(Long id) {
        return repo.findById(id)
                .map(seatInventoryMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(
                        "SeatInventory %d not found".formatted(id)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public SeatInventoryResponse findByFlightAndCabin(Long flightId, String cabin) {
        // Validar que el vuelo existe
        if (!flightRepo.existsById(flightId)) {
            throw new NotFoundException("Flight %d not found".formatted(flightId));
        }

        Cabin cabinEnum = Cabin.valueOf(cabin.toUpperCase());

        return repo.findByFlightIdAndCabin(flightId, cabinEnum)
                .map(seatInventoryMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(
                        "SeatInventory for flight %d and cabin %s not found"
                                .formatted(flightId, cabin)
                ));
    }

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
                .map(seatInventoryMapper::toResponse)
                .toList();
    }
    @Override
    public SeatInventoryResponse update(Long id, SeatInventoryUpdateRequest request) {
        SeatInventory seatInventory = repo.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "SeatInventory %d not found".formatted(id)
                ));

        Integer newAvailable = getInteger(request, seatInventory);

        if (newAvailable < 0) {
            throw new IllegalArgumentException(
                    "Available seats cannot be negative"
            );
        }

        seatInventoryMapper.patch(request, seatInventory);

        // 💾 Guardar los cambios en la BD
        SeatInventory updated = repo.save(seatInventory);

        return seatInventoryMapper.toResponse(updated);
    }


    private static Integer getInteger(SeatInventoryUpdateRequest request, SeatInventory seatInventory) {
        Integer newTotal = request.totalSeats() != null
                ? request.totalSeats()
                : seatInventory.getTotalSeats();

        Integer newAvailable = request.availableSeats() != null
                ? request.availableSeats()
                : seatInventory.getAvailableSeats();

        // 🧩 Validación 1: total seats no puede ser negativo
        if (newTotal < 0) {
            throw new IllegalArgumentException("Total seats cannot be negative");
        }

        // 🧩 Validación 2: available seats no puede ser negativo
        if (newAvailable < 0) {
            throw new IllegalArgumentException("Available seats cannot be negative");
        }

        // 🧩 Validación 3: available no puede superar total
        if (newAvailable > newTotal) {
            throw new IllegalArgumentException(
                    String.format(
                            "Available seats (%d) cannot exceed total seats (%d)",
                            newAvailable, newTotal
                    )
            );
        }

        return newAvailable;
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

        inventory.setAvailableSeats(inventory.getAvailableSeats() - seats);

        return seatInventoryMapper.toResponse(inventory);
    }

    @Override
    public SeatInventoryResponse releaseSeats(@NonNull Long flightId, @NonNull String cabin, @NonNull Integer seats) {
        if (seats <= 0) {
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
                    String.format(
                            "Cannot release %d seats. Would exceed total seats. Current available: %d, Total: %d",
                            seats, inventory.getAvailableSeats(), inventory.getTotalSeats()
                    )
            );
        }

        inventory.setAvailableSeats(newAvailable);
        repo.save(inventory);

        return seatInventoryMapper.toResponse(inventory);
    }
}