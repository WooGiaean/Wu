package com.wjy.personal_blog.utils;

import org.apache.commons.lang3.RandomStringUtils;

/*
* 生成验证码
* */
public class GenerateVerifyCode {


    /*
    * 4位数字验证码
    * */
    public static String generateCode4(){
        return RandomStringUtils.randomNumeric(4);
    }

    /*
    * 6位数字验证码
    * */
    public static String generateCode6(){
        return RandomStringUtils.randomNumeric(6);
    }
}
