package com.wjy.personal_blog.mapper;

import com.wjy.personal_blog.pojo.dto.LoginDTO;
import com.wjy.personal_blog.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    /* 登录验证 */
    @Select("select * from user where user_name=#{username} and user_password=#{password}")
    User loginCheck(String username, String password);


    /* 查询所有用户 */
    @Select("select * from user")
    List<User> getAllUsers();

    /* 查询某个用户 */
    //@Select("select * from user where user_name=#{userName} and user_password=#{userPassword}")
    List<User> findSingleUser(User user);


    @Select("select * from user where user_name=#{userName}")
    User findUserByName(String userName);


    @Insert("insert into user values(#{userName},#{userPassword},#{userNickname},#{userEmail},#{userUrl},#{userAvatar}," +
            "#{userLastLoginIp},#{userRegisterTime},#{userLastLoginTime},#{userStatus},#{userRole})")
    void insertNewUser(User userIfExist);
}
