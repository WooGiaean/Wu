package com.wjy.personal_blog.mapper;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Mapper
public interface ArticleMapper {

    /**
     * 查询所所有文章
     * */
   /* @Select("select article.*,user.user_nickname as blogger " +
            "from article left join user " +
            "on article.article_user_id = user.user_id" +
            " order by article.article_create_time desc")*/
    Page<Article> list(@Param("articleUserId")Integer articleUserId);   //带有标签的文章列表


    /*
    * 管理员查询所有文章
    * */
    Page<Article> adminSeeArticlesList();



    /**
    * 模糊查询（单篇/多篇）
    * */
    List<Article> queryConditional(Article article);


    // ArticleMapper.java
    List<Article> searchArticles(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 新增文章
     * */
    @Insert("insert into article (article_user_id, article_title,article_read_count,article_comment_count,article_like_count, article_content, article_is_comment, article_update_time, article_create_time, article_summary, article_thumbnail,article_status)" +
            " values(#{articleUserId},#{articleTitle},#{articleReadCount},#{articleCommentCount},#{articleLikeCount},#{articleContent},#{articleIsComment},#{articleUpdateTime},#{articleCreateTime},#{articleSummary},#{articleThumbnail},#{articleStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "articleId")
    void insertArticle(Article article);

    /**
    * 修改文章
    * */
    void updateArticle(Article article);

    /**
     * 删除文章
     * */
    @Delete("delete from article where article_id = #{articleId}")
    void deleteArticle(Integer articleId);


    //查询具体某篇文章
    Article specificArticle(Integer articleId);


    //公开状态下的具体文章
    @Results({
            @Result(id = true, property = "articleId", column = "article_id"),
            @Result(property = "categoryList", column = "article_id",
            many = @Many(select = "com.wjy.personal_blog.mapper." +
                    "CategoryMapper.getCategoryById"))
    })
    @Select("select * from article where article_id=#{articleId} and article_status=1")
    Article publicArticle(Integer articleId);

    //更新文章阅读量
    @Update("update article set article_read_count=article_read_count=#{count} where article_id=#{articleId}")
    int updateReadCount(Integer articleId,Integer count);

    //更新文章评论数
    @Update("update article set article_comment_count=article_comment_count+#{count} where article_id= #{articleId}")
    int updateArticleCommentCount(Integer articleId,Integer count);

    //查询对应用户文章数量
    /*@Select("select article_user_id, count(*) as articlCount from " +
            "article where article_user_id in " +
            "#{userIds} group by article_user_id")*/
    /*@MapKey("article_user_id")
    List<Map<Integer,Integer>> countByArticleUserIdMapList(@Param("userIds") List<Integer> userIds);*/

   /* @Select("select article_user_id as auId,count(*) as aCount from article where article_user_id in #{userIds} " +
            "group by article_user_id")*/

    /*
    获取各个用户对应的文章数量以及博客数量
    * */
    @MapKey("article_user_id")
    Map<Integer, Map<String, Long>> countByArticleUserIdMap(@Param("userIds")List<Integer> userIds);

    /*
    * 计算当前id用户的博客数量和笔记数量
    * */
    @Select("select count(*) from article where article_user_id=#{articleUserId}")
    Integer countArticleByUserId(Integer articleUserId);


   /* 根据分类查询文章 */
    Page<Article> getArticlesByCategory(@Param("articleUserId") Integer articleUserId,@Param("categoryId") Integer categoryId);


    /* 计算分类下的文章数量 */
    @MapKey("category_id")
    List<Map<String, Object>> getArticleCountByCategories(@Param("categoryIds") List<Integer> categoryIds);

    @Select("select * from article where article_status=1")
    Page<Article> getPublicArticles();


    /**
     * 保存草稿
     * */
    //void saveDraft(Article article);

    /**
     * 获取草稿列表
    * */
    //List<Article> getDrafts(@Param("articleUserId") Integer articleUserId);
}
