package com.unimag.repositories;

import com.unimag.entidades.SeatInventory;
import com.unimag.entidades.Cabin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {

    /**
     * Obtiene el registro de inventario de asientos para una cabina específica de un vuelo.
     * Hay UK(flight_id, cabin), por lo que la cardinalidad es 0..1.
     */
    Optional<SeatInventory> findByFlightIdAndCabin(Long flightId, Cabin cabin);

    /**
     * Verifica si availableSeats >= min para ese vuelo y cabina.
     * Útil para filtros rápidos antes de crear una reserva.
     */
    @Query("SELECT CASE WHEN si.availableSeats >= :min THEN true ELSE false END " +
            "FROM SeatInventory si WHERE si.flight.id = :flightId AND si.cabin = :cabin")
    boolean hasMinimumSeatsAvailable(@Param("flightId") Long flightId,
                                     @Param("cabin") Cabin cabin,
                                     @Param("min") Integer min);
}