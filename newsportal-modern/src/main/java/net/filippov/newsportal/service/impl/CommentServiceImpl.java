package net.filippov.newsportal.service.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.filippov.newsportal.domain.Comment;
import net.filippov.newsportal.repository.CommentRepository;
import net.filippov.newsportal.service.CommentService;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void add(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getByArticleId(Long articleId) {
        return commentRepository.findByArticleId(articleId, Sort.by(Sort.Direction.DESC, "created"));
    }
}
