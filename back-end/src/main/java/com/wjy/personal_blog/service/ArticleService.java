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
    * 查询当前用户所有历史文章（包括公开、私有）
    * */
    PageResult list(Integer currentId);


    PageResult listByAdmin(PageQueryDTO pageQueryDTO);


    /*
    * 查询某一/几篇文章（模糊查询）
    * */
    List<Article> singleQuery(ArticleDTO articleDTO);


    /*
    * 查询文章：使用全文索引fulltext
    * */
    // ArticleService.java
    List<Article> searchArticles(String keyword, int page, int pageSize);


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
    * 进入公开的具体文章
    * */
    Article publicSpecificArticle(Integer articleId);


    /*
    * 根据分类查询文章
    * 当前用户id: currentUserId
    * 需要传递参数：categoryId
    * */
    PageResult listArticlesByCategory(Integer currentUserId,Integer categoryId, PageQueryDTO pageQueryDTO);

    PageResult getPublicArticles(PageQueryDTO pageQueryDTO);


    /**
     * 统计所有文章数量
     * */
    Integer countArticles();

    /**
     * 统计公开状态下的文章数量
     * */
    Integer countPublicArticles();

    void deleteArticleByAdmin(Integer articleId);

    void batchDeleteArticlesByAdmin(List<Integer> articleIdList);


    /**
     * 设置文章置顶状态
     * @param articleId 文章ID
     * @param isTop 是否置顶
     */
    void setArticleTop(Integer articleId, boolean isTop);


    /**
     * 批量设置文章排序
     * @param articleIds 按排序顺序排列的文章ID列表
     */
    void batchSetArticleOrder(List<Integer> articleIds);

    /**
     * 查询文章列表（按置顶排序，时间倒序）
     * @return 文章列表
     * */
    List<Article> listArticlesOrderByTop();


    /**
     * 标签搜索：根据标签名称搜索文章
     */
    List<Article> searchByTag(String tagName);


    /**
     * 分类+标签组合搜索
     */
    List<Article> searchByCategoryAndTag(Integer categoryId, String tagName);
}
