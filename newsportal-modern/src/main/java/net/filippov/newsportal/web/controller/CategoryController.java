package net.filippov.newsportal.web.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import net.filippov.newsportal.domain.Article;
import net.filippov.newsportal.service.ArticleService;
import net.filippov.newsportal.service.CategoryService;
import net.filippov.newsportal.service.TagService;

@Controller
@RequestMapping("/category")
public class CategoryController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public CategoryController(ArticleService articleService, CategoryService categoryService, TagService tagService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @GetMapping("/{name}")
    public String viewCategory(@PathVariable String name, Model model) {
        Pageable pageable = PageRequest.of(0, 100); // Get first 100 articles
        Page<Article> articlesPage = articleService.getByCategory(name, pageable);

        model.addAttribute("categoryName", name);
        model.addAttribute("articles", articlesPage.getContent());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAllNames());
        return "category";
    }
}
