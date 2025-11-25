package com.wjy.personal_blog.mapper;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface ArticleMapper {

    /**
     * 查询所所有文章
     * */
   /* @Select("select article.*,user.user_nickname as blogger " +
            "from article left join user " +
            "on article.article_user_id = user.user_id" +
            " order by article.article_create_time desc")*/
    Page<Article> list();   //带有标签的文章列表

    /**
    * 模糊查询（单篇/多篇）
    * */
    List<Article> queryConditional(Article article);

    @Insert("insert into article (article_user_id, article_title,article_read_count,article_comment_count,article_like_count, article_content, article_is_comment, article_update_time, article_create_time, article_summary, article_thumbnail,article_status)" +
            " values(#{articleUserId},#{articleTitle},#{articleReadCount},#{articleCommentCount},#{articleLikeCount},#{articleContent},#{articleIsComment},#{articleUpdateTime},#{articleCreateTime},#{articleSummary},#{articleThumbnail},#{articleStatus})")
    void insertArticle(Article article);


    void updateArticle(Article article);

    @Delete("delete from article where article_id = #{articleId}")
    void deleteArticle(Integer articleId);

    //where article_id=#{articleId}
    @Select("select article.*,user.user_nickname as blogger from article left join" +
            " user on article_user_id=user.user_id" +
            "   where article.article_id=#{articleId}")
    Article  specificArticle(Integer articleId);
}
