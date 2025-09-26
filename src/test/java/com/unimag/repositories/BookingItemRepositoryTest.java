package com.unimag.repositories;

import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.entidades.BookingItem;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.repositories.BookingItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingItemRepositoryTest extends AbstractRepository {

    @Autowired
    BookingItemRepository bookingItemRepo;

    @Autowired
    TestEntityManager entityManager;

    private Booking booking1;
    private Flight flight1;
    private Flight flight2;

    @BeforeEach
    void setUp() {
        // Aquí puedes inicializar datos comunes si quieres
        // o dejarlo vacío como lo tienes en tu ejemplo
    }

    @Test
    @DisplayName("BookingItem: find by booking ID ordered by segment order")
    void findByBookingIdOrderBySegmentOrder() {
        // Given
        booking1 = Booking.builder().build();
        entityManager.persistAndFlush(booking1);

        flight1 = Flight.builder().build();
        flight2 = Flight.builder().build();
        entityManager.persistAndFlush(flight1);
        entityManager.persistAndFlush(flight2);

        bookingItemRepo.save(BookingItem.builder()
                .booking(booking1)
                .flight(flight2)
                .segmentOrder(2)
                .price(new BigDecimal("300.00"))
                .cabin(Cabin.BUSINESS)
                .build());

        bookingItemRepo.save(BookingItem.builder()
                .booking(booking1)
                .flight(flight1)
                .segmentOrder(1)
                .price(new BigDecimal("150.00"))
                .cabin(Cabin.ECONOMY)
                .build());

        // When
        List<BookingItem> items = bookingItemRepo.findByBookingIdOrderBySegmentOrder(booking1.getId());

        // Then
        assertThat(items).hasSize(2)
                .extracting(BookingItem::getSegmentOrder)
                .containsExactly(1, 2);
    }

    @Test
    @DisplayName("BookingItem: calculates total by booking ID")
    void calculateTotalByBookingId() {
        // Given
        booking1 = Booking.builder().build();
        entityManager.persistAndFlush(booking1);

        flight1 = Flight.builder().build();
        flight2 = Flight.builder().build();
        entityManager.persistAndFlush(flight1);
        entityManager.persistAndFlush(flight2);

        bookingItemRepo.save(BookingItem.builder()
                .booking(booking1)
                .flight(flight1)
                .segmentOrder(1)
                .price(new BigDecimal("150.00"))
                .cabin(Cabin.ECONOMY)
                .build());

        bookingItemRepo.save(BookingItem.builder()
                .booking(booking1)
                .flight(flight2)
                .segmentOrder(2)
                .price(new BigDecimal("300.00"))
                .cabin(Cabin.BUSINESS)
                .build());

        // When
        BigDecimal total = bookingItemRepo.calculateTotalByBookingId(booking1.getId());

        // Then
        assertThat(total).isEqualByComparingTo("450.00");
    }

    @Test
    @DisplayName("BookingItem: count seats sold by flight and cabin")
    void countSeatsSoldByFlightAndCabin() {
        // Given
        booking1 = Booking.builder().build();
        entityManager.persistAndFlush(booking1);

        flight1 = Flight.builder().build();
        entityManager.persistAndFlush(flight1);

        bookingItemRepo.save(BookingItem.builder()
                .booking(booking1)
                .flight(flight1)
                .segmentOrder(1)
                .price(new BigDecimal("150.00"))
                .cabin(Cabin.ECONOMY)
                .build());

        bookingItemRepo.save(BookingItem.builder()
                .booking(booking1)
                .flight(flight1)
                .segmentOrder(2)
                .price(new BigDecimal("175.00"))
                .cabin(Cabin.ECONOMY)
                .build());

        bookingItemRepo.save(BookingItem.builder()
                .booking(booking1)
                .flight(flight1)
                .segmentOrder(3)
                .price(new BigDecimal("400.00"))
                .cabin(Cabin.BUSINESS)
                .build());

        // When
        long economyCount = bookingItemRepo.countSeatsSoldByFlightAndCabin(flight1.getId(), Cabin.ECONOMY);
        long businessCount = bookingItemRepo.countSeatsSoldByFlightAndCabin(flight1.getId(), Cabin.BUSINESS);
        long premiumCount = bookingItemRepo.countSeatsSoldByFlightAndCabin(flight1.getId(), Cabin.PREMIUM);

        // Then
        assertThat(economyCount).isEqualTo(2);
        assertThat(businessCount).isEqualTo(1);
        assertThat(premiumCount).isEqualTo(0);
    }
}
