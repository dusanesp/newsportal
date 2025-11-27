package net.filippov.newsportal.web.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import net.filippov.newsportal.domain.Article;
import net.filippov.newsportal.service.ArticleService;
import net.filippov.newsportal.service.CategoryService;
import net.filippov.newsportal.service.TagService;

/**
 * Controller for tag-based article browsing
 */
@Controller
public class TagController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public TagController(ArticleService articleService, CategoryService categoryService, TagService tagService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    /**
     * Display articles for a specific tag
     */
    @GetMapping("/tag/{tagName}")
    public String viewArticlesByTag(@PathVariable("tagName") String tagName, Model model,
            @PageableDefault(size = 5, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Article> articles = articleService.getByTag(tagName, pageable);

        model.addAttribute("tagName", tagName);
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAllNames());

        return "tag";
    }
}
