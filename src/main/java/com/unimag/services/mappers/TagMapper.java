package com.unimag.services.mappers;

import com.unimag.api.dto.TagDtos.*;
import com.unimag.dominio.entidades.Tag;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
    Tag toEntity(TagCreateRequest request);
    TagResponse toResponse(Tag tag);
    List<TagResponse> toResponseList(List<Tag> tags);
    Set<TagResponse> toResponseSet(Set<Tag> tags);
}
