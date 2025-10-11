package com.unimag.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.api.dto.SeatInventoryDtos;
import com.unimag.exception.NotFoundException;
import com.unimag.services.SeatInventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(SeatInventoryController.class)
class SeatInventoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SeatInventoryService seatInventoryService;

    @Test
    void create_ShouldReturn201AndLocation() throws Exception {
        var req = new SeatInventoryDtos.SeatInventoryCreateRequest("Economy", 100, 80);
        var saved = new SeatInventoryDtos.SeatInventoryResponse(1L, "Economy", 100, 80, 1L);

        when(seatInventoryService.create(1L, req)).thenReturn(saved);

        mockMvc.perform(post("/api/flights/1/seat-inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/flights/1/seat-inventories/1")))
                .andExpect(content().string("Seat inventory created with ID: 1"));
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        var seat = new SeatInventoryDtos.SeatInventoryResponse(1L, "Economy", 100, 80, 1L);
        when(seatInventoryService.findById(1L)).thenReturn(seat);

        mockMvc.perform(get("/api/flights/1/seat-inventories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cabin").value("Economy"))
                .andExpect(jsonPath("$.totalSeats").value(100))
                .andExpect(jsonPath("$.availableSeats").value(80))
                .andExpect(jsonPath("$.flight_id").value(1));
    }

    @Test
    void getByFlightId_ShouldReturn200WithList() throws Exception {
        var seat1 = new SeatInventoryDtos.SeatInventoryResponse(1L, "Economy", 100, 80, 1L);
        var seat2 = new SeatInventoryDtos.SeatInventoryResponse(2L, "Business", 50, 45, 1L);

        when(seatInventoryService.findByFlightId(1L)).thenReturn(List.of(seat1, seat2));

        mockMvc.perform(get("/api/flights/1/seat-inventories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cabin").value("Economy"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].cabin").value("Business"));
    }

    @Test
    void getByFlightIdAndCabin_ShouldReturn200() throws Exception {
        var seat = new SeatInventoryDtos.SeatInventoryResponse(1L, "Economy", 100, 80, 1L);
        when(seatInventoryService.findByFlightAndCabin(1L, "Economy")).thenReturn(seat);

        mockMvc.perform(get("/api/flights/1/seat-inventories/by-cabin")
                        .param("cabin", "Economy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cabin").value("Economy"))
                .andExpect(jsonPath("$.totalSeats").value(100));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        var req = new SeatInventoryDtos.SeatInventoryUpdateRequest("Economy", 100,20);
        var updated = new SeatInventoryDtos.SeatInventoryResponse(1L, "Economy", 120, 100, 1L);

        when(seatInventoryService.update(1L, req)).thenReturn(updated);

        mockMvc.perform(patch("/api/flights/1/seat-inventories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalSeats").value(120))
                .andExpect(jsonPath("$.availableSeats").value(100));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/flights/1/seat-inventories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getById_ShouldReturn404_WhenNotFound() throws Exception {
        when(seatInventoryService.findById(99L))
                .thenThrow(new NotFoundException("Seat inventory not found"));

        mockMvc.perform(get("/api/flights/1/seat-inventories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seat inventory not found"));
    }

}
