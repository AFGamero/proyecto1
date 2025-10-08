package com.unimag.services;

import com.unimag.api.dto.TagDtos;
import com.unimag.dominio.entidades.Tag;
import com.unimag.dominio.repositories.TagRepository;
import com.unimag.services.implmnts.TagServiceImpl;
import com.unimag.services.mappers.TagMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    TagRepository tagRepository;

    @Mock
    TagMapper tagMapper;

    @InjectMocks
    TagServiceImpl service;

    @Test
    void shouldCreateTagAndReturnToResponse() {
        // ARRANGE
        var request = new TagDtos.TagCreateRequest("Direct Flight");

        // Entidad que crea el mapper
        var tagToSave = Tag.builder()
                .name("Direct Flight")
                .build();

        // Entidad guardada con ID
        var savedTag = Tag.builder()
                .id(1L)
                .name("Direct Flight")
                .build();

        // Response esperado
        var expectedResponse = new TagDtos.TagResponse(1L, "Direct Flight");

        when(tagMapper.toEntity(request)).thenReturn(tagToSave);

        when(tagRepository.save(tagToSave)).thenReturn(savedTag);

        when(tagMapper.toResponse(savedTag)).thenReturn(expectedResponse);

        // ACT
        var res = service.create(request);

        // ASSERT
        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.name()).isEqualTo("Direct Flight");

        verify(tagMapper).toEntity(request);
        verify(tagRepository).save(tagToSave);
        verify(tagMapper).toResponse(savedTag);
    }

    @Test
    void shouldFindTagById() {
        // ARRANGE
        var tag = Tag.builder()
                .id(10L)
                .name("Non-Stop")
                .build();

        when(tagRepository.findById(10L)).thenReturn(Optional.of(tag));

        var expectedResponse = new TagDtos.TagResponse(10L, "Non-Stop");
        when(tagMapper.toResponse(tag)).thenReturn(expectedResponse);

        // ACT
        var response = service.findById(10L);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Non-Stop");

        verify(tagRepository).findById(10L);
        verify(tagMapper).toResponse(tag);
    }

    @Test
    void shouldThrowExceptionWhenTagNotFoundById() {
        // ARRANGE
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag with id 999 not found");

        verify(tagRepository).findById(999L);
        verify(tagMapper, never()).toResponse(any());
    }

    @Test
    void shouldFindTagByName() {
        // ARRANGE
        var tag = Tag.builder()
                .id(5L)
                .name("Red-Eye")
                .build();

        when(tagRepository.findByName("Red-Eye")).thenReturn(Optional.of(tag));

        var expectedResponse = new TagDtos.TagResponse(5L, "Red-Eye");
        when(tagMapper.toResponse(tag)).thenReturn(expectedResponse);

        // ACT
        var response = service.findByName("Red-Eye");

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.name()).isEqualTo("Red-Eye");

        verify(tagRepository).findByName("Red-Eye");
        verify(tagMapper).toResponse(tag);
    }

    @Test
    void shouldThrowExceptionWhenTagNotFoundByName() {
        // ARRANGE
        when(tagRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.findByName("NonExistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag with name NonExistent not found");

        verify(tagRepository).findByName("NonExistent");
        verify(tagMapper, never()).toResponse(any());
    }

    @Test
    void shouldListAllTags() {
        // ARRANGE
        var tag1 = Tag.builder().id(1L).name("Direct").build();
        var tag2 = Tag.builder().id(2L).name("One-Stop").build();
        var tag3 = Tag.builder().id(3L).name("Red-Eye").build();

        when(tagRepository.findAll()).thenReturn(List.of(tag1, tag2, tag3));

        var response1 = new TagDtos.TagResponse(1L, "Direct");
        var response2 = new TagDtos.TagResponse(2L, "One-Stop");
        var response3 = new TagDtos.TagResponse(3L, "Red-Eye");

        when(tagMapper.toResponse(tag1)).thenReturn(response1);
        when(tagMapper.toResponse(tag2)).thenReturn(response2);
        when(tagMapper.toResponse(tag3)).thenReturn(response3);

        // ACT
        var tags = service.findAll();

        // ASSERT
        assertThat(tags).hasSize(3);
        assertThat(tags).extracting(TagDtos.TagResponse::name)
                .containsExactlyInAnyOrder("Direct", "One-Stop", "Red-Eye");

        verify(tagRepository).findAll();
        verify(tagMapper).toResponse(tag1);
        verify(tagMapper).toResponse(tag2);
        verify(tagMapper).toResponse(tag3);
    }

    @Test
    void shouldReturnEmptyListWhenNoTags() {
        // ARRANGE
        when(tagRepository.findAll()).thenReturn(List.of());

        // ACT
        var tags = service.findAll();

        // ASSERT
        assertThat(tags).isEmpty();

        verify(tagRepository).findAll();
        verify(tagMapper, never()).toResponse(any());
    }

    @Test
    void shouldDeleteTag() {
        // ARRANGE
        doNothing().when(tagRepository).deleteById(100L);

        // ACT
        service.deleteById(100L);

        // ASSERT
        verify(tagRepository).deleteById(100L);
    }

}