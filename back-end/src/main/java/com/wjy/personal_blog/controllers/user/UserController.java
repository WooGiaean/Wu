package com.wjy.personal_blog.controllers.user;

import com.wjy.personal_blog.constants.RedisConstant;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.dto.VerifyCodeDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.UserService;
import com.wjy.personal_blog.service.email.EmailService;
import com.wjy.personal_blog.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "用户模块", description = "用户个人中心相关接口")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private EmailService emailService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    /*
    * 查询用户资料（获取当前用户信息）
    * */

    @GetMapping("/profile")
    @Operation(summary = "查询用户资料", description = "获取当前登录用户的个人资料")
    public Result<User> getUserProfile(){
        Integer userId = SecurityUtil.getCurrentUserId();
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
    @Operation(summary = "更新个人资料", description = "更新当前登录用户的个人资料")
    public Result updateProfile(@RequestBody UserDTO userDTO) {
        Integer userId = SecurityUtil.getCurrentUserId();
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
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    public Result updatePassword(@RequestBody Map<String, String> params) {
        //获取登录用户的id
        Integer userId = SecurityUtil.getCurrentUserId();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        if (newPassword == null || newPassword.length() < 6) {
            return Result.error("新密码不能为空且至少6位");
        }
        // 加密新密码
        String encryptedPassword = passwordEncoder.encode(newPassword);
        userService.updatePassword(userId, oldPassword, encryptedPassword);
        return Result.success();
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传头像", description = "上传当前登录用户的头像")
   public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            Integer userId = SecurityUtil.getCurrentUserId();
            String avatarUrl = userService.uploadAvatar(userId, file);
            return Result.success(avatarUrl);
        } catch (RuntimeException e) {
            log.error("上传头像失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }


}
