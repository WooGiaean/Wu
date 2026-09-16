package com.wjy.personal_blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.personal_blog.mapper.ArticleLikeMapper;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.pojo.entity.ArticleLike;
import com.wjy.personal_blog.service.ArticleLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ArticleLikeServiceImpl extends ServiceImpl<ArticleLikeMapper, ArticleLike>
        implements ArticleLikeService {
    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    @Transactional
    public boolean toggleLike(Integer articleId, Integer userId) {
        log.info("切换文章点赞状态，文章ID：{}，用户ID：{}", articleId, userId);

        int isLiked = articleLikeMapper.checkUserLiked(articleId, userId);

        if (isLiked > 0) {
            // 已点赞，取消点赞
            articleLikeMapper.deleteLike(articleId, userId);
            // 减少文章点赞数
            articleMapper.updateArticleLikeCount(articleId, -1);
            log.info("用户 {} 取消点赞文章 {}", userId, articleId);
            return false;
        } else {
            // 未点赞，添加点赞
            ArticleLike like = new ArticleLike();
            like.setArticleId(articleId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            articleLikeMapper.insert(like);
            // 增加文章点赞数
            articleMapper.updateArticleLikeCount(articleId, 1);
            log.info("用户 {} 点赞文章 {}", userId, articleId);
            return true;
        }
    }

    @Override
    public boolean isLiked(Integer articleId, Integer userId) {
        return articleLikeMapper.checkUserLiked(articleId, userId) > 0;
    }

    @Override
    public int getLikeCount(Integer articleId) {
        return articleLikeMapper.countLikesByArticleId(articleId);
    }
}
