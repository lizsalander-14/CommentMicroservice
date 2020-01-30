package com.project.commentMicroservice.dto;

import lombok.Data;

@Data
public class ResponseDTO<T>{

    private Boolean success;
    private String message;
    private T data;
}
