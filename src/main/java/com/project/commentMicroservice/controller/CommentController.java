package com.project.commentMicroservice.controller;

import com.project.commentMicroservice.dto.CommentDTO;
import com.project.commentMicroservice.dto.ResponseDTO;
import com.project.commentMicroservice.entity.Comment;
import com.project.commentMicroservice.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Page;


@RestController
@CrossOrigin("*")
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @PostMapping("/addComment")
    public ResponseDTO<Comment> addComment(@RequestHeader("userId")String userId, @RequestBody CommentDTO commentDTO){
        ResponseDTO<Comment> responseDTO=new ResponseDTO<>();
        Comment comment=new Comment();
        BeanUtils.copyProperties(commentDTO,comment);
        comment.setUserId(userId);
        if(comment.getParentId().startsWith("C_")){
            Comment parentComment=commentService.getCommentById(comment.getParentId());
            if(parentComment.getLevel()<5){
                comment.setLevel(parentComment.getLevel()+1);
            }
            else{
                responseDTO.setSuccess(false);
                responseDTO.setMessage("Comment level cannot exceed 5!");
                return responseDTO;
            }
        }
        else{
            comment.setLevel(1);
        }
        try{
            commentService.addComment(comment);

            //send to notification micro-service
//            final String uri="";
//            HttpHeaders headers=new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<Comment> entityReq=new HttpEntity<>(comment,headers);
//            RestTemplate restTemplate=new RestTemplate();
//            String result=restTemplate.postForObject(uri,entityReq,String.class);
//            System.out.println(result);

            responseDTO.setSuccess(true);
            responseDTO.setData(comment);
        }
        catch (Exception e){
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Couldn't add comment!");
            e.printStackTrace();
        }
        return responseDTO;
    }

    @GetMapping("/getComment/{parentId}/{pages}/{size}")
    public ResponseDTO<Page<Comment>> getComment(@PathVariable("parentId")String id ,@PathVariable("pages")int pages,@PathVariable("size")int size){
        ResponseDTO<Page<Comment>> responseDTO=new ResponseDTO<>();
        try{
            Pageable pageable=PageRequest.of(pages,size);
            Page<Comment> page=commentService.getCommentByParentId(id,pageable);
            responseDTO.setSuccess(true);
            responseDTO.setData(page);
        }
        catch (Exception e){
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Couldn't retrieve comments!");
            e.printStackTrace();
        }
        return responseDTO;
    }

    @PostMapping("/deleteComment/{commentId}")
    public ResponseDTO<String> deleteComment(@PathVariable("commentId")String id){
        ResponseDTO<String> responseDTO=new ResponseDTO<>();
        try{
            commentService.deleteComment(id);
            responseDTO.setSuccess(true);
            responseDTO.setData("Deleted comment!");
        }
        catch (Exception e){
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Couldn't delete comment");
        }
        return responseDTO;
    }
}
