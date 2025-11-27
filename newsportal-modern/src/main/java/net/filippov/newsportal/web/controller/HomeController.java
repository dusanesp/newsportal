package net.filippov.newsportal.web.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import net.filippov.newsportal.service.ArticleService;
import net.filippov.newsportal.service.CategoryService;
import net.filippov.newsportal.service.TagService;

@Controller
public class HomeController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public HomeController(ArticleService articleService, CategoryService categoryService, TagService tagService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @GetMapping("/")
    public String home(Model model,
            @PageableDefault(size = 5, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("articles", articleService.getAll(pageable));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAllNames());
        model.addAttribute("topViewedArticles", articleService.getTopViewed());
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAllNames());
        return "about";
    }
}
