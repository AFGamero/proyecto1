package com.unimag.dominio.repositories;

import com.unimag.dominio.entidades.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByCode(String code); //IATA code (ej. "MAD")
}
