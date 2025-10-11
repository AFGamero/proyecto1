package com.unimag.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.api.dto.BookingDtos.*;
import com.unimag.exception.NotFoundException;
import com.unimag.services.BookingItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingItemController.class)
public class BookingItemControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockitoBean
    BookingItemService service;

    @Test
    void addItem_shouldReturn201AndLocation() throws Exception {
        var req = new BookingItemCreateRequest("ECONOMY", new BigDecimal("150.00"), 1);
        var resp = new BookingItemResponse(10L, "ECONOMY", new BigDecimal("150.00"), 1, 5L, 3L, "AA123");

        when(service.addItem(eq(5L), eq(3L), any())).thenReturn(resp);

        mvc.perform(post("/api/v1/bookings/5/items/flight/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/v1/bookings/5/items/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.cabin").value("ECONOMY"));
    }

    @Test
    void getBookingItem_shouldReturn200() throws Exception {
        when(service.getBookingItem(7L))
                .thenReturn(new BookingItemResponse(7L, "BUSINESS", new BigDecimal("500.00"), 1, 5L, 3L, "BA456"));

        mvc.perform(get("/api/v1/bookings/5/items/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.cabin").value("BUSINESS"));
    }

    @Test
    void getBookingItem_shouldReturn404WhenNotFound() throws Exception {
        when(service.getBookingItem(99L))
                .thenThrow(new NotFoundException("BookingItem 99 not found"));

        mvc.perform(get("/api/v1/bookings/5/items/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("BookingItem 99 not found"));
    }

    @Test
    void listByBooking_shouldReturn200() throws Exception {
        var list = List.of(
                new BookingItemResponse(1L, "ECONOMY", new BigDecimal("150.00"), 1, 5L, 3L, "AA123"),
                new BookingItemResponse(2L, "BUSINESS", new BigDecimal("500.00"), 2, 5L, 4L, "BA456")
        );
        when(service.listByBooking(5L)).thenReturn(list);

        mvc.perform(get("/api/v1/bookings/5/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].cabin").value("ECONOMY"));
    }

    @Test
    void deleteBookingItem_shouldReturn204() throws Exception {
        mvc.perform(delete("/api/v1/bookings/5/items/7"))
                .andExpect(status().isNoContent());
        verify(service).deleteBookingItem(7L);
    }
}
