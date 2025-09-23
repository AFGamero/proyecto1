package com.unimag.repositories;

import com.unimag.entidades.Cabin;
import com.unimag.entidades.Flight;
import com.unimag.entidades.SeatInventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SeatInventoryRepositoryTest {

    @Autowired
    private SeatInventoryRepository seatInventoryRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Test
    @DisplayName("Debe encontrar SeatInventory por flightId y cabin")
    void testFindByFlightIdAndCabin() {
        // Crear vuelo
        Flight flight = new Flight();
        flight.setNumber("AV123");
        flight.setDepartureTime(OffsetDateTime.now().plusDays(1));
        flight.setArrivalTime(OffsetDateTime.now().plusDays(1).plusHours(2));
        flightRepository.save(flight);

        // Crear inventario
        SeatInventory seatInventory = new SeatInventory();
        seatInventory.setFlight(flight);
        seatInventory.setCabin(Cabin.ECONOMY); // asumiendo enum Cabin
        seatInventory.setTotalSeats(100);
        seatInventory.setAvailableSeats(80);
        seatInventoryRepository.save(seatInventory);

        // Buscar
        Optional<SeatInventory> encontrado =
                seatInventoryRepository.findByFlightIdAndCabin(flight.getId(), Cabin.ECONOMY);

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getAvailableSeats()).isEqualTo(80);
    }

    @Test
    @DisplayName("Debe verificar si hay asientos mínimos disponibles")
    void testHasMinimumSeatsAvailable() {
        // Crear vuelo
        Flight flight = new Flight();
        flight.setNumber("LA456");
        flight.setDepartureTime(OffsetDateTime.now().plusDays(2));
        flight.setArrivalTime(OffsetDateTime.now().plusDays(2).plusHours(3));
        flightRepository.save(flight);

        // Crear inventario
        SeatInventory seatInventory = new SeatInventory();
        seatInventory.setFlight(flight);
        seatInventory.setCabin(Cabin.BUSINESS);
        seatInventory.setTotalSeats(20);
        seatInventory.setAvailableSeats(5);
        seatInventoryRepository.save(seatInventory);

        // Comprobar disponibilidad
        boolean haySuficientes = seatInventoryRepository.hasMinimumSeatsAvailable(flight.getId(), Cabin.BUSINESS, 3);
        boolean insuficientes = seatInventoryRepository.hasMinimumSeatsAvailable(flight.getId(), Cabin.BUSINESS, 10);

        assertThat(haySuficientes).isTrue();
        assertThat(insuficientes).isFalse();
    }
}
