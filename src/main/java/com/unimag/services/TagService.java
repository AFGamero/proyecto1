package com.unimag.services;

import com.unimag.api.dto.TagDtos.*;
import java.util.List;

public interface TagService {
    TagResponse create(TagCreateRequest request);
    TagResponse findById(Long id);
    TagResponse findByName(String name);
    List<TagResponse> findAll();
    void deleteById(Long id);
}