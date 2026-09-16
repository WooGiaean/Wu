package com.wjy.personal_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.personal_blog.pojo.entity.ArticleCollect;

import java.util.List;

public interface ArticleCollectService extends IService<ArticleCollect> {
    /**
     * 收藏文章（如果已收藏则取消收藏）
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return true=收藏成功，false=取消收藏成功
     */
    boolean toggleCollect(Integer articleId, Integer userId);

    /**
     * 检查用户是否已收藏文章
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return true=已收藏，false=未收藏
     */
    boolean isCollected(Integer articleId, Integer userId);

    /**
     * 获取文章的收藏数
     * @param articleId 文章ID
     * @return 收藏数
     */
    int getCollectCount(Integer articleId);

    /**
     * 获取用户收藏的文章ID列表
     * @param userId 用户ID
     * @return 收藏的文章ID列表
     */
    List<Integer> getUserCollectArticleIds(Integer userId);
}
