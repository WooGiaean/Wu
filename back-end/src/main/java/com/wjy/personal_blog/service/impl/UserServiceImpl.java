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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    @Autowired
    private PasswordEncoder passwordEncoder;


    private static final String UPLOAD_PATH = "classpath:/static/img";

    //在新增文章时添加图片，图片的上传路径
    private static final String UPLOAD_IMG_PATH = "src/main/resources/static/touxiang/";

    /*
     * 登录验证
     * */
    @Override
    public User loginVerify(LoginDTO loginDTO) {
        User userIfExist = userMapper.loginCheck(loginDTO.getUsername(), loginDTO.getPassword());
        return userIfExist != null ? userIfExist : null;
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
        PageHelper.startPage(page, pageSize);

        //获取所有用户
        Page<User> allUsers = userMapper.getAllUsers();

        //获取所有用户id
        List<Integer> allUserIds = allUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        //获取用户对应的文章数
        Map<Integer, Map<String, Long>> articles = articleMapper.countByArticleUserIdMap(allUserIds);
        Map<Integer, Integer> articleMap = mapConvert(articles, "articleCount");

        //获取用户对应的笔记数
        Map<Integer, Map<String, Long>> notes = noteMapper.countNoteByUserId(allUserIds);
        Map<Integer, Integer> noteMap = mapConvert(notes, "noteCount");

        //设置用户文章数和笔记数
        for (User user : allUsers) {
            user.setArticleCount(articleMap.getOrDefault(user.getUserId(), 0));
            user.setNoteCount(noteMap.getOrDefault(user.getUserId(), 0));
        }

        PageResult pageResult = new PageResult(allUsers.getTotal(), allUsers.getResult());
        return pageResult;
    }

    /*
     * map格式转换的方法
     * */
    private Map<Integer, Integer> mapConvert(Map<Integer, Map<String, Long>> map, String str) {
        Map<Integer, Integer> tempMap = new HashMap<>();
        for (Map.Entry<Integer, Map<String, Long>> entry : map.entrySet()) {
            tempMap.put(entry.getKey(), entry.getValue().get(str).intValue());
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
        if (userDTO == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        List<User> singleUser = userMapper.findSingleUser(user);
        return singleUser;
    }

    /*
     * 插入新用户（新用户注册：默认角色为user）
     * */
    @Override
    public void insertNewUser(User user) {

        //初始化数据
        user.setUserRegisterTime(LocalDateTime.now());
        user.setUserLastLoginTime(LocalDateTime.now());
        user.setUserStatus(1); //默认用户状态是：正常
        user.setUserUrl("");
        user.setUserAvatar(null);
        user.setUserLastLoginIp(null);
        //默认用户角色是：user
        user.setUserRole(User.USER_ROLE_USER);
        userMapper.insertNewUser(user);
    }


    /*
     * 更新用户
     * */
    @Override
    public void updateUser(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            log.info("用户不存在");
            return;
        }
        User userIfExist = userMapper.getUserById(userDTO.getUserId());
        if (userIfExist == null) {
            log.info("用户不存在");
            return;
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userMapper.updateUser(user);
    }

    /**
     * 删除用户
     */
    @Override
    public void deleteUser(Integer id) {
        User userById = userMapper.getUserById(id);
        if (userById == null) {
            log.info("用户不存在");
            return;
        }
        log.info("删除id为{}的用户", id);
        userMapper.deleteUser(id);
        log.info("删除用户成功");
    }

    /*
     * 关键字/词查找用户
     * */
    @Override
    public PageResult findSpecificUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        Page<User> specificUser = userMapper.findSingleUser(user);
        PageResult pageResult = new PageResult(specificUser.getTotal(), specificUser.getResult());
        return pageResult;
    }

    /*
     * 根据用户名查询用户
     * */
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /*
     * 批量删除用户
     * */
    @Override
    public void batchDeleteUsers(Integer[] userIds) {
        if (userIds == null || userIds.length == 0) {
            throw new RuntimeException("请选择要删除的用户");
        }
        userMapper.deleteUserBatch(userIds);
        log.info("删除了{}个用户", userIds.length);
    }

    /*
     * 添加用户（管理员功能:可设置新增用户的角色）
     * */
    @Override
    public void addUserByAdmin(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        // 初始化用户数据
        user.setUserRegisterTime(LocalDateTime.now());
        user.setUserLastLoginTime(LocalDateTime.now());
        user.setUserStatus(1);
        user.setUserUrl("");
        user.setUserAvatar(UPLOAD_IMG_PATH+"blog_avatar-3.png");
        user.setUserLastLoginIp(null);
        user.setUserNickname(user.getUserName());
        // 密码加密
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        userMapper.insertNewUser(user);
    }


    /*
    * 修改密码
    * */
    @Override
    public void updatePassword(Integer userId, String oldPassword, String newPassword) {
        User user2update = userMapper.getUserById(userId);
        if (user2update == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user2update.getUserPassword().equals(oldPassword)) {
            throw new RuntimeException("旧密码错误,请重新输入");
        }
        //更新密码
        user2update.setUserPassword(passwordEncoder.encode(newPassword));
        userMapper.updateUser(user2update);
        log.info("用户id为{}的密码更新成功", userId);
    }

    /*
    * 上传头像
    * */
    @Override
    public String uploadAvatar(Integer userId, MultipartFile file) {
        //判断上传的文件是否为空
        if(file==null||file.isEmpty()){
            throw new RuntimeException("请选择文件，头像不能为空");
        }
        User user = userMapper.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        //获取文件初始名字
        String originalFilename = file.getOriginalFilename();
        //获取后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //随机生成图片文件名称
        String imgName = UUID.randomUUID().toString() + suffix;

        //创建文件目录
        File dir = new File(UPLOAD_IMG_PATH);
        if (!dir.exists()) {
            dir.mkdirs();    //递归创建
        }

        //进行文件上传
        try {
            File dest = new File(UPLOAD_IMG_PATH + imgName);
            file.transferTo(dest);
            log.info("上传成功：{}", imgName);
            
            //更新用户头像路径
            user.setUserAvatar(imgName);
            userMapper.updateUser(user);
            return imgName;
        } catch (IOException e) {
           e.printStackTrace();
           throw new RuntimeException("上传失败" + e.getMessage());
        }
    }

}
