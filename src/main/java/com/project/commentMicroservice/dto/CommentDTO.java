package com.project.commentMicroservice.dto;

import lombok.Data;

@Data
public class CommentDTO {

    private String commentId;
    private String userId;
    private String parentId;
    private String comment;
    private int level;
}
