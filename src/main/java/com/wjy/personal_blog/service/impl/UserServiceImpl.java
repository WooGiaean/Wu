package com.wjy.personal_blog.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.NoteMapper;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.dto.LoginDTO;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private ArticleMapper articleMapper;


    @Autowired
    private NoteMapper noteMapper;

    /*
    * 登录验证
    * */
    @Override
    public User loginVerify(LoginDTO loginDTO) {
        User userIfExist = userMapper.loginCheck(loginDTO.getUsername(), loginDTO.getPassword());
        return userIfExist!=null?userIfExist:null;
    }

    /*
    * 获取所有用户
    * */
    @Override
    public PageResult getAllUsers(PageQueryDTO pageQueryDTO) {
        log.info("获取所有用户");

        int page = pageQueryDTO.getPage() != null ? pageQueryDTO.getPage() : 1;
        int pageSize = pageQueryDTO.getPageSize() != null ? pageQueryDTO.getPageSize() : 10;

        //默认分页：一页10个
        PageHelper.startPage(page,pageSize);

        //获取所有用户
        Page<User> allUsers = userMapper.getAllUsers();

        //获取所有用户id
        List<Integer> allUserIds = allUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        //获取用户对应的文章数
        Map<Integer, Map<String, Long>> articles = articleMapper.countByArticleUserIdMap(allUserIds);
        Map<Integer, Integer> articleMap = mapConvert(articles,"articleCount");

        //获取用户对应的笔记数
        Map<Integer, Map<String, Long>> notes = noteMapper.countNoteByUserId(allUserIds);
        Map<Integer, Integer> noteMap = mapConvert(notes,"noteCount");

        //设置用户文章数和笔记数
        for (User user : allUsers) {
            user.setArticleCount(articleMap.getOrDefault(user.getUserId(),0));
            user.setNoteCount(noteMap.getOrDefault(user.getUserId(),0));
        }

        PageResult pageResult=new PageResult(allUsers.getTotal(),allUsers.getResult());
        return pageResult;
    }

    /*
    * map格式转换的方法
    * */
    private Map<Integer,Integer> mapConvert(Map<Integer, Map<String, Long>> map,String str){
        Map<Integer,Integer> tempMap=new HashMap<>();
        for(Map.Entry<Integer,Map<String,Long>> entry:map.entrySet()){
            tempMap.put(entry.getKey(),entry.getValue().get(str).intValue());
        }
        return tempMap;
    }



    /*
    * 根据id查找用户
    * */
    @Override
    public User getUserById(Integer id) {
        User userById = userMapper.getUserById(id);
        return userById;
    }


    /*
    * 通过用户名or邮箱查询用户
    * */
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
        //初始化数据
        user.setUserRegisterTime(LocalDateTime.now());
        user.setUserLastLoginTime(LocalDateTime.now());
        user.setUserStatus(1);
        user.setUserUrl("");
        user.setUserAvatar(null);
        user.setUserLastLoginIp("127.0.0.1");
        // TODO 暂时不确定提交表单的页面有无角色字段
        //默认用户角色是：user
        user.setUserRole(User.USER_ROLE_USER);
        userMapper.insertNewUser(user);
    }

    /*
    * 更新用户
    * */
    @Override
    public void updateUser(UserDTO userDTO) {
        if (userDTO==null||userDTO.getUserId()==null){
            log.info("用户不存在");
            return;
        }
        User userIfExist = userMapper.getUserById(userDTO.getUserId());
        if (userIfExist==null){
            log.info("用户不存在");
            return;
        }
        User user=new User();
        BeanUtils.copyProperties(userDTO,user);
        userMapper.updateUser(user);
    }

    /**
     * 删除用户
     * */
    @Override
    public void deleteUser(Integer id) {
        User userById = userMapper.getUserById(id);
        if (userById==null){
            log.info("用户不存在");
            return;
        }
        log.info("删除id为{}的用户",id);
        userMapper.deleteUser(id);
        log.info("删除用户成功");
    }

    /*
    * 关键字/词查找用户
    * */
    @Override
    public PageResult findSpecificUser(UserDTO userDTO) {
        User user=new User();
        BeanUtils.copyProperties(userDTO,user);
        Page<User> specificUser = userMapper.findSingleUser(user);
        PageResult pageResult=new PageResult(specificUser.getTotal(),specificUser.getResult());
        return pageResult;
    }

    /*
    * 根据用户名查询用户
    * */
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }



}
