package com.wjy.personal_blog.controllers.user;

import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.ArticleService;
import com.wjy.personal_blog.service.NoteService;
import com.wjy.personal_blog.service.custom.CustomerUserDetails;
import com.wjy.personal_blog.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
@Slf4j
@Tag(name = "主页", description = "主页相关接口")
public class HomeController {


    @Autowired
    private NoteService noteService;


    @Autowired
    private ArticleService articleService;

    /**
     * 获取当前登录用户信息
     * */
    @GetMapping("/currentUser")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    public Result<User> getCurrentUser(){
        CustomerUserDetails currentUser = SecurityUtil.getCurrentUser();
        log.info("获取当前用户信息：{}",currentUser.getUser().getUserName());
        if(currentUser==null){
            return Result.error("用户未登录");
        }
        return Result.success(currentUser.getUser());
    }

    /**
     *
     * 主页展示最近的笔记列表
    * */
    @GetMapping("/latestNotes")
    @Operation(summary = "获取最近笔记", description = "获取当前用户最近的笔记列表")
    public Result<PageResult> getLatestNotes(@RequestParam(defaultValue = "4") Integer limit){
        log.info("获取最近的{}条笔记",limit);
        if (limit>10){
            //防止最大值滥用
            limit=4 ;
        }
        Integer currentId = SecurityUtil.getCurrentUserId();
        PageHelper.startPage(1,limit);
        PageResult pageResult = noteService.noteList(currentId);
        return Result.success(pageResult);
    }


    /**
     * 主页展示最近的博文列表
     * @return
     */
    @GetMapping("/latestArticles")
    @Operation(summary = "获取最近文章", description = "获取当前用户最近的文章列表")
    public Result<PageResult> getLatestArticles(
            @RequestParam(defaultValue = "5") Integer limit){
        Integer currentId = SecurityUtil.getCurrentUserId();
        log.info("获取最近的{}篇博文",limit);
        PageHelper.startPage(1,limit);
        PageResult list = articleService.list(currentId);
        return Result.success(list);
    }

}
