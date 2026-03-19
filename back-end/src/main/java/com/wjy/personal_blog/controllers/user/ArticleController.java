package com.wjy.personal_blog.controllers.user;

import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.ArticleService;
import com.wjy.personal_blog.service.UserService;
import com.wjy.personal_blog.utils.SecurityUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/articles")
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @Autowired
    private UserService userService;

    /*
     * 查看历史博客
     * */
    @GetMapping("/list")
    @ResponseBody
    public Result<PageResult> historyArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
            ) {
        log.info("文章列表查询");
        Integer currentId = SecurityUtil.getCurrentUserId();
        if (currentId == null) {
            return Result.error("请先登录!");
        }
        log.info("当前用户 {} 开始查询：页码{},数量{}", currentId, page, pageSize);
        PageResult pageResult = articleService.list(currentId);
        return Result.success(pageResult);
    }


   /* @GetMapping("/allArticles")
    @ResponseBody
    public Result<PageResult> allArticles(){
        log.info("管理员查询所有文章");
        PageResult pageResult = articleService.listByAdmin();
        return Result.success(pageResult);
    }*/


    /*
     * 跳转到allArticles页面，显示所有的blog文章
     * */
    /*@GetMapping("/")
    public String turnToArticlesList() {
        return "redirect:/Article/allArticles.html";
    }*/


    /*
     * 点击博文的编辑按钮后跳转到编辑页面
     * */
    /*@GetMapping("/toEditPage")
    public String turnToEditPage(@RequestParam("editId") Integer id) {
        return "forward:/Article/editArticle.html";
    }*/

    /**
     * 单条查询（模糊查询：分类、关键字）
     * 需要传递参数
     */

    @PostMapping("/searchCondition")
    @ResponseBody
    public Result singleQuery(@RequestBody ArticleDTO articleDTO) {
        log.info("单条查询：{}", articleDTO);
        List<Article> articlesList = articleService.singleQuery(articleDTO);
        return Result.success(articlesList);
    }

    /**
     * 添加新文章
     */

    @PostMapping("/insert")
    @ResponseBody
    public Result insertNewArticle(@RequestBody ArticleDTO articleDTO) {
        log.info("添加新文章...");
        if (articleDTO == null) {
            return Result.error("文章内容为空");
        }
        articleService.insertNewArticle(articleDTO);
        return Result.success();
    }

    /**
     * 修改文章内容
     */

    @PutMapping("/update")
    @ResponseBody
    public Result updateArticle(@RequestBody ArticleDTO articleDTO) {
        log.info("修改文章:{}", articleDTO);
        Integer currentId = BaseContext.getCurrentId();
        User userById = userService.getUserById(currentId);
        if(userById.getUserId()!=currentId){
            return Result.error("非当前用户操作，无权修改其他用户的文章!");
        }
        articleService.updateArticle(articleDTO);
        return Result.success();
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Result deleteArticle(@PathVariable("id") Integer articleId) {
        log.info("删除文章：{}", articleId);
        articleService.deleteArticle(articleId);
        return Result.success();
    }


    /*
     * 加载具体某篇文章内容
     * */
    @GetMapping("/specificArticle")
    @ResponseBody
    public Result<Article> SpecificArticle(@RequestParam("id") Integer id) {
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
    @GetMapping("/specificArticleForEdit")
    @ResponseBody
    public Result<Article> specificArticleForEdit(HttpSession session) {
        Integer editId = (Integer) session.getAttribute("editId");
        log.info("进入id为：" + editId + "的文章");
        Article article = articleService.specificArticle(editId);
        if (article == null) {
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }
}
