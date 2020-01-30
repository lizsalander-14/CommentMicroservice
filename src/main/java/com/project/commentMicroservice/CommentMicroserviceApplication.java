package com.project.commentMicroservice;

import com.project.commentMicroservice.repository.CommentRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CommentMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommentMicroserviceApplication.class, args);
	}

}
