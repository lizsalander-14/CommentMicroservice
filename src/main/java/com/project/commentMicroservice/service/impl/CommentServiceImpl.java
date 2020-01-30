package com.project.commentMicroservice.service.impl;

import com.project.commentMicroservice.entity.Comment;
import com.project.commentMicroservice.repository.CommentRepository;
import com.project.commentMicroservice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Override
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment getCommentById(String commentId) {
        return commentRepository.findById(commentId).get();
    }

    @Override
    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
        Page<Comment> commentList=commentRepository.findByParentId(commentId,null);
        if(commentList!=null){
            for (Comment childComment:commentList) {
                deleteComment(childComment.getCommentId());
            }
        }
    }

    @Override
    public Page<Comment> getCommentByParentId(String parentId, Pageable pageable) {
        return commentRepository.findByParentId(parentId,pageable);
    }
}
