package com.wjy.personal_blog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.wjy.personal_blog.configs.RabbitMQConfig;
import com.wjy.personal_blog.json.JacksonObjectMapper;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.NoteMapper;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.dto.LoginDTO;
import com.wjy.personal_blog.pojo.entity.Category;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.service.NoteService;
import com.wjy.personal_blog.service.impl.CategoryServiceImpl;
import com.wjy.personal_blog.utils.GenerateVerifyCode;
import com.wjy.personal_blog.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wjy.personal_blog.constants.RedisConstant.USER_TOKEN_PREFIX;

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
      /*  LoginDTO l = new LoginDTO();
        l.setUsername("tempLogin");
        l.setPassword("123321");
        redisTemplate.opsForValue().set("login", l);
        log.info("设置成功");*/
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

  /*  @Test
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
*/





    @Test
    void testCode() {
        System.out.println(GenerateVerifyCode.generateCode6());
    }


    @Value("${blog.upload.path}")
    private  String UPLOAD_PATH ;

    @Test
    void testAnnotation() {
        System.out.println(UPLOAD_PATH);
    }

    @Autowired
    private CategoryServiceImpl categoryService;

    @Test
    void testCategory() {
        List<Category> list = categoryService.list();
        // 获取分类ID列表
        List<Integer> categoryIds = list.stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList());

        List<Map<String, Object>> maps = articleMapper.getArticleCountByCategories(categoryIds);

        // 将分类名称与数量合并
        Map<Integer, Integer> countMap = new HashMap<>();
        for (Map<String, Object> map : maps) {
            Integer categoryId = (Integer) map.get("categoryId");
            Integer count = ((Long) map.get("articleCount")).intValue();
            countMap.put(categoryId, count);
        }

        // 构建结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category category : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", category.getCategoryId());
            item.put("categoryName", category.getCategoryName());
            item.put("articleCount", countMap.getOrDefault(category.getCategoryId(), 0));
            result.add(item);
        }



        System.out.println(maps);
        System.out.println(result);
    }

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testJWT() {
        //System.out.println(jwtUtil.generateSecretKey());
        System.out.println(jwtUtil.generateToken(1,"Wuhu","admin"));

    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void testSendEmail() {

        String exchangeName="test.fanout";

        String testMessage="这是一段测试文本，测试Java客户端是否能成功向RabbitMQ发送并接收消息。";

        try {
            rabbitTemplate.convertAndSend(exchangeName,
                    "",
                    testMessage);
            log.info("成功发送消息："+testMessage) ;
        } catch (AmqpException e) {
            log.error("发送消息失败："+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "test.queue")
    void testReceive(String msg){
        log.info("收到消息："+msg);
    }



    @Test
    void testRedis(){
        String username="Giaean";
        // 查询用户信息
        User user = userMapper.findByUsername(username);
        log.info("查询用户:{}", user.getUserName());

        //用户存在，生成JWT令牌信息
        //生成JWT令牌信息
        String token = jwtUtil.generateToken(user.getUserId(), user.getUserName(), user.getUserRole());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);

        redisTemplate.opsForValue().set("token:userId:"+user.getUserId(), map);

    }


    private JacksonObjectMapper jacksonObjectMapper;

    @Test
    void testRedisGet(){
    Map<String, Object> userMap =(Map<String, Object>) redisTemplate.opsForValue().get("token:userId:12");
        if(userMap!=null&&userMap.containsKey("user")){
            log.info("用户信息存在");
           // User user = objectMapper.convertValue(userMap.get("user"), User.class);
            User user1 = (User)userMap.get("user");
            log.info("用户信息:{}", user1.getUserName());
        }else{
            log.info("用户信息不存在");
            System.out.println(userMap);
            //System.out.println("用户user："+userMap.get("user"));
            //System.out.println("token："+userMap.get("token"));
        }

    }

}
