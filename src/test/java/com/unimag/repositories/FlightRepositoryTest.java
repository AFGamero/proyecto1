package com.unimag.repositories;

import com.unimag.dominio.entidades.Airline;
import com.unimag.dominio.entidades.Airport;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.repositories.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FlightRepositoryTest {

    @Autowired
    private FlightRepository flightRepository;

    private Airline airline;
    private Airport origin;
    private Airport destination;

    @BeforeEach
    void setUp() {
        // Entidades auxiliares
        airline = Airline.builder()
                .name("Avianca")
                .build();

        origin = Airport.builder()
                .name("El Dorado")
                .build();

        destination = Airport.builder()
                .name("José María Córdova")
                .build();
    }

    @Test
    void testFindByAirlineName() {
        // Crear vuelo con Builder
        Flight flight = Flight.builder()
                .number("AV123")
                .departureTime(OffsetDateTime.now().plusDays(1))
                .arrivalTime(OffsetDateTime.now().plusDays(1).plusHours(1))
                .airline(airline)
                .origin(origin)
                .destination(destination)
                .build();

        // Guardar vuelo
        flightRepository.save(flight);

        // Ejecutar query
        List<Flight> result = flightRepository.findByAirlineName("Avianca");

        // Verificar resultados
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getNumber()).isEqualTo("AV123");
    }
}
