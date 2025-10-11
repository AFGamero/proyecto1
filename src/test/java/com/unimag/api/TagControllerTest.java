package com.unimag.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.api.dto.TagDtos;
import com.unimag.exception.NotFoundException;
import com.unimag.services.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoBeans;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TagController.class)
class TagControllerTest {
 @Autowired
 MockMvc mockMvc;

 @Autowired
 ObjectMapper om ;

 @MockitoBean
 TagService tagService;

 @Test
 void create_ShouldReturn201AndLocation() throws Exception{
     var req = new TagDtos.TagCreateRequest("tag1");
     var saved = new TagDtos.TagResponse(1L, "tag1");

        //when + then
        when(tagService.create(req)).thenReturn(saved);

        mockMvc.perform(
                 org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/tags")
                         .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                         .content(om.writeValueAsString(req))
         )
                 .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                 .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Location", "http://localhost/api/tags/1"))
                 .andExpect(jsonPath("$.id").value(1))
                 .andExpect(jsonPath("$.name").value("tag1"));

 }

 @Test
 void GetById_ShouldReturn200() throws Exception{
        when(tagService.findById(1L)).thenReturn(new TagDtos.TagResponse(1L, "tag1"));

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/tags/1")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tag1"));
 }

    @Test
    void GetByName_ShouldReturn200() throws Exception{

     var response = new TagDtos.TagResponse(1L, "tag1");
        when(tagService.findByName("tag1")).thenReturn(response);

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/tags")
                                .param("name", "tag1")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tag1"));
    }

    @Test
    void Get_ShouldReturn404() throws Exception{
        when(tagService.findById(1L)).thenThrow(new NotFoundException("Tag not found"));

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/tags/1")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.message").value("Tag not found"));
    }

    @Test
    void Delete_ShouldReturn204() throws Exception {
        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/tags/1")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNoContent());
    }
}