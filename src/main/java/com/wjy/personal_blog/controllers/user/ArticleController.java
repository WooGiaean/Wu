package com.wjy.personal_blog.controllers.user;

import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.ArticleService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
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

    /*
    * 查看历史博客
    * */
    @GetMapping("/list")
    @ResponseBody
    public Result<PageResult> historyArticles(
            @RequestParam(defaultValue = "1")Integer page,
            @RequestParam(defaultValue = "10")Integer pageSize){
       log.info("开始查询：页码{},数量{}",page,pageSize);
      PageResult pageResult = articleService.list();
        //System.out.println(pageResult);
      return Result.success(pageResult);
    }

    /*
    * 跳转到allArticles页面，显示所有的blog文章
    * */
    @GetMapping("/")
    public String turnToArticlesList(){
        return "redirect:/Home/allArticles.html";
    }


    /*
    * 改善前：需要后端控制层控制跳转到具体某篇文章
    * 改善后：直接在前端进行页面之间的跳转
    * */
    /*@GetMapping("/toArticle/{id}")
    public String turnToSpecificArticle(@PathVariable("id")Integer id,HttpSession session){
        session.setAttribute("id",id);
        return "redirect:/Home/article.html";
    }*/

    /*
    * 点击博文的编辑按钮后跳转到编辑页面
    * */
    @GetMapping("/toEditPage")
    public String turnToEditPage(@RequestParam("editId")Integer id){
        return "forward:/Article/editArticle.html";
    }

    /**
    * 单条查询（模糊查询：分类、关键字）
     * 需要传递参数
    * */

    @PostMapping("/searchCondition")
    @ResponseBody
    public Result singleQuery(@RequestBody ArticleDTO articleDTO){
        log.info("单条查询：{}",articleDTO);
        List<Article> articlesList = articleService.singleQuery(articleDTO);
        return Result.success(articlesList);
    }

    /**
     * 添加新文章
     */

    @PostMapping("/insert")
    @ResponseBody
    public Result insertNewArticle(@RequestBody ArticleDTO articleDTO){
        log.info("添加新文章：{}",articleDTO);
        articleService.insertNewArticle(articleDTO);
        return Result.success();
    }

    /**
     * 修改文章内容
     *
     * */

    @PutMapping("/update")
    @ResponseBody
    public Result updateArticle(@RequestBody ArticleDTO articleDTO){
        log.info("修改文章:{}",articleDTO);
        articleService.updateArticle(articleDTO);
        return Result.success();
    }

    /**
     *
     * 删除文章
     *
     * */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Result deleteArticle(@PathVariable("id") Integer articleId){
        log.info("删除文章：{}",articleId);
        articleService.deleteArticle(articleId);
        return Result.success();
    }


    /*
    * 查找存储于session中的文章数据并通过前端vue进行加载展示在页面上
    * */
    @GetMapping("/specificArticle")
    @ResponseBody
    public Result<Article> SpecificArticle(@RequestParam("id") Integer id){
        log.info("查看id为："+id+"的文章");
        Article article = articleService.specificArticle(id);
        if(article==null){
            return Result.error("文章已删除");
        }
        return Result.success(article);
    }


    @GetMapping("/specificArticleForEdit")
    @ResponseBody
    public Result<Article> specificArticleForEdit(HttpSession session){
        Integer editId = (Integer) session.getAttribute("editId");
        log.info("进入id为："+editId+"的文章");
        Article article = articleService.specificArticle(editId);
        if(article==null){
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }
}
