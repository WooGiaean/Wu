package com.wjy.personal_blog.service;


import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import com.wjy.personal_blog.result.PageResult;
import jakarta.servlet.http.HttpSession;
import jdk.dynalink.linker.LinkerServices;

import java.util.List;

public interface ArticleService {

    /*
    * 查询当前用户所有历史文章
    * */
    PageResult list();


    PageResult listByAdmin(PageQueryDTO pageQueryDTO);


    /*
    * 查询某一/几篇文章（模糊查询）
    * */
    List<Article> singleQuery(ArticleDTO articleDTO);


    /*
    * 添加新文章
    * */
    void insertNewArticle(ArticleDTO articleDTO);

    /*
    * 编辑文章
    * */
    void updateArticle(ArticleDTO articleDTO);

    /*
    * 删除指定文章
    * */
    void deleteArticle(Integer articleId);


    /*
    * 进入具体文章
    * */
    Article specificArticle(Integer articleId);

    /*
    * 根据分类查询文章
    * 需要传递参数：categoryId
    * */
    PageResult listArticlesByCategory(Integer categoryId, PageQueryDTO pageQueryDTO);

}
