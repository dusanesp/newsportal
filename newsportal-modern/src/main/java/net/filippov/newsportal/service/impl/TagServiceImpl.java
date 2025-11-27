package net.filippov.newsportal.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import net.filippov.newsportal.domain.Tag;
import net.filippov.newsportal.repository.TagRepository;
import net.filippov.newsportal.service.TagService;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Tag getByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllNames() {
        return tagRepository.findAllNames();
    }

    @Override
    public Set<Tag> getTagsFromString(String tagString) {
        Set<Tag> tags = new HashSet<>();
        String[] tagNames = tagString.split(",");
        for (String tagName : tagNames) {
            String trimmedName = tagName.trim();
            if (!trimmedName.isEmpty()) {
                Tag tag = tagRepository.findByName(trimmedName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(trimmedName);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
        }
        return tags;
    }
}
