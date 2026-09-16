package com.wjy.personal_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.constants.RedisConstant;
import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.exceptions.BusinessException;
import com.wjy.personal_blog.mapper.ArticleCategoryMapper;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.CommentMapper;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.*;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.ArticleService;
import com.wjy.personal_blog.service.CategoryService;
import com.wjy.personal_blog.service.TagService;
import com.wjy.personal_blog.service.custom.UserStatisticsService;
import com.wjy.personal_blog.utils.SecurityUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wjy.personal_blog.constants.RedisConstant.ARTICLE_READ_KEY;


@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;



    @Autowired
    private UserStatisticsService statisticsService;


    @Autowired
    private CommentMapper commentMapper;

    /*
     * 历史文章查询（用户下所有文章）
     * */
    @Override
    public PageResult list(Integer currentId) {
        //先获取当前用户ID
        //先到缓存查询，如果缓存中有则直接返回缓存数据，如果没有则查询数据库并添加到缓存中
        //key名称，加入用户ID确保每个用户有自己的缓存

        String cacheKey = RedisConstant.RECENT_ARTICLES_KEY + currentId;
        //过期时间:10min
        long expiredMinutes = RedisConstant.HOT_DATA_EXPIRED_MINUTES;
        //ops操作redis
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        //从缓存中获取数据
        Object cachedData = ops.get(cacheKey);

        //缓存中存在数据
        if (cachedData != null) {
            if (cachedData instanceof PageResult) {
                log.info("从缓存中获取到了文章列表数据");
                return (PageResult) cachedData;
            } else {
                log.warn("缓存数据格式错误，清除key：{}", cacheKey);
                redisTemplate.delete(cacheKey);
            }
        } else {
            log.info("缓存未命中，重建中。。。");
        }
        //缓存中没有数据，从数据库中获取
       // PageHelper.startPage(1, 10);
        log.info("查询当前用户id为:{}所有历史文章", currentId);
        Page<Article> list = articleMapper.list(currentId);
        PageResult pageResult = new PageResult(list.getTotal(), list.getResult());
        log.info("从数据库中获取数据成功，开始写入缓存");
        //将新数据插入到缓存,并设置过期时间
        ops.set(cacheKey, pageResult, expiredMinutes, TimeUnit.MINUTES);

        return pageResult;
    }


    /*
     * 管理员查询所有用户的所有文章
     * */
    @Override
    public PageResult listByAdmin(PageQueryDTO pageQueryDTO) {
        log.info("管理员查看所有文章");
        log.info("分页查询参数：{}", pageQueryDTO);
        int page = pageQueryDTO.getPage() != null ? pageQueryDTO.getPage() : 1;
        int pageSize = pageQueryDTO.getPageSize() != null ? pageQueryDTO.getPageSize() : 10;

        PageHelper.startPage(page, pageSize);
        Page<Article> articles = articleMapper.adminSeeArticlesList();
        log.info("查询成功，返回数据");
        PageResult pageResult = new PageResult(articles.getTotal(), articles.getResult());
        return pageResult;
    }

    /*
     * 查询某一/多条文章数据(模糊查询：关键字查询)
     * */
    @Override
    public List<Article> singleQuery(ArticleDTO articleDTO) {
        log.info("单条/多条查询：{}", articleDTO);
        Integer currentId = SecurityUtil.getCurrentUserId();
        User userById = userMapper.getUserById(currentId);
        if (userById == null) {
            log.warn("当前用户未登录");
            return null;
        }
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);
        article.setArticleUserId(currentId);
        List<Article> articles = articleMapper.queryConditional(article);
        return articles;
    }

    @Override
    public List<Article> searchArticles(String keyword, int page, int pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        return articleMapper.searchArticles(keyword, offset, pageSize);
    }


    /*
     * 新增文章（单篇）
     * */
    @Override
    @Transactional
    public void insertNewArticle(ArticleDTO articleDTO) {
        log.info("添加新文章ing...");
        if (articleDTO == null) {
            throw new BusinessException("文章内容为空");
        }
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);

        //初始化用户id，当前用户id
        Integer currentUserId = SecurityUtil.getCurrentUserId();
        article.setArticleUserId(currentUserId);
        /*
         * 分别设置初始的观看数、评论数和点赞数
         * */
        article.setArticleReadCount(0);
        article.setArticleCommentCount(0);
        article.setArticleLikeCount(0);
        /*
         * 先手动进行status和isComment的设置
         * 0：私有/不允许评论
         * 1：公开/允许评论
         * */
        article.setArticleStatus(1);
        article.setArticleIsComment(1);

        /*
         * 设置新增时间和更新时间
         * */
        article.setArticleCreateTime(LocalDateTime.now());
        article.setArticleUpdateTime(LocalDateTime.now());
        //所有数据准备完成再插入到数据库中
        articleMapper.insertArticle(article);

        // 保存文章分类关联
        handleCategory(articleDTO, article);

        // 保存文章标签关联
        handleTag(articleDTO, article);

        redisTemplate.delete(RedisConstant.RECENT_ARTICLES_KEY);
        // 更新用户统计数据
        statisticsService.updateArticleCount(currentUserId);

    }

    /*
     * 编辑/更新文章信息
     * */
    @Override
    @Transactional
    public void updateArticle(ArticleDTO articleDTO) {
        log.info("更新id为{}的文章", articleDTO.getArticleId());
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);
        //更新修改时间
        article.setArticleUpdateTime(LocalDateTime.now());
        //判断文章是否存在以及判断是否当前用户在执行
        Integer currentId = SecurityUtil.getCurrentUserId();
        Article articleToBeUpdated = articleMapper.specificArticle(article.getArticleId());
        if (articleToBeUpdated == null) {
            log.warn("文章不存在");
            return;
        }
        if (!articleToBeUpdated.getArticleUserId().equals(currentId)) {
            log.warn("用户 {} 没有权限操作此文章", currentId);
            return;
        }
        articleMapper.updateArticle(article);

        // 更新文章分类关联
        // 1. 删除该文章所有现有的分类关联
        articleCategoryMapper.deleteByArticleId(article.getArticleId());

        // 2. 如果有新的分类ID列表，批量插入新的分类关联
        handleCategory(articleDTO, article);

        // 更新文章标签关联
        tagService.updateArticleTags(article.getArticleId(), articleDTO.getTagIds());

        redisTemplate.delete(RedisConstant.RECENT_ARTICLES_KEY);
    }

    private void handleCategory(ArticleDTO articleDTO, Article article) {
        if (articleDTO.getCategoryIds() != null && !articleDTO.getCategoryIds().isEmpty()) {
            List<ArticleCategory> articleCategories = articleDTO.getCategoryIds()
                    .stream().map(categoryId ->
                            new ArticleCategory(article.getArticleId(), categoryId))
                    .collect(Collectors.toList());
            // 批量插入文章分类关联
            articleCategoryMapper.insertBatch(articleCategories);
        }
    }

    private void handleTag(ArticleDTO articleDTO, Article article) {
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            tagService.addTagsToArticle(article.getArticleId(), articleDTO.getTagIds());
        }
    }

    @Override
    @Transactional
    public void deleteArticle(Integer articleId) {
        //判断文章id是否为空，如果id为空则退出
        if (articleId == null) return;

        //判断是否当前用户正在操作，获取当前用户id
        Integer currentId = SecurityUtil.getCurrentUserId();
        if (currentId == null) {
            throw new RuntimeException("用户未登录");
        }

        //获取当前文章信息，进行校验是否是当前用户所属的文章
        Article article = articleMapper.specificArticle(articleId);
        //判断文章是否存在
        if (article == null) {
            log.warn("文章不存在");
            return;
        }

        if (!article.getArticleUserId().equals(currentId)) {
            log.warn("用户 {} 没有权限操作此文章", currentId);
        }


        // 删除文章分类关联
        articleCategoryMapper.deleteByArticleId(articleId);

        // 删除文章标签关联
        tagService.updateArticleTags(articleId, null);

        //删除文章评论关联
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id", articleId);
        commentMapper.delete(wrapper);

        // 删除文章
        articleMapper.deleteArticle(articleId);
        log.info("成功删除文章：{}", articleId);
        redisTemplate.delete(RedisConstant.RECENT_ARTICLES_KEY);
    }

    /*
     * 获取指定文章信息
     * */
    @Override
    @Transactional
    public Article specificArticle(Integer articleId) {
        //用于增加文章的阅读数
        int i = articleMapper.updateReadCount(articleId,1);
        if (i == 0) {
            log.info("文章不存在");
            return null;
        }
        log.info("成功修改{}条文章数据", i);
        log.info("获取文章：{}", articleId);
        Article article = articleMapper.specificArticle(articleId);
        return article == null ? null : article;
    }

    /*
     * 获取指定的公开文章的信息
     * */
    @Override
    public Article publicSpecificArticle(Integer articleId) {

        /**
         * 有关文章浏览量的统计 ( 先获取文章数据后，再进行浏览量的统计 )
         *
         * 获取当前的用户id
         * 1. 如果是游客（为登录），所有游客都使用同一个id：0。文章浏览量只记录一次
         * 2. 如果是登录的用户，判断当前文章的用户id和登录的用户id是否一致
         * 2-1 如果一致，则表示是文章作者浏览自己的文章，不记录浏览量
         * 2-2 如果不一致，则表示是其他人访问文章，记录浏览量
        * */
        //获取当前用户id
        Integer currentUserId = SecurityUtil.getCurrentUserId();
        //如果当前用户id为空，则表示是游客

        Article article = articleMapper.publicArticle(articleId);
        if(article==null){
            log.error("文章不存在");
            throw new BusinessException("文章不存在");
        }
        Integer articleUserId = article.getArticleUserId();
        //判断是否为本人访问
        boolean isAuthor = (currentUserId != null) && currentUserId.equals(articleUserId);


        //非作者本人才能进行文章浏览量记录
        if(!isAuthor){
            //判断是否为游客访问
            int visitorId = (currentUserId!=null)?currentUserId:0;
            //设置浏览记录的key
            String readKey = ARTICLE_READ_KEY + articleId + ":user:" + visitorId;
            //判断用户是否已浏览过该文章
            Boolean isRead = redisTemplate.hasKey(readKey);

            if(isRead==null||!isRead){
                int i = articleMapper.updateReadCount(articleId,1);
                if (i < 1) {
                    log.error("文章不存在");
                    throw new BusinessException("文章不存在");
                }
                //设置用户每个用户浏览的文章的浏览记录，24h内只记录一次
                redisTemplate.opsForValue().set(readKey,"1",24,TimeUnit.HOURS);
                log.info("id为：{} 文章的阅读量 + 1", articleId);
            }
            else{
                log.info("用户 {} 已阅读过文章 {}，不重复计数", currentUserId, articleId);
            }
        }else{
            log.error("用户 {} 已是文章作者，不能访问此文章", currentUserId);
        }

        return article ;
    }


    /*
     * 根据分类查询文章
     * */
    @Override
    public PageResult listArticlesByCategory(Integer currentUserId, Integer categoryId, PageQueryDTO pageQueryDTO) {
        log.info("根据分类查询文章，分类ID：{}，分页参数：{}", categoryId, pageQueryDTO);

        // 设置分页
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());

        // 执行查询
        Page<Article> articles = articleMapper.getArticlesByCategory(currentUserId, categoryId);

        // 封装结果
        return new PageResult(articles.getTotal(), articles.getResult());
    }


    /*
    * 获取公开的文章列表
    * */
    @Override
    public PageResult getPublicArticles(PageQueryDTO pageQueryDTO) {

        int page = pageQueryDTO.getPage() == null ? 1 : pageQueryDTO.getPage();
        int pageSize = pageQueryDTO.getPageSize() == null ? 10 : pageQueryDTO.getPageSize();

        // 设置分页
        PageHelper.startPage(page, pageSize);

        // 执行查询
        List<Article> publics = articleMapper.getPublicArticles();
        // 封装结果
        return new PageResult(publics.size(), publics);
    }


    /*
    * 计算所有文章数量
    * */
    @Override
    public Integer countArticles() {
        return articleMapper.countArticles();
    }

   /*
    * 计算公开文章数量
   * */

    @Override
    public Integer countPublicArticles() {
        return articleMapper.countPublicArticles();
    }


    /*
    * 管理员删除文章
    * */
    @Override
    public void deleteArticleByAdmin(Integer articleId) {
        log.info("管理员删除文章，ID：{}", articleId);

        if (articleId == null) {
            throw new BusinessException("文章ID不能为空");
        }

        Article article = articleMapper.specificArticle(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 删除文章分类关联
        articleCategoryMapper.deleteByArticleId(articleId);

        // 删除文章标签关联
        tagService.updateArticleTags(articleId, null);

        //删除文章评论关联
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id", articleId);
        commentMapper.delete(wrapper);

        articleMapper.deleteArticle(articleId);
    }

    /*
    * 管理员批量删除文章
    * */
    @Override
    public void batchDeleteArticlesByAdmin(List<Integer> articleIdList) {
        log.info("管理员批量删除文章，ID列表：{}", articleIdList);
        if (articleIdList == null || articleIdList.isEmpty()) {
            throw new BusinessException("文章ID列表不能为空");
        }
        articleIdList.forEach(articleId -> {

            // 删除文章分类关联
            articleCategoryMapper.deleteByArticleId(articleId);

            // 删除文章标签关联
            tagService.updateArticleTags(articleId, null);

            //删除文章评论关联
            QueryWrapper<Comment> wrapper = new QueryWrapper<>();
            wrapper.eq("article_id", articleId);
            commentMapper.delete(wrapper);

        });
        articleMapper.deleteBatchArticles(articleIdList);
    }

    //置顶文章的排序值
    private static final int TOP_ORDER = 9999;

    private static final int NORMAL_ORDER = 0;

    /*
    * 设置文章置顶状态
    * */
    @Override
    @Transactional
    public void setArticleTop(Integer articleId, boolean isTop) {
        log.info("设置文章置顶状态，文章ID：{}，置顶状态：{}", articleId, isTop);

        Article article = articleMapper.specificArticle(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        if(isTop){
            //设置文章置顶
            article.setArticleOrder(TOP_ORDER);
            articleMapper.updateArticle(article);
        }else{
            //取消文章置顶
            article.setArticleOrder(NORMAL_ORDER);
            articleMapper.updateArticle(article);
        }

        log.info("文章 {} 置顶状态已设置为：{}", articleId, isTop);
    }


    /**
     * 批量设置文章排序
     * */
    @Override
    public void batchSetArticleOrder(List<Integer> articleIds) {
        log.info("批量设置文章排序，文章ID列表：{}", articleIds);

        for (int i = 0; i < articleIds.size(); i++) {
            Article article = new Article();
            article.setArticleId(articleIds.get(i));
            article.setArticleOrder(articleIds.size() - i); // 倒序，越靠前值越大
            articleMapper.updateArticle(article);
        }
        log.info("文章排序批量设置成功");
    }

    @Override
    public List<Article> listArticlesOrderByTop() {
        return articleMapper.selectPublicArticlesOrderByTop();
    }


    /*
    * 根据标签查询文章
    * */
    @Override
    public List<Article> searchByTag(String tagName) {
        Tag tag = tagService.getTagByName(tagName);
        //标签不存在
        if(tag == null){
            return new ArrayList<>();
        }

        //根据标签ID查询文章
        List<Integer> articleIds = tagService.getArticleIdsByTagId(tag.getTagId());
        if(articleIds == null || articleIds.isEmpty()){
            return new ArrayList<>();
        }


        return articleMapper.selectArticlesByIds(articleIds);
    }

    @Override
    public List<Article> searchByCategoryAndTag(Integer categoryId, String tagName) {
        // 1. 调用 TagService 获取标签ID
        Tag tag = tagService.getTagByName(tagName);
        if (tag == null) {
            log.info("标签不存在，标签名称：{}", tagName);
            return new ArrayList<>();
        }

        // 2. 根据标签ID查询文章ID列表
        List<Integer> articleIds = tagService.getArticleIdsByTagId(tag.getTagId());
        if (articleIds.isEmpty()) {
            log.info("标签下没有文章，标签名称：{}", tagName);
            return new ArrayList<>();
        }

        // 3. 根据分类ID查询文章ID列表
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            log.info("分类不存在，分类ID：{}", categoryId);
            return new ArrayList<>();
        }


        //分类、标签都存在的情况下
        return articleMapper.selectArticlesByCategoryAndTag(categoryId, articleIds);

    }


}
