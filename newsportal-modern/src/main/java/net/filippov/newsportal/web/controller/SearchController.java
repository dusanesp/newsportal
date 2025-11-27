package net.filippov.newsportal.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.filippov.newsportal.domain.Article;
import net.filippov.newsportal.service.ArticleService;
import net.filippov.newsportal.service.CategoryService;
import net.filippov.newsportal.service.TagService;

/**
 * Controller for search operations
 */
@Controller
public class SearchController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public SearchController(ArticleService articleService, CategoryService categoryService, TagService tagService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    /**
     * Handle search form submission
     */
    @PostMapping("/search")
    public String searchSubmit(@RequestParam("query") String query) {
        try {
            return "redirect:/search/" + URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "redirect:/search/" + query;
        }
    }

    /**
     * Display search results for the given fragment
     */
    @GetMapping("/search/{fragment}")
    public String search(@PathVariable("fragment") String fragment, Model model,
            @PageableDefault(size = 5, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Article> articles = articleService.search(fragment, pageable);

        model.addAttribute("fragment", fragment);
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAllNames());

        return "search";
    }
}
