
package net.filippov.newsportal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.filippov.newsportal.domain.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByAuthorId(Long id, Pageable pageable);

    Page<Article> findByCategoryName(String name, Pageable pageable);

    Page<Article> findByTagsName(String name, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.title LIKE %:fragment% OR a.preview LIKE %:fragment% OR a.content LIKE %:fragment%")
    Page<Article> findByContentContaining(@Param("fragment") String fragment, Pageable pageable);

    long countByContentContaining(String fragment);

    List<Article> findTop5ByOrderByViewCountDesc();
}
