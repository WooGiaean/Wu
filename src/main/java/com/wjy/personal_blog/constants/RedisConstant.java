package com.wjy.personal_blog.constants;


/*
* 用于存储到redis中的常量值
*
*
* */
public class RedisConstant {

    //定义键的常量值：文章列表
    public static final String RECENT_ARTICLES_KEY = "blog:recent_articles";

    //笔记列表
    public static final String RECENT_NOTES_KEY = "blog:recent_notes";

    //定义过期时间
    public static final long CACHE_EXPIRED_SECONDS=3600;

    //用于测试的过期时间 5 min
    public static final long TEST_EXPIRED_SECONDS=300;


    //
}
