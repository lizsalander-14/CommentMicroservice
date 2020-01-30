package com.project.commentMicroservice.service;

import com.project.commentMicroservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Comment addComment(Comment comment);
    Comment getCommentById(String commentId);
    void deleteComment(String commentId);
    Page<Comment> getCommentByParentId(String parentId, Pageable pageable);
}
