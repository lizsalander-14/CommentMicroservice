package com.project.commentMicroservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.commentMicroservice.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ProducerService {
    @Autowired
    KafkaTemplate<String,String> productDtoKafkaTemplate;

    private String TopicName="comment";
    public void produce(Comment comment) throws IOException
    {

        ObjectMapper objectMapper = new ObjectMapper();
        String productDto=objectMapper.writeValueAsString(comment);
        this.productDtoKafkaTemplate.send(TopicName,productDto);


    }
}
