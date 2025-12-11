package com.wjy.personal_blog.controllers.admin;

import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.pojo.dto.LoginDTO;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.http.HttpRequest;
import java.util.List;


@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private UserService userService;

    /**
    * 默认访问路径是登录界面
    * */
    @GetMapping("/")
    public String loginPage(){
        return "forward:/Admin/login.html";
    }

    /**
     * 跳转到主页面
     */
    /*@GetMapping("/index")
    public String indexPage(){
        return "Admin/index";
    }*/


    /**
     *
     * 注册页面
     * */
   /* @RequestMapping("/register")
    public String registerPage(){
        return "Admin/register";
    }*/


    /**
     * 登录验证
     */

    @PostMapping("/loginCheck")
    @ResponseBody
    public Result loginCheck(@RequestBody LoginDTO loginDTO,HttpSession session){
        log.info("用户登录验证:{}",loginDTO.getUsername()+":"+loginDTO.getPassword());
        if(loginDTO==null){
            return Result.error("用户名或密码为空");
        }
        User userLogin = userService.loginVerify(loginDTO);
        if(userLogin==null){
            return Result.error("用户名或密码错误");
        }
        //设置session对象方便后续验证
        log.info("登录用户：{}",userLogin);
        //存入用户session的id，作为唯一标识！！
        session.setAttribute("user",userLogin);
        //将当前登录用户的id存到ThreadLocal中
        BaseContext.setCurrentId(userLogin.getUserId());
        log.info("当前用户的JSESSIONID：{}",userLogin.getUserId());
        return Result.success(userLogin);
    }


    /*
    * 特殊账户
    * */
    @RequestMapping("/tempLogin")
    public String tempLogin(){
        return "redirect:/Home/pic.html";
    }


    /**
     * 注册验证
     */

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestBody UserDTO userDTO){
        log.info("用户注册验证：{}",userDTO);
        //查询前端传递的用户数据是否已经存在数据库中
        User user=new User();
        BeanUtils.copyProperties(userDTO,user);
        userService.insertNewUser(user);
        log.info("用户注册成功：{}",userDTO);
        return Result.success(userDTO);
    }


    /**
    * 用户简介页面显示
    * */
    /*@GetMapping("/profile")
    public ModelAndView userProfileView(){
        ModelAndView modelAndView = new ModelAndView("Home/profile");
        modelAndView.addObject("info","个人资料");
        return modelAndView;
    }*/

    /**
     * 用户资料提交
     */
   /* @PostMapping("/save")
    public String saveProfile(){
        return "redirect:/admin/profile";
    }*/


    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @ResponseBody
    public Result<String> logoutPage(HttpSession session){
        log.info("用户退出登录");
        session.invalidate();   //让session失效
        BaseContext.removeCurrentId();
        return Result.success("成功退出登录");
    }


}
