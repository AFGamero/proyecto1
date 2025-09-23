package com.unimag.repositories;

import com.unimag.entidades.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("Debe encontrar Tag por nombre")
    void testFindByName() {
        // Arrange
        Tag promo = new Tag();
        promo.setName("promo");
        tagRepository.save(promo);

        // Act
        Optional<Tag> encontrado = tagRepository.findByName("promo");

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getName()).isEqualTo("promo");
    }

    @Test
    @DisplayName("Debe encontrar Tags por lista de nombres")
    void testFindByNameIn() {
        // Arrange
        Tag eco = new Tag();
        eco.setName("eco");

        Tag redEye = new Tag();
        redEye.setName("red-eye");

        Tag luxury = new Tag();
        luxury.setName("luxury");

        tagRepository.saveAll(Arrays.asList(eco, redEye, luxury));

        // Act
        List<Tag> encontrados = tagRepository.findByNameIn(Arrays.asList("eco", "red-eye"));

        // Assert
        assertThat(encontrados).hasSize(2);
        assertThat(encontrados).extracting(Tag::getName)
                .containsExactlyInAnyOrder("eco", "red-eye");
    }
}
