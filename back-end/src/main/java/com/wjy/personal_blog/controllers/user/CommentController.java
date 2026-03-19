package com.wjy.personal_blog.controllers.user;


import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* 评论功能控制层
* */
@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;


    @PostMapping("/add")
    public Result addComment(){

        return Result.success();
    }
}
