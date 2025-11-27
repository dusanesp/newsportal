package net.filippov.newsportal.service;

import java.util.List;
import java.util.Set;

import net.filippov.newsportal.domain.Tag;

public interface TagService {

    Tag getByName(String name);

    List<String> getAllNames();

    Set<Tag> getTagsFromString(String tagString);
}
