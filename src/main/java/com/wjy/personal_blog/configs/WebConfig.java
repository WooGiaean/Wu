package com.wjy.personal_blog.configs;

import com.wjy.personal_blog.interceptor.AdminInterceptor;
import com.wjy.personal_blog.interceptor.LoginInterceptor;
import com.wjy.personal_blog.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.util.List;

@Configuration
//@EnableWebMvc
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminInterceptor adminInterceptor;


    @Autowired
    private LoginInterceptor loginInterceptor;
    /*
    * 配置静态资源位置
    * */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");


        registry.addResourceHandler("/uploaded-images/**")
                .addResourceLocations("file:///D:/Self_Directory/Pictures/");

            }

    /*
    * 注册拦截器
    * */

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册登录拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/Home/**","/Article/**","/Note/**"
                        ,"/home/**","/articles/**","/notes/**")
                .excludePathPatterns("/admin/**");

        //注册管理员拦截器
        registry.addInterceptor(adminInterceptor)
                //拦截器拦截的路径和页面
                .addPathPatterns("/admin/**")
                //拦截器放行的路径和页面
                .excludePathPatterns("/admin/loginCheck",   //登录接口
                        "/admin/register",  //注册接口
                        "/admin/logout", //退出登录接口
                        "/Admin/login.html",    //登录页面
                        "/Admin/register.html");    //注册页面


        /*
        *
                        ,
        * */

    }//"/admin/"



    /*
            * 配置json数据格式的转换
            * */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //创建对象转换器，将Java
        converter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到converters中
        //参数0表示优先级最高，参数converters.size()表示最后。为了优先处理自定义的对象转换器，将优先级设为0
        converters.add(0,converter);
    }
}
