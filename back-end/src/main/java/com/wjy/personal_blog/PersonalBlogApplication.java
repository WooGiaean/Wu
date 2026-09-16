package com.wjy.personal_blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
//@CrossOrigin
public class PersonalBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalBlogApplication.class, args);
	}

}
