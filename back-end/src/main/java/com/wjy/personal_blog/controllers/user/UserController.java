package com.wjy.personal_blog.controllers.user;

import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {


    @Autowired
    private UserService userService;

    /*
    * 查询用户资料（获取当前用户信息）
    * */

    @GetMapping("/profile")
    public Result<User> getUserProfile(){
        Integer userId = BaseContext.getCurrentId();
        User userById = userService.getUserById(userId);
        if(userById == null){
            return Result.error("用户不存在");
        }
        return Result.success(userById);
    }


    /**
     * 更新个人资料
     */
    @PutMapping("/profile")
    public Result updateProfile(@RequestBody UserDTO userDTO) {
        Integer userId = BaseContext.getCurrentId();
        if(userDTO.getUserId() == null||!userDTO.getUserId().equals(userId)){
            return Result.error("无权修改他人资料");
        }
        userDTO.setUserId(userId);
        userService.updateUser(userDTO);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result updatePassword(@RequestBody Map<String, String> params) {
        Integer userId = BaseContext.getCurrentId();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    @ResponseBody
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            Integer userId = BaseContext.getCurrentId();
            String avatarUrl = userService.uploadAvatar(userId, file);
            return Result.success(avatarUrl);
        } catch (RuntimeException e) {
            log.error("上传头像失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
