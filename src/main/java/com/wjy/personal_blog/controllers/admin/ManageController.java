package com.wjy.personal_blog.controllers.admin;


import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.dto.UserPageQueryDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.ArticleService;
import com.wjy.personal_blog.service.NoteService;
import com.wjy.personal_blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.UserDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*
* 这个控制层用于管理员管理用户信息、文章信息等功能
* */

@RestController
@RequestMapping("/manage")
@Slf4j
public class ManageController {

    //用户管理
    @Autowired
    private UserService userService;

    //文章管理
    @Autowired
    private ArticleService articleService;

    //笔记管理
    @Autowired
    private NoteService noteService;

    /*
    * 查询所有用户
    * */
    @GetMapping ("/users")
    public Result<PageResult> allUsers(
    ){
        log.info("开始查询所有用户信息");
        PageResult allUsers = userService.getAllUsers();
        return Result.success(allUsers);
    }

    /*
       查询某个用户
     */
    @PostMapping("/singleUser")
    public Result<PageResult> specificUser(@RequestBody UserDTO userDTO){
        log.info("开始查询用户：{}",userDTO.getUserName());
        PageResult users = userService.findSpecificUser(userDTO);
        return Result.success(users);
    }


    /*
    * 编辑用户信息
    * */
    @PutMapping("/update")
    public Result updateUser(@RequestBody User user){
        log.info("开始更新用户信息：{}",user);
        return Result.success();
    }

    /*
    * 删除用户
    * */
    @DeleteMapping("/delete")
    public Result deleteUser(@RequestParam("id") Integer userId){
        log.info("删除用户");
        userService.deleteUser(userId);
        return Result.success();
    }


    /*
    * 查看所有文章
    * */
    @GetMapping("/allArticles")
    public Result<PageResult> allArticles(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize)
    {
        log.info("管理员查看所有用户的文章");
        PageResult pageResult = articleService.listByAdmin();
        return Result.success(pageResult);
    }


    /*
    * 查看所有笔记
    * */
    @GetMapping("/allNotes")
    public Result<PageResult> allNotes(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize)
    {
        log.info("管理员查看所有用户的笔记");
        PageResult pageResult = noteService.adminNoteList();
        return Result.success(pageResult);
    }

}
