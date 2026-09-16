package com.wjy.personal_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.personal_blog.pojo.entity.ArticleLike;

public interface ArticleLikeService extends IService<ArticleLike> {
    /**
     * 点赞文章（如果已点赞则取消点赞）
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return true=点赞成功，false=取消点赞成功
     */
    boolean toggleLike(Integer articleId, Integer userId);

    /**
     * 检查用户是否已点赞文章
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return true=已点赞，false=未点赞
     */
    boolean isLiked(Integer articleId, Integer userId);

    /**
     * 获取文章的点赞数
     * @param articleId 文章ID
     * @return 点赞数
     */
    int getLikeCount(Integer articleId);
}
