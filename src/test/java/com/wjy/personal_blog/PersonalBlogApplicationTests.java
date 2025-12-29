package com.wjy.personal_blog;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.NoteMapper;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.dto.LoginDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.service.NoteService;
import com.wjy.personal_blog.utils.GenerateVerifyCode;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
class PersonalBlogApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private ArticleMapper articleMapper;

    @Test
    void contextLoads() {
        //	redisTemplate.opsForValue().set("name", "WooGiaean");
        LoginDTO l = new LoginDTO();
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

    @Test
    void verifyCode() {
        System.out.println(RandomStringUtils.randomNumeric(4));
        System.out.println(RandomStringUtils.randomNumeric(6));
        System.out.println(RandomStringUtils.randomAlphanumeric(6).toUpperCase());
    }


   /* @Test
    void testSQL() {
        //获取所有用户
        Page<User> allUsers = userMapper.getAllUsers();
        //获取所有用户id
        List<Integer> articleCount = allUsers.stream()
                        .map(User::getUserId)
                                .collect(Collectors.toList());

        System.out.println("用户列表id："+articleCount);
        //获取用户对应的文章数

        Map<Integer, Map<String, Long>> map = articleMapper.countByArticleUserIdMap(articleCount);
        System.out.println("map内部数据情况：" + map);

        Map<Integer, Integer> articleCountMap = new HashMap<>();
        for (Map.Entry<Integer, Map<String, Long>> entry : map.entrySet()) {
            articleCountMap.put(entry.getKey(), entry.getValue().get("articleCount").intValue());
        }
        System.out.println(articleCountMap);
    }*/


    @Autowired
    private NoteService noteService;
    @Autowired
    private NoteMapper noteMapper;

    @Test
    void testNote(){
        Page<User> allUsers = userMapper.getAllUsers();
        //获取所有用户id
        List<Integer> userIds = allUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
        System.out.println(userIds);

        Map<Integer, Map<String, Long>> map = noteMapper.countNoteByUserId(userIds);
        System.out.println(map);

        Map<Integer, Integer> noteCountMap = new HashMap<>();
        for (Map.Entry<Integer, Map<String, Long>> entry : map.entrySet()) {
            noteCountMap.put(entry.getKey(), entry.getValue().get("noteCount").intValue());
        }
        System.out.println(noteCountMap);

        for (User user : allUsers) {
            user.setNoteCount(noteCountMap.getOrDefault(user.getUserId(),0));
        }

        allUsers.forEach(user -> {
            System.out.println(user.getUserId()+":"+user.getNoteCount());
        });

    }





	/*public Map<Integer,Integer> mapConvert(Map<Integer, Map<String, Object>> map){
		Map<Integer,Integer> formattedMap=new HashMap<>();
		for (Map.Entry<Integer, Map<String, Object>> entry : map.entrySet()) {
			Integer key = entry.getKey();
			Number countNumber = (Number) entry.getValue().get("count(*)");
			Integer value = countNumber.intValue(); // 安全转换
			formattedMap.put(key, value);
		}
		return formattedMap;
	}*/


    @Test
    void testCode() {
        System.out.println(GenerateVerifyCode.generateCode6());
    }


    @Test
    void testVersion() {

    }
}
