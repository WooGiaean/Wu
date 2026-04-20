package com.wjy.personal_blog.constants;


/*
* 用于存储到redis中的常量值
*
*
* */
public class RedisConstant {
    //定义当前登录用户的key
    public static final String CURRENT_USER_KEY = "current:user:";

  /*  // 用户信息存储键前缀
    public static final String USER_INFO_PREFIX = "user:info:";*/

    // Token跟用户信息的映射存储键前缀
    public static final String USER_TOKEN_PREFIX = "user:token:";

    // Token黑名单键前缀
    public static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";



    //定义键的常量值：最新文章列表
    public static final String RECENT_ARTICLES_KEY = "blog:recent_articles:";

    //定义键的常量值：最新笔记列表
    public static final String RECENT_NOTES_KEY = "blog:recent_notes:";


    //定义键的常量值：用户文章数量
    public static final String USER_ARTICLE_COUNT_KEY = "user:article:count:";

    //定义键的常量值：用户笔记数量
    public static final String USER_NOTE_COUNT_KEY = "user:note:count:";

    //热门数据：博文、笔记过期时间 10 min
    public static final long HOT_DATA_EXPIRED_MINUTES=10;


    //验证码存储redis中的时间：5 min
    public static final long CACHE_EXPIRED_SECONDS=300;


    //存放登录验证码的key
    public static final String VERIFY_CODE_KEY="verify_code:";


    //邮箱发送频率限制
    public static final String EMAIL_SEND_RATE_LIMIT="email_send_rate_limit:";
}
