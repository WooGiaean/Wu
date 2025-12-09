package com.wjy.personal_blog;

import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.dto.LoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

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
	void setArticle() {
		/*ArticleDTO articleDTO=new ArticleDTO();
		articleDTO.setArticleTitle("测试文章");
		articleDTO.setArticleContent("测试文章内容");
		articleDTO.setArticleSummary("测试文章摘要");
		articleDTO.setArticleThumbnail(null);
		articleDTO.setArticleReadCount(0);
		articleDTO.setArticleCommentCount(0);
		articleDTO.setArticleLikeCount(0);
		articleDTO.setArticleStatus(1);
		articleDTO.setArticleIsComment(1);
		redisTemplate.opsForValue().set("blog:recent_article", articleDTO,60, TimeUnit.SECONDS);*/

	}

	@Test
	void getName() {
		//System.out.println(redisTemplate.opsForValue().get("name"));
		System.out.println(redisTemplate.opsForValue().get("login"));
	}
}
