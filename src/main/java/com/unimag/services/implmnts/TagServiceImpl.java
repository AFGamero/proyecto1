package com.unimag.services.implmnts;

import com.unimag.api.dto.TagDtos;
import com.unimag.dominio.repositories.TagRepository;
import com.unimag.exception.NotFoundException;
import com.unimag.services.TagService;
import com.unimag.services.mappers.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public TagDtos.TagResponse create(TagDtos.TagCreateRequest request) {
        return tagMapper.toResponse(tagRepository.save(tagMapper.toEntity(request)));
    }

    @Override
    public TagDtos.TagResponse findById(Long id) {
        return tagRepository.findById(id).map(tagMapper::toResponse).orElseThrow(() -> new NotFoundException("Tag with id " + id + " not found"));
    }

    @Override
    public TagDtos.TagResponse findByName(String name) {
        return tagRepository.findByName(name).map(tagMapper::toResponse).orElseThrow(() -> new NotFoundException("Tag with name " + name + " not found"));
    }

    @Override
    public List<TagDtos.TagResponse> findAll() {

        return tagRepository.findAll().stream().map(tagMapper::toResponse).toList();
    }

    @Override
    public void deleteById(Long id) {
    tagRepository.deleteById(id);
    }





}
