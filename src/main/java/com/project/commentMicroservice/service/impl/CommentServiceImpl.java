package com.project.commentMicroservice.service.impl;

import com.project.commentMicroservice.entity.Comment;
import com.project.commentMicroservice.repository.CommentRepository;
import com.project.commentMicroservice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        return commentRepository.existsById(commentId)?commentRepository.findById(commentId).get():null;
    }

    @Override
    public void deleteComment(String commentId) {
        Comment comment=getCommentById(commentId);
        int remainingPoints=comment.getLikes()-comment.getDislikes();

        //call profile micro-service to deduct remaining pts
//        final String uri="http://  /profile/"+String.valueOf(-remainingPoints)+"/"+comment.getUserId();
//        RestTemplate restTemplate=new RestTemplate();
//        restTemplate.put(uri,null);

        commentRepository.deleteById(commentId);

        //todo : explore deleteBy syntax to delete all the comments with one query
        //todo : put the exit logic for recursion after 5 iterations, otherwise you might land in problem
        //expecting data to be sane is not the best way to code
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

    @Override
    public String getRootParentId(String commentId) {
        String parentId=getCommentById(commentId).getParentId();
        if(parentId.startsWith("C_"))
            return getRootParentId(parentId);
        return parentId;
    }
}
