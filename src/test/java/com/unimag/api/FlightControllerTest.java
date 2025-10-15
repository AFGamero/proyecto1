package com.unimag.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.api.dto.FlightDtos;
import com.unimag.exception.NotFoundException;
import com.unimag.services.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    FlightService flightService;

    @Test
    void create_ShouldReturn201AndLocation() throws Exception {
        // given
        var departure = OffsetDateTime.now();
        var arrival = departure.plusHours(2);

        var req = new FlightDtos.FlightCreateRequest("IB123", departure, arrival);
        var saved = new FlightDtos.FlightResponse(
                10L, "IB123", departure, arrival,
                1L, 2L, 3L, Set.of()
        );

        when(flightService.create(eq(1L), eq(2L), eq(3L), any(FlightDtos.FlightCreateRequest.class)))
                .thenReturn(saved);

        // when + then
        mockMvc.perform(post("/api/airlines/1/Flights")
                        .param("originAirportId", "2")
                        .param("destinationAirportId", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/airlines/1/Flights/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.number").value("IB123"))
                .andExpect(jsonPath("$.airline_id").value(1))
                .andExpect(jsonPath("$.origin_airport_id").value(2))
                .andExpect(jsonPath("$.destination_airport_id").value(3));
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        var departure = OffsetDateTime.now();
        var arrival = departure.plusHours(2);

        var response = new FlightDtos.FlightResponse(
                10L, "IB123", departure, arrival,
                1L, 2L, 3L, Set.of()
        );

        when(flightService.findById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/airlines/1/Flights/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.number").value("IB123"));
    }

    @Test
    void getAll_ShouldReturn200WithList() throws Exception {
        var now = OffsetDateTime.now();
        var flight1 = new FlightDtos.FlightResponse(1L, "AA101", now, now.plusHours(1), 1L, 2L, 3L, Set.of());
        var flight2 = new FlightDtos.FlightResponse(2L, "AA202", now, now.plusHours(2), 1L, 3L, 4L, Set.of());

        when(flightService.findAll()).thenReturn(List.of(flight1, flight2));

        mockMvc.perform(get("/api/airlines/1/Flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].number").value("AA101"))
                .andExpect(jsonPath("$[1].number").value("AA202"));
    }



    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/airlines/1/Flights/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getById_ShouldReturn404_WhenNotFound() throws Exception {
        when(flightService.findById(99L)).thenThrow(new NotFoundException("Flight not found"));

        mockMvc.perform(get("/api/airlines/1/Flights/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Flight not found"));
    }

    @Test
    void addTag_ShouldReturn200() throws Exception {
        var now = OffsetDateTime.now();
        var response = new FlightDtos.FlightResponse(10L, "IB123", now, now.plusHours(2), 1L, 2L, 3L, Set.of());

        when(flightService.addTag(10L, 5L)).thenReturn(response);

        mockMvc.perform(patch("/api/airlines/1/Flights/10/tags/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void removeTag_ShouldReturn200() throws Exception {
        var now = OffsetDateTime.now();
        var response = new FlightDtos.FlightResponse(10L, "IB123", now, now.plusHours(2), 1L, 2L, 3L, Set.of());

        when(flightService.removeTag(10L, 5L)).thenReturn(response);

        mockMvc.perform(delete("/api/airlines/1/Flights/10/tags/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }
}
