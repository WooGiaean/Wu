package com.wjy.personal_blog.service.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.CommentMapper;
import com.wjy.personal_blog.mapper.NoteMapper;
import com.wjy.personal_blog.pojo.entity.Comment;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.service.CommentService;
import com.wjy.personal_blog.service.impl.CommentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.wjy.personal_blog.constants.RedisConstant.*;

@Service
@Slf4j
public class UserStatisticsService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private NoteMapper noteMapper;

  /*  @Autowired
    private CommentService commentService;*/

    @Autowired
    private CommentMapper commentMapper;

    // 获取用户文章数量
    public int getArticleCount(Integer userId) {
        String key = USER_ARTICLE_COUNT_KEY + userId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return (Integer) value;
        }

        // 缓存不存在，从数据库查询
        int count = articleMapper.countArticleByUserId(userId);
        redisTemplate.opsForValue().set(key, count, 10, TimeUnit.MINUTES); // 缓存10min
        return count;
    }

    // 获取用户笔记数量
    public int getNoteCount(Integer userId) {
        String key = USER_NOTE_COUNT_KEY + userId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return (Integer) value;
        }

        // 缓存不存在，从数据库查询
        int count = noteMapper.countNoteByUserId(userId);
        redisTemplate.opsForValue().set(key, count, 10, TimeUnit.MINUTES); // 缓存10min
        return count;
    }

    // 获取用户评论数量
    public int getCommentCount(Integer userId) {
        String key = USER_COMMENT_COUNT_KEY + userId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return (Integer) value;
        }

        // 缓存不存在，从数据库查询
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        int count = commentMapper.selectCount(queryWrapper).intValue();
        redisTemplate.opsForValue().set(key, count, 10, TimeUnit.MINUTES); // 缓存10min
        return count;
    }


    // 更新用户文章数量
    public void updateArticleCount(Integer userId) {
        String key = USER_ARTICLE_COUNT_KEY + userId;
        int count = articleMapper.countArticleByUserId(userId);
        redisTemplate.opsForValue().set(key, count, 10, TimeUnit.MINUTES);
    }

    // 更新用户笔记数量
    public void updateNoteCount(Integer userId) {
        String key = USER_NOTE_COUNT_KEY + userId;
        int count = noteMapper.countNoteByUserId(userId);
        redisTemplate.opsForValue().set(key, count, 10, TimeUnit.MINUTES);
    }

    // 更新用户评论数量
    public void updateCommentCount(Integer userId) {
       String key = USER_COMMENT_COUNT_KEY + userId;
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        int count = commentMapper.selectCount(queryWrapper).intValue();
        redisTemplate.opsForValue().set(key, count, 10, TimeUnit.MINUTES);
    }

    // 设置用户统计数据
    public void setUserStatistics(User user) {
    log.info("设置用户统计数据：{}",user);
        if (user != null) {
            user.setArticleCount(getArticleCount(user.getUserId()));
            user.setNoteCount(getNoteCount(user.getUserId()));
            user.setCommentCount(getCommentCount(user.getUserId()));
            log.info("用户统计数据设置成功");
        }
    }


    public void setUsersStatistics(List<User> users) {
        for (User user : users) {
            setUserStatistics(user);
        }
    }

}
