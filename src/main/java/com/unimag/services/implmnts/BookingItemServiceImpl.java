package com.unimag.services.implmnts;

import com.unimag.api.dto.BookingDtos;
import com.unimag.dominio.repositories.BookingItemRepository;
import com.unimag.dominio.repositories.BookingRepository;
import com.unimag.dominio.repositories.FlightRepository;
import com.unimag.exception.NotFoundException;
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
    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final BookingMapper bookingMapper;


    @Override
    public BookingDtos.BookingItemResponse addItem(Long bookingId, Long flightId,BookingDtos.BookingItemCreateRequest req) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));
        // To Do: Validar que el vuelo exista
        var flight = flightRepository.findById(flightId).orElseThrow(()-> new NotFoundException("Flight with id " + flightId + " not found"));

        var item = bookingMapper.toItemEntity(req);
        booking.addItem(item);

        return bookingMapper.toItemResponse(item);
    }

    @Override
    public BookingDtos.BookingItemResponse getBookingItem(Long bookingItemId) {
        var bookingItem = bookingItemRepository.findById(bookingItemId)
                .orElseThrow(() -> new NotFoundException("BookingItem with id " + bookingItemId + " not found"));

        return bookingMapper.toItemResponse(bookingItem);
    }

    @Override
    public void deleteBookingItem(Long id) {
        bookingItemRepository.deleteById(id);
    }
    @Override
    public List<BookingDtos.BookingItemResponse> listByBooking(Long bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));

        return bookingItemRepository.findByBookingIdOrderBySegmentOrder(booking.getId())
                .stream()
                .map(bookingMapper::toItemResponse)
                .toList();
    }


    @Override
    public BookingDtos.BookingItemResponse updateItem(Long itemId, Long flightId, BookingDtos.BookingItemUpdateRequest req) {
        var bookingItem = bookingItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("BookingItem with id " + itemId + " not found"));

        var flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new NotFoundException("Flight with id " + flightId + " not found"));

        // Llama al mapper para aplicar los cambios del request sobre el entity existente
        bookingMapper.patch(req, bookingItem);

        // Actualiza el vuelo
        bookingItem.setFlight(flight);

        // Guarda el item actualizado
        bookingItemRepository.save(bookingItem);

        return bookingMapper.toItemResponse(bookingItem);
    }

}
