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



    /**
     * 新增文章
     * */
    @Insert("insert into article (article_user_id, article_title,article_read_count,article_comment_count,article_like_count, article_content, article_is_comment, article_update_time, article_create_time, article_summary, article_thumbnail,article_status)" +
            " values(#{articleUserId},#{articleTitle},#{articleReadCount},#{articleCommentCount},#{articleLikeCount},#{articleContent},#{articleIsComment},#{articleUpdateTime},#{articleCreateTime},#{articleSummary},#{articleThumbnail},#{articleStatus})")
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
    Article  specificArticle(Integer articleId);


    //更新文章阅读量
    @Update("update article set article_read_count=article_read_count+1 where article_id=#{articleId}")
    int updateCount(Integer articleId);


    //查询对应用户文章数量
    /*@Select("select article_user_id, count(*) as articlCount from " +
            "article where article_user_id in " +
            "#{userIds} group by article_user_id")*/
    /*@MapKey("article_user_id")
    List<Map<Integer,Integer>> countByArticleUserIdMapList(@Param("userIds") List<Integer> userIds);*/

   /* @Select("select article_user_id as auId,count(*) as aCount from article where article_user_id in #{userIds} " +
            "group by article_user_id")*/

    @MapKey("article_user_id")
    Map<Integer, Map<String, Long>> countByArticleUserIdMap(@Param("userIds")List<Integer> userIds);


   /* 根据分类查询文章 */
    Page<Article> getArticlesByCategory(@Param("articleUserId") Integer articleUserId,@Param("categoryId") Integer categoryId);


    /* 计算分类下的文章数量 */
    @MapKey("category_id")
    List<Map<String, Object>> getArticleCountByCategories(@Param("categoryIds") List<Integer> categoryIds);
}
