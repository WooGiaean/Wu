package com.wjy.personal_blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.personal_blog.pojo.entity.ArticleCollect;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleCollectMapper extends BaseMapper<ArticleCollect> {

    /**
     * 检查用户是否已收藏文章
     */
    @Select("SELECT COUNT(*) FROM article_collect WHERE article_id = #{articleId} AND user_id = #{userId}")
    int checkUserCollected(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    /**
     * 删除用户对文章的收藏
     */
    @Delete("DELETE FROM article_collect WHERE article_id = #{articleId} AND user_id = #{userId}")
    int deleteCollect(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    /**
     * 获取文章的收藏数量
     */
    @Select("SELECT COUNT(*) FROM article_collect WHERE article_id = #{articleId}")
    int countCollectsByArticleId(@Param("articleId") Integer articleId);

    /**
     * 获取用户收藏的文章ID列表
     */
    @Select("SELECT article_id FROM article_collect WHERE user_id = #{userId}")
    List<Integer> getCollectedArticleIds(@Param("userId") Integer userId);
}
