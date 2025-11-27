package net.filippov.newsportal.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.filippov.newsportal.domain.Article;
import net.filippov.newsportal.repository.ArticleRepository;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Test
    void save_ShouldReturnSavedArticle() {
        Article article = new Article();
        article.setTitle("Test Title");
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        Article saved = articleService.save(article);

        assertNotNull(saved);
        assertEquals("Test Title", saved.getTitle());
        verify(articleRepository).save(article);
    }

    @Test
    void get_ShouldReturnArticle_WhenFound() {
        Article article = new Article();
        article.setId(1L);
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        Article found = articleService.get(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }
}
