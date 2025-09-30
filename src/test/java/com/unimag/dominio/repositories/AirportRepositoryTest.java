package com.unimag.dominio.repositories;

import com.unimag.dominio.entidades.Airport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AirportRepositoryTest extends AbstractRepository {

    @Autowired
    private AirportRepository airportRepository;

    @Test
    @DisplayName("Debe encontrar un aeropuerto por su código IATA")
    void shouldFindByCode() {
        // given
        Airport madrid = Airport.builder()
                .code("MAD")
                .name("Madrid Barajas")
                .build();
        airportRepository.save(madrid);

        // when
        Optional<Airport> found = airportRepository.findByCode("MAD");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Madrid Barajas");
    }

    @Test
    @DisplayName("Debe devolver vacío si no existe el aeropuerto con ese código")
    void shouldReturnEmptyIfNotFound() {
        // when
        Optional<Airport> found = airportRepository.findByCode("XXX");

        // then
        assertThat(found).isEmpty();
    }
}
