package net.filippov.newsportal.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import net.filippov.newsportal.domain.Article;

import net.filippov.newsportal.domain.User;
import net.filippov.newsportal.repository.UserRepository;
import net.filippov.newsportal.service.ArticleService;
import net.filippov.newsportal.service.CategoryService;
import net.filippov.newsportal.service.ImageService;
import net.filippov.newsportal.service.TagService;

import java.io.IOException;

@Controller
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final UserRepository userRepository;
    private final ImageService imageService;

    public ArticleController(ArticleService articleService, CategoryService categoryService, TagService tagService,
            UserRepository userRepository, ImageService imageService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model, Authentication authentication) {
        Long userId = null;
        // TODO: Get user ID from authentication if needed for view count logic
        // optimization
        model.addAttribute("article", articleService.get(id, userId));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAllNames());
        return "article";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id, @RequestParam String content, Authentication authentication) {
        if (authentication != null) {
            articleService.addComment(id, content, authentication.getName());
        }
        return "redirect:/article/" + id;
    }

    @PreAuthorize("hasRole('AUTHOR')")
    @GetMapping("/add")
    public String addArticlePage(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", categoryService.getAll());
        return "edit-article";
    }

    @PreAuthorize("hasRole('AUTHOR')")
    @PostMapping("/add")
    public String addArticleSubmit(@Valid @ModelAttribute("article") Article article, BindingResult result,
            @RequestParam(value = "categoryName", defaultValue = "") String categoryName,
            @RequestParam(value = "tagString", defaultValue = "") String tagString,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tagString", tagString);
            model.addAttribute("categories", categoryService.getAll());
            return "edit-article";
        }

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = imageService.saveImage(image);
                article.setImageUrl(imageUrl);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("tagString", tagString);
                model.addAttribute("categories", categoryService.getAll());
                return "edit-article";
            } catch (IOException e) {
                model.addAttribute("error", "Failed to upload image. Please try again.");
                model.addAttribute("tagString", tagString);
                model.addAttribute("categories", categoryService.getAll());
                return "edit-article";
            }
        }

        User user = userRepository.findByLogin(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Article savedArticle = articleService.add(article, user.getId(), categoryName, tagString);
        return "redirect:/article/" + savedArticle.getId();
    }

    @PreAuthorize("hasRole('AUTHOR')")
    @GetMapping("/edit/{id}")
    public String editArticlePage(@PathVariable Long id, Model model, Authentication authentication,
            HttpServletRequest request) {
        Article article = articleService.get(id);
        User currentUser = userRepository.findByLogin(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is author or admin
        if (!currentUser.getId().equals(article.getAuthor().getId())
                && !request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/article/" + id;
        }

        model.addAttribute("article", article);
        model.addAttribute("categories", categoryService.getAll());
        if (article.getCategory() != null) {
            model.addAttribute("category", article.getCategory().getName());
        }
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            String tagString = article.getTags().stream()
                    .map(tag -> tag.getName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            model.addAttribute("tagString", tagString);
        }
        return "edit-article";
    }

    @PreAuthorize("hasRole('AUTHOR')")
    @PostMapping("/edit/{id}")
    public String editArticleSubmit(@PathVariable Long id, @Valid @ModelAttribute("article") Article article,
            BindingResult result, @RequestParam(value = "categoryName", defaultValue = "") String categoryName,
            @RequestParam(value = "tagString", defaultValue = "") String tagString,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tagString", tagString);
            model.addAttribute("categories", categoryService.getAll());
            return "edit-article";
        }

        // Get existing article to preserve old image URL
        Article existingArticle = articleService.get(id);
        String oldImageUrl = existingArticle.getImageUrl();

        // Handle image removal
        if (removeImage && oldImageUrl != null) {
            imageService.deleteImage(oldImageUrl);
            article.setImageUrl(null);
        }
        // Handle new image upload
        else if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = imageService.saveImage(image);
                // Delete old image if exists
                if (oldImageUrl != null) {
                    imageService.deleteImage(oldImageUrl);
                }
                article.setImageUrl(imageUrl);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("tagString", tagString);
                model.addAttribute("categories", categoryService.getAll());
                return "edit-article";
            } catch (IOException e) {
                model.addAttribute("error", "Failed to upload image. Please try again.");
                model.addAttribute("tagString", tagString);
                model.addAttribute("categories", categoryService.getAll());
                return "edit-article";
            }
        }
        // Keep existing image
        else {
            article.setImageUrl(oldImageUrl);
        }

        articleService.update(article, categoryName, tagString);
        return "redirect:/article/" + id;
    }

    @PreAuthorize("hasRole('AUTHOR')")
    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Article deleted successfully");
        return "redirect:/";
    }

    @GetMapping("/tags-autocomplete")
    @ResponseBody
    public java.util.List<String> tagsAutocomplete() {
        return tagService.getAllNames();
    }

    @GetMapping("/cancel/{id}")
    public String cancelArticleEdit(@PathVariable Long id) {
        if (id == 0) {
            return "redirect:/";
        }
        return "redirect:/article/" + id;
    }

    @GetMapping("/popular")
    public String popular(Model model) {
        model.addAttribute("articles", articleService.getMostPopular(20));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("tags", tagService.getAllNames());
        model.addAttribute("topViewedArticles", articleService.getTopViewed());
        return "popular";
    }
}
