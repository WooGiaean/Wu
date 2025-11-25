package com.wjy.personal_blog.service.impl;

import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.dto.LoginDTO;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /*
    * 登录验证
    * */
    @Override
    public User loginVerify(LoginDTO loginDTO) {
        User userIfExist = userMapper.loginCheck(loginDTO.getUsername(), loginDTO.getPassword());
        return userIfExist!=null?userIfExist:null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = userMapper.getAllUsers();
        return allUsers;
    }

    @Override
    public List<User> getUserByNameOrEmail(UserDTO userDTO) {
        if(userDTO==null){
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);
        List<User> singleUser = userMapper.findSingleUser(user);
        return singleUser;
    }

    /*
    * 插入新用户（新用户注册）
    * */
    @Override
    public void insertNewUser(User user) {
        //新用户，进行数据插入
        user.setUserRegisterTime(Date.valueOf(LocalDate.now()));
        user.setUserLastLoginTime(Date.valueOf(LocalDate.now()));
        // TODO 暂时不确定提交表单的页面有无角色字段
        user.setUserRole(User.USER_ROLE_USER);
        userMapper.insertNewUser(user);
    }
}
