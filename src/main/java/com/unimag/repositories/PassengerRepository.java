package com.unimag.repositories;

import com.unimag.entidades.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    /**
     * Busca un pasajero por email, ignorando mayúsculas/minúsculas.
     */
    Optional<Passenger> findByEmailIgnoreCase(String email);

    /**
     * Busca un pasajero por email, precargando el PassengerProfile
     */
    @Query("SELECT p FROM Passenger p LEFT JOIN FETCH p.passengerProfile WHERE LOWER(p.email) = LOWER(:email)")
    Optional<Passenger> findByEmailIgnoreCaseWithProfile(@Param("email") String email);

}
