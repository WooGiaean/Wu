package com.wjy.personal_blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.personal_blog.mapper.ArticleCollectMapper;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.pojo.entity.ArticleCollect;
import com.wjy.personal_blog.service.ArticleCollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ArticleCollectServiceImpl extends ServiceImpl<ArticleCollectMapper, ArticleCollect>
        implements ArticleCollectService {
    @Autowired
    private ArticleCollectMapper articleCollectMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    @Transactional
    public boolean toggleCollect(Integer articleId, Integer userId) {
        log.info("切换文章收藏状态，文章ID：{}，用户ID：{}", articleId, userId);

        int isCollected = articleCollectMapper.checkUserCollected(articleId, userId);

        if (isCollected > 0) {
            // 已收藏，取消收藏
            articleCollectMapper.deleteCollect(articleId, userId);
            // 减少文章收藏数（如果需要维护收藏数）
            log.info("用户 {} 取消收藏文章 {}", userId, articleId);
            return false;
        } else {
            // 未收藏，添加收藏
            ArticleCollect collect = new ArticleCollect();
            collect.setArticleId(articleId);
            collect.setUserId(userId);
            collect.setCreateTime(LocalDateTime.now());
            articleCollectMapper.insert(collect);
            log.info("用户 {} 收藏文章 {}", userId, articleId);
            return true;
        }
    }

    @Override
    public boolean isCollected(Integer articleId, Integer userId) {
        return articleCollectMapper.checkUserCollected(articleId, userId) > 0;
    }

    @Override
    public int getCollectCount(Integer articleId) {
        return articleCollectMapper.countCollectsByArticleId(articleId);
    }

    @Override
    public List<Integer> getUserCollectArticleIds(Integer userId) {
        return articleCollectMapper.getCollectedArticleIds(userId);
    }
}
