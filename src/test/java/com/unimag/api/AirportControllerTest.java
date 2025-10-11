package com.unimag.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.api.dto.AirportDtos.AirportCreateRequest;
import com.unimag.api.dto.AirportDtos.AirportResponse;
import com.unimag.exception.NotFoundException;
import com.unimag.services.AirportService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AirportController.class)
public class AirportControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    AirportService service;

    @Test
    void create_shouldReturn201AndLocation() throws Exception {
        var request = new AirportCreateRequest("DMT", "Dayro Moreno Traicionero", "Once Caldas");
        var response = new AirportResponse(1L,"DMT", "Dayro Moreno Traicionero", "Once Caldas");

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/airports").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
                .andExpect(status().isCreated()).
                andExpect(header().string("Location", Matchers.endsWith("/api/v1/airports/1")))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void get_shouldFindByIdAndReturn200AndLocation() throws Exception {
        when(service.findById(5L)).thenReturn(new AirportResponse(5L,"ALA","Alerta Aeropuerto", "HalfMoon"));

        mockMvc.perform(get("/api/v1/airports/5").contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    void get_shouldFindByIdAndReturn404WhenNotFound() throws Exception {
        when(service.findById(78L)).thenThrow(new NotFoundException("Airport with id 78 Not Found"));

        mockMvc.perform(get("/api/v1/airports/78").contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Airport with id 78 Not Found"));
    }

    @Test
    void findAll_shouldReturn200() throws Exception {
        var list = List.of(
                new AirportResponse(1L, "UNI", "Union Bagdalena", "Fundadorezzz"),
                new AirportResponse(2L, "JUN", "Junior Tu Papa", "Barranquilla"),
                new AirportResponse(3L, "AMR", "Andres Manuel Rudas", "Santa Marta")
        );
        when(service.findAll()).thenReturn(list);

        mockMvc.perform(get("/api/v1/airports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].code").value("UNI"));
    }

    @Test
    void delete_shouldDeleteByIdAndReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/airports/5").contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isNoContent());
        verify(service).deleteById(5L);
    }
}
