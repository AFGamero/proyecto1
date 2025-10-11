package com.unimag.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.api.dto.AirlineDtos;
import com.unimag.exception.NotFoundException;
import com.unimag.services.AirlineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AirlineController.class)
class AirlineControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired
    ObjectMapper om ;
    @MockitoBean
    AirlineService airlineService;


    @Test
    void Create_ShouldReturn201location() throws Exception {
        //given
        var req = new AirlineDtos.AirlineCreateRequest("IB", "Iberia");
        var saved = new AirlineDtos.AirlineResponse(1L, "IB", "Iberia");

        when(airlineService.create(req)).thenReturn(saved);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/airlines")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req))
        )
                .andExpect(status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Location", "http://localhost/api/airlines/1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Iberia"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.code").value("IB"));

    }

    @Test
        void GetById_ShouldReturn200() throws Exception {
        when(airlineService.findById(1L)).thenReturn(new AirlineDtos.AirlineResponse(1L, "Iberia", "LATAM"));

        mockMvc.perform(get("/api/airlines/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("LATAM"));


    }

    @Test
    void getAll_shouldReturn200WithList() throws Exception {
        var airline1 = new AirlineDtos.AirlineResponse(1L, "AV", "Avianca");
        var airline2 = new AirlineDtos.AirlineResponse(2L, "LA", "Latam");

        when(airlineService.findAll()).thenReturn(List.of(airline1, airline2));
        mockMvc.perform(get("/api/airlines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("AV"))
                .andExpect(jsonPath("$[0].name").value("Avianca"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].code").value("LA"))
                .andExpect(jsonPath("$[1].name").value("Latam"));
    }


    @Test
    void Get_ShouldReturn404() throws Exception {
      when(airlineService.findById(1L)).thenThrow(new NotFoundException("Not Found"));
        mockMvc.perform(get("/api/airlines/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not Found"));

    }

    @Test
    void Delete_ShouldReturn204() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/airlines/1"))
                .andExpect(status().isNoContent());
    }
}