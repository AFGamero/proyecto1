package com.unimag.services.implmnts;

import com.unimag.api.dto.BookingDtos;
import com.unimag.dominio.entidades.Booking;
import com.unimag.dominio.entidades.BookingItem;
import com.unimag.dominio.entidades.Cabin;
import com.unimag.dominio.entidades.Flight;
import com.unimag.dominio.repositories.BookingItemRepository;
import com.unimag.dominio.repositories.BookingRepository;
import com.unimag.dominio.repositories.FlightRepository;
import com.unimag.services.BookingItemService;
import com.unimag.services.mappers.BookingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingItemServiceImpl implements BookingItemService {
    private final BookingItemRepository bookingItemRepository;
    private final BookingRepository bookingRepository ;
    private final FlightRepository flightRepository;


    @Override
    public BookingDtos.BookingItemResponse addItem(Long bookingId, Long flightId, BookingDtos.BookingItemCreateRequest req) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking with id " + bookingId + " not found"));
        // To Do: Validar que el vuelo exista
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight with id " + flightId + " not found"));

        BookingItem item =BookingItem.builder().cabin(Cabin.valueOf(req.cabin())).price(req.price()).flight(flight).build();
        BookingMapper.addItem(item, booking);

        return BookingMapper.toItemResponse(item);
    }

    @Override
    public BookingDtos.BookingItemResponse getBookingItem(Long id) {

        return bookingItemRepository.findById(id).map(BookingMapper::toItemResponse)
                .orElseThrow(() -> new RuntimeException("Booking item with id " + id + " not found"));
    }

    @Override
    public void deleteBookingItem(Long id) {
        bookingItemRepository.deleteById(id);
    }

    @Override
    public List<BookingDtos.BookingItemResponse> listByBooking(Long bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking with id " + bookingId + " not found"));

        return bookingItemRepository.findByBookingIdOrderBySegmentOrder(booking.getId())
                .stream().map(BookingMapper::toItemResponse).toList();
    }

    @Override
    public BookingDtos.BookingItemResponse updateItem(Long itemId,Long flightId, BookingDtos.BookingItemUpdateRequest req) {
    var bookingItem = bookingItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Booking item with id " + itemId + " not found"));

    var flight = flightRepository.findById(flightId)
            .orElseThrow(() -> new RuntimeException("Flight with id " + flightId + " not found"));

    bookingItem.setFlight(flight);
        return BookingMapper.toItemResponse(bookingItem);
    }


    @Override
    public void removeItem(Long itemId) {
    bookingItemRepository.deleteById(itemId);
    }
}
