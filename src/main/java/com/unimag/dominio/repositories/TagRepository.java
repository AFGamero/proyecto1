package com.unimag.dominio.repositories;

import com.unimag.dominio.entidades.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Busca una etiqueta por nombre ("promo", "eco", "red-eye").
     */
    Optional<Tag> findByName(String name);

    /**
     * Retorna todas las Tag cuyos nombres estén en la lista dada.
     * Útil para filtros múltiples.
     */
    List<Tag> findByNameIn(List<String> names);
}
