package net.filippov.newsportal.service;

import java.util.List;

import net.filippov.newsportal.domain.Comment;

public interface CommentService {

    void add(Comment comment);

    List<Comment> getByArticleId(Long articleId);
}
