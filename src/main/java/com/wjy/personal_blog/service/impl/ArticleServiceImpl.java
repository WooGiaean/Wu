package com.wjy.personal_blog.service.impl;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.constants.RedisConstant;
import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.service.ArticleService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /*
    * 历史文章查询（所有文章）
    * */
    @Override
    public PageResult list() {
        //先到缓存查询，如果缓存中有则直接返回缓存数据，如果没有则查询数据库并添加到缓存中

        //key名称
        String cacheKey = RedisConstant.RECENT_ARTICLES_KEY;
        //过期时间:5min
        long expiredTime = RedisConstant.TEST_EXPIRED_SECONDS;

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        //从缓存中获取数据
        Object cachedData = ops.get(cacheKey);

        //缓存中存在数据
        if(cachedData!=null){
            if(cachedData instanceof PageResult){
                log.info("从缓存中获取到了文章列表数据");
                return (PageResult) cachedData;
            }else {
             log.warn("缓存数据格式错误，清除key：{}",cacheKey);
             redisTemplate.delete(cacheKey);
            }
        }else{
            log.info("缓存未命中，重建中。。。");
        }
        //缓存中没有数据，从数据库中获取
        log.info("从数据库中获取数据");
        Integer currentId = BaseContext.getCurrentId();
        Page<Article> list = articleMapper.list(currentId);
        PageResult pageResult = new PageResult(list.getTotal(),list.getResult());

        log.info("从数据库中获取数据成功，开始写入缓存");
        //将新数据插入到缓存,并设置过期时间
        ops.set(cacheKey,pageResult,60*2, TimeUnit.SECONDS);

        return pageResult;
    }

    /*
    * 查询某一/多条文章数据(模糊查询：关键字查询)
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
        log.info("添加新文章...");
        Article article=new Article();
        BeanUtils.copyProperties(articleDTO,article);

        Integer currentUserId = BaseContext.getCurrentId();

        //初始化用户id，当前用户id
        article.setArticleUserId(currentUserId);
        /*
        * 分别设置初始的观看数、评论数和点赞数
        * */
        article.setArticleReadCount(0);
        article.setArticleCommentCount(0);
        article.setArticleLikeCount(0);

        /*
        * 先手动进行status和isComment的设置
        * 0：未发布
        * 1：已发布
        * 2：草稿
        * 3：回收站
        * 4：待审核
        * 5：待审核通过
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
        redisTemplate.delete(RedisConstant.RECENT_ARTICLES_KEY);
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
        redisTemplate.delete(RedisConstant.RECENT_ARTICLES_KEY);
    }

    @Override
    public void deleteArticle(Integer articleId) {
        //判断文章id是否为空，如果id为空则退出
        if(articleId==null) return;

        //判断是否当前用户正在操作，获取当前用户id
        Integer currentId = BaseContext.getCurrentId();
        if(currentId== null){
            throw new RuntimeException("用户未登录");
        }

        //获取当前文章信息，进行校验是否是当前用户所属的文章
        Article article = articleMapper.specificArticle(articleId);
        //判断文章是否存在
        if(article== null){
            log.warn("文章不存在");
            return;
        }

        if(!article.getArticleUserId().equals(currentId)){
            log.warn("用户 {} 没有权限操作此文章",currentId);
        }

        articleMapper.deleteArticle(articleId);
        log.info("成功删除文章：{}",articleId);
        redisTemplate.delete(RedisConstant.RECENT_ARTICLES_KEY);
    }

    /*
    * 获取指定文章信息
    * */
    @Override
    @Transactional
    public Article specificArticle(Integer articleId) {
        //用于增加文章的阅读数
        int i = articleMapper.updateCount(articleId);
        if(i==0){
            log.info("文章不存在");
            return null;
        }
        log.info("成功修改{}条文章数据", i);
        log.info("获取文章：{}",articleId);
        Article article = articleMapper.specificArticle(articleId);
        return article==null?null:article;
    }
}
