package com.wjy.personal_blog;

import com.wjy.personal_blog.pojo.dto.LoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
@Slf4j
class PersonalBlogApplicationTests {

	@Autowired
	private RedisTemplate redisTemplate;


	@Test
	void contextLoads() {
		//	redisTemplate.opsForValue().set("name", "WooGiaean");
		LoginDTO l=new LoginDTO();
		l.setUsername("tempLogin");
		l.setPassword("123321");
		redisTemplate.opsForValue().set("login", l);
		log.info("设置成功");
	}


	@Test
	void getName() {
		//System.out.println(redisTemplate.opsForValue().get("name"));
		System.out.println(redisTemplate.opsForValue().get("login"));
	}
}
