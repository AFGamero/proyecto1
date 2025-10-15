package com.unimag.services;

import com.unimag.api.dto.BookingDtos;
import com.unimag.api.dto.BookingDtos.*;
import com.unimag.dominio.entidades.*;
import com.unimag.dominio.repositories.BookingItemRepository;
import com.unimag.dominio.repositories.BookingRepository;
import com.unimag.dominio.repositories.FlightRepository;
import com.unimag.exception.NotFoundException;
import com.unimag.services.implmnts.BookingItemServiceImpl;
import com.unimag.services.mappers.BookingMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingItemServiceImplTest {

    @Mock
    BookingItemRepository bookingItemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    FlightRepository flightRepository;

    @Mock
    BookingMapper bookingMapper;

    @InjectMocks
    BookingItemServiceImpl service;
    @Test
    void shouldAddItemToBooking() {
        // Arrange
        Long bookingId = 1L;
        Long flightId = 50L;

        var booking = Booking.builder().id(bookingId).build();
        var flight = Flight.builder().id(flightId).number("AV123").build();

        var request = new BookingDtos.BookingItemCreateRequest(
                "ECONOMY",
                new BigDecimal("150.00"),
                10
        );

        var item = BookingItem.builder()
                .cabin(Cabin.ECONOMY)
                .price(new BigDecimal("150.00"))
                .segmentOrder(10)
                .flight(flight)
                .build();

        var expectedResponse = new BookingDtos.BookingItemResponse(
                1L,
                "ECONOMY",
                new BigDecimal("150.00"),
                10,
                20L,
                50L,
                "AV123"
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flight));
        when(bookingMapper.toItemEntity(request)).thenReturn(item);
        when(bookingMapper.toItemResponse(any(BookingItem.class))).thenReturn(expectedResponse);

        // Act
        var result = service.addItem(bookingId, flightId, request);

        // Assert
        assertEquals(expectedResponse, result);
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFoundOnAddItem() {
        // ARRANGE
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        var request = new BookingItemCreateRequest("ECONOMY", new BigDecimal("150.00"), 10);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.addItem(999L, 50L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Booking with id 999 not found");

        verify(bookingRepository).findById(999L);
        verify(flightRepository, never()).findById(any());
        verify(bookingItemRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenFlightNotFoundOnAddItem() {
        // ARRANGE
        var booking = Booking.builder().id(10L).build();

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        var request = new BookingItemCreateRequest("ECONOMY", new BigDecimal("150.00"), 10);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.addItem(10L, 999L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Flight with id 999 not found");

        verify(bookingRepository).findById(10L);
        verify(flightRepository).findById(999L);
        verify(bookingItemRepository, never()).save(any());
    }

    @Test
    void shouldGetBookingItemById() {
        // ARRANGE
        var flight = Flight.builder()
                .id(30L)
                .number("LA456")
                .build();

        var bookingItem = BookingItem.builder()
                .id(100L)
                .cabin(Cabin.BUSINESS)
                .price(new BigDecimal("500.00"))
                .flight(flight)
                .segmentOrder(1)
                .build();

        var expectedResponse = new BookingItemResponse(
                100L,
                "BUSINESS",
                new BigDecimal("500.00"),
                1232,
                bookingItem.getId(),
                flight.getId(),
                flight.getNumber()

        );

        when(bookingItemRepository.findById(100L)).thenReturn(Optional.of(bookingItem));
        when(bookingMapper.toItemResponse(bookingItem)).thenReturn(expectedResponse);

        // ACT
        var response = service.getBookingItem(100L);

        // ASSERT
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.cabin()).isEqualTo("BUSINESS");
        assertThat(response.price()).isEqualTo(new BigDecimal("500.00"));
        assertThat(response.flight_number()).isEqualTo("LA456");
        assertThat(response.segmentOrder()).isEqualTo(1232);

        verify(bookingItemRepository).findById(100L);
        verify(bookingMapper).toItemResponse(bookingItem);
    }

    @Test
    void shouldThrowExceptionWhenBookingItemNotFound() {
        // ARRANGE
        when(bookingItemRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.getBookingItem(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("BookingItem with id 999 not found");

        verify(bookingItemRepository).findById(999L);
        verifyNoInteractions(bookingMapper);
    }

    @Test
    void shouldListItemsByBookingId() {
        // ARRANGE
        var booking = Booking.builder().id(20L).build();

        var flight1 = Flight.builder().id(1L).number("FL001").build();
        var flight2 = Flight.builder().id(2L).number("FL002").build();

        var item1 = BookingItem.builder()
                .id(201L)
                .cabin(Cabin.ECONOMY)
                .price(new BigDecimal("100.00"))
                .flight(flight1)
                .segmentOrder(1)
                .build();

        var item2 = BookingItem.builder()
                .id(202L)
                .cabin(Cabin.BUSINESS)
                .price(new BigDecimal("300.00"))
                .flight(flight2)
                .segmentOrder(2)
                .build();

        var response1 = new BookingItemResponse(201L, "ECONOMY", new BigDecimal("100.00"), 32, 1L,23L, "FL001");
        var response2 = new BookingItemResponse(202L, "BUSINESS", new BigDecimal("300.00"), 22, 2L,24L, "FL002");

        when(bookingRepository.findById(20L)).thenReturn(Optional.of(booking));
        when(bookingItemRepository.findByBookingIdOrderBySegmentOrder(20L)).thenReturn(List.of(item1, item2));
        when(bookingMapper.toItemResponse(item1)).thenReturn(response1);
        when(bookingMapper.toItemResponse(item2)).thenReturn(response2);

        // ACT
        var response = service.listByBooking(20L);

        // ASSERT
        assertThat(response).hasSize(2);
        assertThat(response).extracting(BookingItemResponse::cabin)
                .containsExactly("ECONOMY", "BUSINESS");
        assertThat(response).extracting(BookingItemResponse::flight_number)
                .containsExactly("FL001", "FL002");

        verify(bookingRepository).findById(20L);
        verify(bookingItemRepository).findByBookingIdOrderBySegmentOrder(20L);
        verify(bookingMapper).toItemResponse(item1);
        verify(bookingMapper).toItemResponse(item2);
    }

    @Test
    void shouldReturnEmptyListWhenNoItemsForBooking() {
        // ARRANGE
        var booking = Booking.builder().id(30L).build();

        when(bookingRepository.findById(30L)).thenReturn(Optional.of(booking));
        when(bookingItemRepository.findByBookingIdOrderBySegmentOrder(30L)).thenReturn(List.of());

        // ACT
        var response = service.listByBooking(30L);

        // ASSERT
        assertThat(response).isEmpty();

        verify(bookingRepository).findById(30L);
        verify(bookingItemRepository).findByBookingIdOrderBySegmentOrder(30L);
        verifyNoInteractions(bookingMapper);
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFoundOnList() {
        // ARRANGE
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.listByBooking(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Booking with id 999 not found");

        verify(bookingRepository).findById(999L);
        verify(bookingItemRepository, never()).findByBookingIdOrderBySegmentOrder(any());
    }

    @Test
    void shouldUpdateBookingItemFlight() {
        // ARRANGE
        var oldFlight = Flight.builder()
                .id(10L)
                .number("OLD123")
                .build();

        var newFlight = Flight.builder()
                .id(20L)
                .number("NEW456")
                .build();

        var bookingItem = BookingItem.builder()
                .id(500L)
                .cabin(Cabin.ECONOMY)
                .price(new BigDecimal("200.00"))
                .flight(oldFlight)
                .segmentOrder(5)
                .build();

        when(bookingItemRepository.findById(500L)).thenReturn(Optional.of(bookingItem));
        when(flightRepository.findById(20L)).thenReturn(Optional.of(newFlight));

        doAnswer(inv -> {
            BookingItemUpdateRequest req = inv.getArgument(0);
            BookingItem item = inv.getArgument(1);
            if (req.cabin() != null) item.setCabin(Cabin.valueOf(req.cabin()));
            if (req.price() != null) item.setPrice(req.price());
            if (req.segmentOrder() != null) item.setSegmentOrder(req.segmentOrder());
            return null;
        }).when(bookingMapper).patch(any(), any());

        when(bookingItemRepository.save(bookingItem)).thenReturn(bookingItem);

        var expectedResponse = new BookingItemResponse(
                500L,
                "BUSINESS",
                new BigDecimal("350.00"),
                234,
                20L,
                30L,
                "NEW456"
        );
        when(bookingMapper.toItemResponse(bookingItem)).thenReturn(expectedResponse);

        var updateReq = new BookingItemUpdateRequest("BUSINESS", new BigDecimal("350.00"), 10);

        // ACT
        var response = service.updateItem(500L, 20L, updateReq);

        // ASSERT
        assertThat(response.flight_number()).isEqualTo("NEW456");
        assertThat(response.cabin()).isEqualTo("BUSINESS");
        assertThat(response.price()).isEqualTo(new BigDecimal("350.00"));

        verify(bookingItemRepository).findById(500L);
        verify(flightRepository).findById(20L);
        verify(bookingMapper).patch(updateReq, bookingItem);
        verify(bookingItemRepository).save(bookingItem);
        verify(bookingMapper).toItemResponse(bookingItem);
    }

    @Test
    void shouldThrowExceptionWhenBookingItemNotFoundOnUpdate() {
        // ARRANGE
        when(bookingItemRepository.findById(999L)).thenReturn(Optional.empty());

        var updateReq = new BookingItemUpdateRequest("BUSINESS", new BigDecimal("350.00"), 10);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.updateItem(999L, 20L, updateReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("BookingItem with id 999 not found");

        verify(bookingItemRepository).findById(999L);
        verify(flightRepository, never()).findById(any());
        verify(bookingItemRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenFlightNotFoundOnUpdate() {
        // ARRANGE
        var bookingItem = BookingItem.builder().id(500L).build();

        when(bookingItemRepository.findById(500L)).thenReturn(Optional.of(bookingItem));
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        var updateReq = new BookingItemUpdateRequest("BUSINESS", new BigDecimal("350.00"), 10);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.updateItem(500L, 999L, updateReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Flight with id 999 not found");

        verify(bookingItemRepository).findById(500L);
        verify(flightRepository).findById(999L);
        verify(bookingItemRepository, never()).save(any());
    }

    @Test
    void shouldDeleteBookingItem() {
        // ACT
        service.deleteBookingItem(999L);

        // ASSERT
        verify(bookingItemRepository).deleteById(999L);
    }
}