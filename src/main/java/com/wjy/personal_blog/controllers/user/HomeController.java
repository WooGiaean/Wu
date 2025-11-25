package com.wjy.personal_blog.controllers.user;

import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.pojo.entity.Notes;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.ArticleService;
import com.wjy.personal_blog.service.NoteService;
import com.wjy.personal_blog.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home")
@Slf4j
public class HomeController {


    @Autowired
    private NoteService noteService;


    @Autowired
    private ArticleService articleService;

    /**
     * 获取当前登录用户信息
     * */
    @GetMapping("/currentUser")
    @ResponseBody
    public Result<User> getCurrentUser(HttpSession session){
        User user = (User)session.getAttribute("user");
        return Result.success(user);
    }

    /**
     *
     * 主页展示最近的笔记列表
    * */
    @GetMapping("/latestNotes")
    @ResponseBody
    public Result<PageResult> getLatestNotes(@RequestParam(defaultValue = "4") Integer limit){
        log.info("获取最近的{}条笔记",limit);
        if (limit>10){
            //防止最大值滥用
            limit=4 ;
        }
        PageHelper.startPage(1,limit);
        PageResult pageResult = noteService.noteList();
        return Result.success(pageResult);
    }


    /**
     * 主页展示最近的博文列表
     * @return
     */
    @GetMapping("/latestArticles")
    @ResponseBody
    public Result<PageResult> getLatestArticles(@RequestParam(defaultValue = "5") Integer limit){
        log.info("获取最近的{}篇博文",limit);
        PageHelper.startPage(1,limit);
        PageResult list = articleService.list();
        return Result.success(list);
    }

    /*
    * 跳转到笔记页面
    * */
    @GetMapping("/note")
    public String notesPage(){
        return "forward:/Home/note.html";
    }

    /*
    *
    * 退出登录
    * */
    @GetMapping("/logout")
    public String logout(){
        return "redirect:/Admin/login.html";
    }

    @GetMapping("/selfIntroduce")
    public String profile(){
        return "forward:/Home/profile.html";
    }
}
