package com.wjy.personal_blog.mapper;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.dto.UserStatisticsDTO;
import com.wjy.personal_blog.pojo.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    /* 登录验证 */
    @Select("select * from user where user_name=#{username} and user_password=#{password}")
    User loginCheck(String username, String password);


    /* 查询所有用户 */
    @Select("select * from user")
    Page<User> getAllUsers();

    /* 查询某个用户 */
    //@Select("select * from user where user_name=#{userName} and user_password=#{userPassword}")
    Page<User> findSingleUser(User user);


    /*
    * 通过id获取用户
    * */
    @Select("select * from user where user_id=#{userId}")
    User getUserById(Integer userId);


    /*
    * 验证码登录：通过邮箱查询
    * */
    @Select("select * from user where user_email=#{email}")
    User getUserByEmail(String email);
    
    /*
    * 根据用户名查询用户
    * */
    @Select("select * from user where user_name=#{userName}")
    User findByUsername(String username);

    /* 注册新用户 */
    @Insert("insert into user(user_name, user_password, user_nickname, user_email, user_url, user_avatar, " +
            "user_last_login_ip, user_register_time, user_last_login_time, user_status, user_role)" +
            " values(#{userName},#{userPassword},#{userNickname},#{userEmail},#{userUrl},#{userAvatar}," +
            "#{userLastLoginIp},#{userRegisterTime},#{userLastLoginTime},#{userStatus},#{userRole})")
    void insertNewUser(User userIfExist);

    /* 删除用户
    *
    *  需要联合文章、笔记进行对应删除
    *  */
    @Delete("delete from user where user_id=#{id}")
    void deleteUser(Integer id);

    /*
    * 编辑/更新用户信息
    * */
    void updateUser(User user);
    /*
    * 批量删除用户
     */
    //@Delete("delete from user where user_id in (#{userIds})")
    void deleteUserBatch(List<Integer> userIds);

    /*
    * 通过邮箱查询用户
    * */
    @Select("select * from user where user_email=#{email}")
    User findUserByEmail(String email);

    @Update("update user set user_password=#{encode} where user_email=#{email}")
    void resetPasswordByEmail(String email, String encode);

    @Select("select count(*) from user")
    Integer countAllUsers();



   /* 一次性统计用户文章数、笔记数、评论数

   @Select("select (select count(*) from article where article_user_id=#{userId}) as articleCount," +
            "(select count(*) from notes where note_user_id=#{userId}) as articleCount," +
            "(select count(*) from comment where user_id=#{userId}) as articleCount")
    UserStatisticsDTO getStatistics(Integer userId);*/
}
