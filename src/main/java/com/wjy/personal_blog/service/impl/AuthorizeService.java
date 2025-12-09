/*
package com.wjy.personal_blog.service.impl;

import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AuthorizeService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("用户登录验证:{}",username);
        User userByName = userMapper.findUserByName(username);
        if (userByName == null){
            log.warn("用户不存在:{}"+username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(userByName.getUserName())
                .password(userByName.getUserPassword())
                .roles(userByName.getUserRole())
                .build();
    }
}
*/
