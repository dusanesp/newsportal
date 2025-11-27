package net.filippov.newsportal.service;

import java.util.List;

import net.filippov.newsportal.domain.Category;

public interface CategoryService {

    Category getByName(String name);

    List<Category> getAll();
}
