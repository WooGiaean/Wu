package com.wjy.personal_blog.service.auth;

import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.service.custom.CustomerUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
        log.info("用户登录验证:{},{}", user.getUserName(),user.getUserRole());
        // 构建 UserDetails 对象
        return new CustomerUserDetails(user,
                Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole())));
    }
}