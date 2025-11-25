package com.wjy.personal_blog.service;

import com.wjy.personal_blog.pojo.dto.LoginDTO;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.Result;

import java.util.List;

public interface UserService {

    //用户登录认证
   // Result loginVerify(String username, String password);


    User loginVerify(LoginDTO loginDTO);

    //用户列表
    List<User> getAllUsers();


    //通过名字/邮箱查找用户
    List<User> getUserByNameOrEmail(UserDTO userDTO);

    //插入新用户
    void insertNewUser(User user);
}
