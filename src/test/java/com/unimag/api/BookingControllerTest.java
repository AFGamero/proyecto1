package com.unimag.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.api.dto.BookingDtos.BookingCreateRequest;
import com.unimag.api.dto.BookingDtos.BookingResponse;
import com.unimag.exception.NotFoundException;
import com.unimag.services.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @MockitoBean
    BookingService service;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @Test
    void create_shouldReturn201AndLocation() throws Exception {
        var req = new BookingCreateRequest(1L);
        var resp = new BookingResponse(10L, OffsetDateTime.now(), "Juan Perez", "juan@mail.com", List.of());

        when(service.createBooking(any())).thenReturn(resp);

        mvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/v1/bookings/10")))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void getBooking_shouldReturn200() throws Exception {
        when(service.getBooking(5L))
                .thenReturn(new BookingResponse(5L, OffsetDateTime.now(), "Ana", "ana@mail.com", List.of()));

        mvc.perform(get("/api/v1/bookings/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void getBooking_shouldReturn404WhenNotFound() throws Exception {
        when(service.getBooking(99L)).thenThrow(new NotFoundException("Booking 99 not found"));

        mvc.perform(get("/api/v1/bookings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Booking 99 not found"));
    }


    @Test
    void delete_shouldReturn204() throws Exception {
        mvc.perform(delete("/api/v1/bookings/3")).andExpect(status().isNoContent());
        verify(service).deleteBooking(3L);
    }
}
