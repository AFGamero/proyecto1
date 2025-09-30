package com.unimag.dominio.repositories;

import com.unimag.dominio.entidades.Passenger;
import com.unimag.dominio.repositories.PassengerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PassengerRepositoryTest {

    @Autowired
    private PassengerRepository passengerRepository;

    @Test
    @DisplayName("Debe guardar y encontrar pasajero por email ignorando mayúsculas")
    void testFindByEmailIgnoreCase() {
        Passenger passenger = new Passenger();
        passenger.setFullName("Juan Pérez");
        passenger.setEmail("TEST@correo.com");

        passengerRepository.save(passenger);

        Optional<Passenger> encontrado = passengerRepository.findByEmailIgnoreCase("test@CORREO.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getFullName()).isEqualTo("Juan Pérez");
    }

    @Test
    @DisplayName("Debe traer pasajero junto con su perfil (LEFT JOIN FETCH)")
    void testFindByEmailIgnoreCaseWithProfile() {
        Passenger passenger = new Passenger();
        passenger.setFullName("Ana López");
        passenger.setEmail("ana@correo.com");

        passengerRepository.save(passenger);

        Optional<Passenger> encontrado = passengerRepository.findByEmailIgnoreCaseWithProfile("ANA@CORREO.COM");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("ana@correo.com");

        // Aquí simplemente verificamos que no explote aunque el perfil sea null
    }
}
