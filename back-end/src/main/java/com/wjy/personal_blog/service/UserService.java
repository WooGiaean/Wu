package com.wjy.personal_blog.service;

import com.wjy.personal_blog.pojo.dto.LoginDTO;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    //用户登录认证
    User loginVerify(LoginDTO loginDTO);

    //用户列表
    PageResult getAllUsers(PageQueryDTO pageQueryDTO);

    //通过id查询用户
    User getUserById(Integer id);

    //通过名字/邮箱查找用户
    List<User> getUserByNameOrEmail(UserDTO userDTO);

    //添加新用户（注册）
    void insertNewUser(User user);

    //修改用户信息
    void updateUser(UserDTO userDTO);

    //删除用户
    void deleteUser(Integer id);

    //批量删除用户
    void batchDeleteUsers(Integer[] userIds);

    //查询用户
    PageResult findSpecificUser(UserDTO userDTO);
    
    //根据用户名查询用户
    User findByUsername(String username);
    
    //添加用户（管理员功能）
    void addUserByAdmin(UserDTO userDTO);


    void updatePassword(Integer userId, String oldPassword, String newPassword);

    String uploadAvatar(Integer userId, MultipartFile file);
}
