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
        // 根据用户名查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        log.info("用户登录验证:{}", username);
        log.info("用户密码:{}", user.getUserPassword());
        log.info("用户角色:{}", user.getUserRole());
        // 构建 UserDetails 对象
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getUserPassword())
                .authorities(user.getUserRole())
                .build();
    }
}