package com.project.commentMicroservice.controller;

import com.project.commentMicroservice.dto.CommentDTO;
import com.project.commentMicroservice.dto.ResponseDTO;
import com.project.commentMicroservice.entity.Comment;
import com.project.commentMicroservice.service.CommentService;
import com.project.commentMicroservice.service.impl.ProducerService;
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

    @Autowired
    ProducerService producerService;

    @PostMapping("/addComment")
    public ResponseDTO<Comment> addComment(@RequestHeader("userId")String userId, @RequestBody CommentDTO commentDTO){
        ResponseDTO<Comment> responseDTO=new ResponseDTO<>();

        //call questionAndAnswer micro-service to check if thread is open
        String rootParentId;
        if(commentDTO.getParentId().startsWith("C_")){
            rootParentId=commentService.getRootParentId(commentDTO.getParentId());
        }
        else {
            rootParentId=commentDTO.getParentId();
        }
        final String uri="http://10.177.68.235/questions/isThreadOpenCheck/"+rootParentId;
        RestTemplate restTemplate=new RestTemplate();
        boolean result=restTemplate.getForObject(uri,boolean.class);
        if(!result){
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Can't comment as thread is closed!");
            return responseDTO;
        }

        Comment comment=new Comment();
        BeanUtils.copyProperties(commentDTO,comment);
        comment.setUserId(userId);
        comment.setLikes(0);
        comment.setDislikes(0);
        Comment parentComment=commentService.getCommentById(comment.getParentId());
        if(comment.getParentId().startsWith("C_") && (parentComment.getLevel()<5)){
            comment.setLevel(parentComment.getLevel()+1);
        }
        else if(comment.getParentId().startsWith("C_") && (parentComment.getLevel()>=5)){
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Comment level cannot exceed 5!");
            return responseDTO;
        }
        else{
            comment.setLevel(1);
        }
        try{
            commentService.addComment(comment);

            //send to notification micro-service
            producerService.produce(comment);

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

    @GetMapping("/getUserIdByCommentId")
    public ResponseDTO<String> getUserIdByCommentId(@RequestBody String commentId){
        ResponseDTO<String> responseDTO=new ResponseDTO<>();
        try{
            String userId=commentService.getCommentById(commentId).getUserId();
            responseDTO.setSuccess(true);
            responseDTO.setData(userId);
        }
        catch (Exception e){
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Couldn't retrieve user id!");
        }
        return responseDTO;
    }

    @PostMapping("/addLike/{commentId}")
    public ResponseDTO<String> addLike(@PathVariable("commentId")String commentId){
        ResponseDTO<String> responseDTO=new ResponseDTO<>();
        try{
            Comment comment=commentService.getCommentById(commentId);
            comment.setLikes(comment.getLikes()+1);

            //call profile micro-service to add score
//            final String uri="http://  /profile/1/"+comment.getUserId();
//            RestTemplate restTemplate=new RestTemplate();
//            restTemplate.put(uri,null);

            responseDTO.setSuccess(true);
            responseDTO.setData("Added a like!");
        }
        catch (Exception e){
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Error in adding like!");
        }
        return responseDTO;
    }

    @PostMapping("/addDislike/{commentId}")
    public ResponseDTO<String> addDislike(@PathVariable("commentId")String commentId){
        ResponseDTO<String> responseDTO=new ResponseDTO<>();
        try{
            Comment comment=commentService.getCommentById(commentId);
            comment.setDislikes(comment.getDislikes()+1);

            //call profile micro-service to decrease score
//            final String uri="http://  /profile/-1/"+comment.getUserId();
//            RestTemplate restTemplate=new RestTemplate();
//            restTemplate.put(uri,null);

            responseDTO.setSuccess(true);
            responseDTO.setData("Added a dislike!");
        }
        catch (Exception e){
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Error in adding dislike!");
        }
        return responseDTO;
    }
}
