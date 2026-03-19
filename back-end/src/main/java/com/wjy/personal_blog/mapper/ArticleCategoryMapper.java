package com.wjy.personal_blog.mapper;

import com.wjy.personal_blog.pojo.entity.ArticleCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleCategoryMapper {
    
    /**
     * 批量插入文章分类关联
     * @param articleCategories 文章分类关联列表
     */
    void insertBatch(@Param("articleCategories") List<ArticleCategory> articleCategories);
    
    /**
     * 根据文章ID删除所有关联的分类
     * @param articleId 文章ID
     */
    @Delete("delete from article_category where article_id = #{articleId}")
    void deleteByArticleId(@Param("articleId") Integer articleId);
    
    /**
     * 插入单条文章分类关联
     * @param articleId 文章ID
     * @param categoryId 分类ID
     */
    @Insert("insert into article_category (article_id, category_id) values (#{articleId}, #{categoryId})")
    void insert(@Param("articleId") Integer articleId, @Param("categoryId") Integer categoryId);
}