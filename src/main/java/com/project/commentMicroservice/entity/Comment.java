package com.project.commentMicroservice.entity;

import com.project.commentMicroservice.config.StringPrefixedSequenceIdGenerator;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "COMMENT")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @GenericGenerator(name = "comment_seq",
                      strategy = "com.project.commentMicroservice.config.StringPrefixedSequenceIdGenerator",
                      parameters = {
                                    @org.hibernate.annotations.Parameter(name=StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value= "5"),
                                    @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "C_"),
                                    @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
    private String commentId;

    private String userId;
    private String parentId;
    private String comment;
    private int level;
    private int likes;
    private int dislikes;
}
