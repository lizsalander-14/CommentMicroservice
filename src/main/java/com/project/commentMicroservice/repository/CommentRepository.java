package com.project.commentMicroservice.repository;

import com.project.commentMicroservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment,String>,PagingAndSortingRepository<Comment,String> {

    Page<Comment> findByParentId(String parentId, Pageable pageable);
}
