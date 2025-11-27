package net.filippov.newsportal.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import net.filippov.newsportal.domain.User;
import net.filippov.newsportal.service.CategoryService;
import net.filippov.newsportal.service.TagService;
import net.filippov.newsportal.service.UserService;

/**
 * Controller for user-related operations
 */
@Controller
public class UserController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public UserController(UserService userService, CategoryService categoryService, TagService tagService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    /**
     * Display user profile page
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{id}")
    public String profile(@PathVariable("id") Long userId, Model model) {
        User user = userService.get(userId);

        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAllNames());

        return "profile";
    }

    /**
     * Display current user's profile page
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String currentUserProfile(Model model, java.security.Principal principal) {
        User user = userService.getByLogin(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAllNames());

        return "profile";
    }
}
