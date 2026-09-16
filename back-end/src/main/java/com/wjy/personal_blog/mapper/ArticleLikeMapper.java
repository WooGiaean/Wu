package com.wjy.personal_blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.personal_blog.pojo.entity.ArticleLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleLikeMapper extends BaseMapper<ArticleLike> {
    /**
     * 检查用户是否已点赞文章
     */
    @Select("SELECT COUNT(*) FROM article_like WHERE article_id = #{articleId} AND user_id = #{userId}")
    int checkUserLiked(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    /**
     * 删除用户对文章的点赞
     */
    @Delete("DELETE FROM article_like WHERE article_id = #{articleId} AND user_id = #{userId}")
    int deleteLike(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    /**
     * 获取文章的点赞数量
     */
    @Select("SELECT COUNT(*) FROM article_like WHERE article_id = #{articleId}")
    int countLikesByArticleId(@Param("articleId") Integer articleId);
}
