package com.unimag.repositories;

import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.entidades.Passenger;
import com.unimag.dominio.repositories.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BookingRepositoryTest extends AbstractRepository {

    @Autowired
    BookingRepository bookingRepo;

    @Autowired
    TestEntityManager entityManager;

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passenger = Passenger.builder()
                .fullName("Elma Estro")
                .email("TEST@EMAIL.COM")
                .build();
        entityManager.persistAndFlush(passenger);

        Booking booking1 = Booking.builder()
                .createdAt(OffsetDateTime.now().minusDays(1))
                .passenger(passenger)
                .build();
        entityManager.persistAndFlush(booking1);

        Booking booking2 = Booking.builder()
                .createdAt(OffsetDateTime.now())
                .passenger(passenger)
                .build();
        entityManager.persistAndFlush(booking2);

        entityManager.clear();
    }

    @Test
    @DisplayName("Booking: find by passenger email ignoring case")
    void shouldFindByPassengerEmailIgnoreCaseOrderByCreatedAtDesc() {
        // When
        Page<Booking> result = bookingRepo.findByPassengerEmailIgnoreCaseOrderByCreatedAtDesc(
                "test@email.com", PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Booking::getCreatedAt)
                .isSortedAccordingTo((a, b) -> b.compareTo(a)); // Descendente
    }

    @Test
    @DisplayName("Booking: find by ID with details (passenger + bookingItems + flights)")
    void shouldFindByIdWithDetails() {
        // Given
        Booking booking = bookingRepo.findAll().get(0);

        // When
        Optional<Booking> loaded = bookingRepo.findByIdWithDetails(booking.getId());

        // Then
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getPassenger()).isNotNull();
        assertThat(loaded.get().getBookingsItems()).isNotNull();
    }
}
