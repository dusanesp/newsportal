package net.filippov.newsportal.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import net.filippov.newsportal.domain.Article;
import net.filippov.newsportal.domain.Category;
import net.filippov.newsportal.domain.Comment;
import net.filippov.newsportal.domain.Tag;
import net.filippov.newsportal.domain.User;
import net.filippov.newsportal.repository.ArticleRepository;
import net.filippov.newsportal.repository.CategoryRepository;
import net.filippov.newsportal.repository.CommentRepository;
import net.filippov.newsportal.repository.TagRepository;
import net.filippov.newsportal.repository.UserRepository;
import net.filippov.newsportal.service.ArticleService;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository, UserRepository userRepository,
            CategoryRepository categoryRepository, TagRepository tagRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Article save(Article article) {
        if (article.getId() == null) {
            // New article
        } else {
            article.setLastModified(new Date());
        }
        return articleRepository.save(article);
    }

    @Override
    @Transactional(readOnly = true)
    public Article get(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
    }

    @Override
    public Article get(Long id, Long userId) {
        Article article = get(id);
        if (userId != null && !userId.equals(article.getAuthor().getId())) {
            article.setViewCount(article.getViewCount() + 1);
            articleRepository.save(article);
        } else if (userId == null) {
            article.setViewCount(article.getViewCount() + 1);
            articleRepository.save(article);
        }
        return article;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Article> getAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Article> getByCategory(String categoryName, Pageable pageable) {
        return articleRepository.findByCategoryName(categoryName, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Article> getByTag(String tagName, Pageable pageable) {
        return articleRepository.findByTagsName(tagName, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Article> getByAuthor(Long authorId, Pageable pageable) {
        return articleRepository.findByAuthorId(authorId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Article> search(String fragment, Pageable pageable) {
        return articleRepository.findByContentContaining(fragment, pageable);
    }

    @Override
    public Article add(Article article, Long authorId, String categoryName, String tagString) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        article.setAuthor(author);

        if (categoryName != null && !categoryName.isEmpty()) {
            Category category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            article.setCategory(category);
        }

        if (tagString != null && !tagString.isEmpty()) {
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
            article.setTags(tags);
        }

        return articleRepository.save(article);
    }

    @Override
    public void update(Article article, String categoryName, String tagString) {
        Article existingArticle = get(article.getId());

        existingArticle.setTitle(article.getTitle());
        existingArticle.setPreview(article.getPreview());
        existingArticle.setContent(article.getContent());
        existingArticle.setLastModified(new Date());

        if (categoryName != null && !categoryName.isEmpty()) {
            Category category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            existingArticle.setCategory(category);
        }

        if (tagString != null && !tagString.isEmpty()) {
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
            existingArticle.setTags(tags);
        }

        articleRepository.save(existingArticle);
    }

    @Override
    public void addComment(Long articleId, String content, String username) {
        Article article = get(articleId);
        User author = userRepository.findByLogin(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Comment comment = new Comment(author, article, content);
        commentRepository.save(comment);
    }

    @Override
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Article> getTopViewed() {
        return articleRepository.findTop5ByOrderByViewCountDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Article> getMostPopular(int limit) {
        return articleRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, limit,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "viewCount")))
                .getContent();
    }
}
