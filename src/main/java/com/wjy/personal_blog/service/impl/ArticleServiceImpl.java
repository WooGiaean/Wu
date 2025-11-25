package com.wjy.personal_blog.service.impl;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;


    /*
    * 历史文章查询（所有文章）
    * */
    @Override
    public PageResult list() {
        Page<Article> list = articleMapper.list();
        PageResult pageResult = new PageResult(list.getTotal(),list.getResult());
        return pageResult;
    }

    /*
    * 查询某一/多条文章数据
    * */
    @Override
    public List<Article> singleQuery(ArticleDTO articleDTO) {
        log.info("单条/多条查询：{}",articleDTO);
        Article article=new Article();
        BeanUtils.copyProperties(articleDTO,article);
        List<Article> articles = articleMapper.queryConditional(article);
        return articles;
    }


    /*
    * 新增文章（单篇）
    * */
    @Override
    public void insertNewArticle(ArticleDTO articleDTO) {
        //log.info("添加新文章：{}",articleDTO);
        Article article=new Article();
        BeanUtils.copyProperties(articleDTO,article);

        //初始化用户id：先固定用户
        article.setArticleUserId(2);
        /*
        * 分别设置初始的观看数、评论数和点赞数
        * */
        article.setArticleReadCount(0);
        article.setArticleCommentCount(0);
        article.setArticleLikeCount(0);

        /*
        * 先手动进行status和isComment的设置
        * */
        article.setArticleStatus(0);
        article.setArticleIsComment(0);

        /*
        * 设置新增时间和更新时间
        * */
        article.setArticleCreateTime(LocalDateTime.now());
        article.setArticleUpdateTime(LocalDateTime.now());
        //所有数据准备完成再插入到数据库中
        articleMapper.insertArticle(article);
    }

    /*
    * 编辑/更新文章信息
    * */
    @Override
    public void updateArticle(ArticleDTO articleDTO) {
        log.info("更新文章：{}",articleDTO);
        Article article=new Article();
        BeanUtils.copyProperties(articleDTO,article);
        /*
        * 更新修改时间
        * */
        article.setArticleUpdateTime(LocalDateTime.now());
        articleMapper.updateArticle(article);
    }

    @Override
    public void deleteArticle(Integer articleId) {
        //判断文章id是否为空，如果id为空则退出
        if(articleId==null) return;
        articleMapper.deleteArticle(articleId);
        log.info("成功删除文章：{}",articleId);
    }

    /*
    * 获取指定文章信息
    * */
    @Override
    public Article specificArticle(Integer articleId) {
        Article article = articleMapper.specificArticle(articleId);
        return article==null?null:article;
    }
}
