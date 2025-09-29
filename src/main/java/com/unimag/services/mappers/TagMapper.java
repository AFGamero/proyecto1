package com.unimag.services.mappers;

import com.unimag.api.dto.FlightDtos;
import com.unimag.api.dto.TagDtos;
import com.unimag.dominio.entidades.Tag;

import java.util.Set;
import java.util.stream.Collectors;

public class TagMapper {
    public static Tag toEntity(TagDtos.TagCreateRequest request){
        return  Tag.builder().name(request.name()).build();
    }
    public static TagDtos.TagResponse toResponse(Tag tag){
        return new TagDtos.TagResponse(tag.getId(), tag.getName());
    }
}
