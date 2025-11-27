package net.filippov.newsportal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.filippov.newsportal.domain.Article;

public interface ArticleService {

    Article save(Article article);

    Article get(Long id);

    Article get(Long id, Long userId); // With view count logic

    Page<Article> getAll(Pageable pageable);

    Page<Article> getByCategory(String categoryName, Pageable pageable);

    Page<Article> getByTag(String tagName, Pageable pageable);

    Page<Article> getByAuthor(Long authorId, Pageable pageable);

    Page<Article> search(String fragment, Pageable pageable);

    Article add(Article article, Long authorId, String categoryName, String tagString);

    void update(Article article, String categoryName, String tagString);

    void addComment(Long articleId, String content, String username);

    void delete(Long id);

    List<Article> getTopViewed();

    List<Article> getMostPopular(int limit);
}
