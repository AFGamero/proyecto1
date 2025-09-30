package com.unimag.services;

import com.unimag.api.dto.TagDtos;
import com.unimag.dominio.entidades.Tag;
import com.unimag.dominio.repositories.TagRepository;
import com.unimag.services.implmnts.TagServiceImpl;
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

    @InjectMocks
    TagServiceImpl service;


    @Test
    void shouldCreateTagAndReturnToResponse() {
        // ARRANGE
        var request = new TagDtos.TagCreateRequest("Direct Flight");

        when(tagRepository.save(any())).thenAnswer(inv -> {
            Tag t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        // ACT
        var res = service.create(request);

        // ASSERT
        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.name()).isEqualTo("Direct Flight");

        verify(tagRepository).save(any(Tag.class));
    }


    @Test
    void shouldFindTagById() {
        // ARRANGE
        var tag = Tag.builder()
                .id(10L)
                .name("Non-Stop")
                .build();

        when(tagRepository.findById(10L)).thenReturn(Optional.of(tag));

        // ACT
        var response = service.findById(10L);

        // ASSERT
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Non-Stop");

        verify(tagRepository).findById(10L);
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
    }

    @Test
    void shouldFindTagByName() {
        // ARRANGE
        var tag = Tag.builder()
                .id(5L)
                .name("Red-Eye")
                .build();

        when(tagRepository.findByName("Red-Eye")).thenReturn(Optional.of(tag));

        // ACT
        var response = service.findByName("Red-Eye");

        // ASSERT
        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.name()).isEqualTo("Red-Eye");

        verify(tagRepository).findByName("Red-Eye");
    }

    @Test
    void shouldThrowExceptionWhenTagNotFoundByName() {

        when(tagRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByName("NonExistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag with name NonExistent not found");

        verify(tagRepository).findByName("NonExistent");
    }


    @Test
    void shouldListAllTags() {

        when(tagRepository.findAll()).thenReturn(
                List.of(
                        Tag.builder().id(1L).name("Direct").build(),
                        Tag.builder().id(2L).name("One-Stop").build(),
                        Tag.builder().id(3L).name("Red-Eye").build()
                )
        );


        var tags = service.findAll();


        assertThat(tags).hasSize(3);
        assertThat(tags).extracting(TagDtos.TagResponse::name)
                .containsExactlyInAnyOrder("Direct", "One-Stop", "Red-Eye");

        verify(tagRepository).findAll();
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
    }


    @Test
    void shouldDeleteTag() {
        // ACT
        service.deleteById(100L);

        verify(tagRepository).deleteById(100L);
    }
}