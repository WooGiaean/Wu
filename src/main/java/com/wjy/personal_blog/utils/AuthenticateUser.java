package com.wjy.personal_blog.utils;

import com.wjy.personal_blog.context.BaseContext;

/*
* 该方法用户验证用户
* */
public class AuthenticateUser {

    public boolean authenticateUser(Integer userId){
        Integer currentId = BaseContext.getCurrentId();
        if (currentId != userId) {
            return false;
        }
        return true;
    }
}
