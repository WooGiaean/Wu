package com.wjy.personal_blog.controllers.user;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.annotation.autoCheck;
import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.ArticleCollectService;
import com.wjy.personal_blog.service.ArticleLikeService;
import com.wjy.personal_blog.service.ArticleService;
import com.wjy.personal_blog.service.UserService;
import com.wjy.personal_blog.utils.SecurityUtil;
import jakarta.servlet.http.HttpSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("userArticleController")
@RequestMapping("/user/articles")
@Slf4j
@Tag(name = "文章管理", description = "用户文章相关接口")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @Autowired
    private UserService userService;


    @Autowired
    private ArticleLikeService articleLikeService;

    @Autowired
    private ArticleCollectService articleCollectService;


    /*
     * 查看历史博客
     * */
    @GetMapping("/list")
    @Operation(summary = "查看文章列表", description = "查看当前用户的文章列表")
    @autoCheck(requireLogin = true, logEnabled = true)
    public Result<PageResult> historyArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
            ) {
        log.info("文章列表查询");
        Integer currentId = SecurityUtil.getCurrentUserId();
        log.info("当前用户 {} 开始查询：页码{},数量{}", currentId, page, pageSize);
        PageResult pageResult = articleService.list(currentId);
        return Result.success(pageResult);
    }


    /**
     * 单条查询（模糊查询：分类、关键字）
     * 需要传递参数
     */

    @PostMapping("/search")
    @Operation(summary = "搜索文章", description = "根据分类或关键字搜索文章")
    public Result singleQuery(@RequestBody ArticleDTO articleDTO) {
        log.info("单条查询：{}", articleDTO);
        List<Article> articlesList = articleService.singleQuery(articleDTO);
        return Result.success(articlesList);
    }

    /**
     * 添加新文章
     */

    @PostMapping("")
    @Operation(summary = "添加新文章", description = "创建一篇新文章")
    public Result insertNewArticle(@RequestBody ArticleDTO articleDTO) {
        log.info("添加新文章...");
        articleService.insertNewArticle(articleDTO);
        return Result.success();
    }

    /**
     * 修改文章内容
     */

    @PutMapping("/{id}")
    @Operation(summary = "修改文章", description = "修改指定文章的内容")
    public Result updateArticle(@PathVariable("id") Integer id,
                                @RequestBody ArticleDTO articleDTO) {
        log.info("修改文章:{}", articleDTO);
        Integer currentId = SecurityUtil.getCurrentUserId();
        User userById = userService.getUserById(currentId);
        if(userById.getUserId()!=currentId){
            return Result.error("非当前用户操作，无权修改其他用户的文章!");
        }
        articleDTO.setArticleId(id);
        articleService.updateArticle(articleDTO);
        return Result.success();
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章", description = "删除指定文章")
    public Result deleteArticle(@PathVariable("id") Integer articleId) {
        log.info("删除文章：{}", articleId);
        articleService.deleteArticle(articleId);
        return Result.success();
    }


    /*
     * 加载具体某篇文章内容
     * */
    @GetMapping("/{id}")
    @Operation(summary = "查看文章详情", description = "查看指定文章的详细内容")
    public Result<Article> SpecificArticle(@PathVariable("id") Integer id) {
        log.info("查看id为：" + id + "的文章");
        // 查看具体某一篇文章
        Article article = articleService.specificArticle(id);
        if (article == null) {
            return Result.error("文章已删除");
        }
        return Result.success(article);
    }


    /*
    * 用户进入到编辑文章页面，加载具体某篇文章内容
    * */
    @GetMapping("/{id}/edit")
    @Operation(summary = "编辑文章", description = "加载文章内容用于编辑")
    public Result<Article> specificArticleForEdit(@PathVariable("id") Integer id) {
        log.info("进入id为：" + id + "的文章");
        Article article = articleService.specificArticle(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }


    /**
     * 文章点赞
     * */
    @PostMapping("/{id}/like")
    @Operation(summary = "文章点赞", description = "点赞或取消点赞文章")
    public Result<Map<String, Object>> likeArticle(@PathVariable("id") Integer articleId) {
        Integer currentUserId = SecurityUtil.getCurrentUserId();
        log.info("用户 {} 点赞文章 {}", currentUserId, articleId);
        boolean isLiked = articleLikeService.toggleLike(articleId, currentUserId);
        int likeCount = articleLikeService.getLikeCount(articleId);

        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("likeCount", likeCount);

        return Result.success(result);
    }

    /**
     * 文章收藏
     * */
    @PostMapping("/{id}/collect")
    @Operation(summary = "文章收藏", description = "收藏或取消收藏文章")
    public Result<Map<String, Object>> collectArticle(@PathVariable("id") Integer articleId) {
        Integer userId = SecurityUtil.getCurrentUserId();
        log.info("用户 {} 收藏文章 {}", userId, articleId);

        boolean isCollected = articleCollectService.toggleCollect(articleId, userId);
        int collectCount = articleCollectService.getCollectCount(articleId);

        Map<String, Object> result = new HashMap<>();
        result.put("isCollected", isCollected);
        result.put("collectCount", collectCount);

        return Result.success(result);
    }



}
