package com.wjy.personal_blog.configs;

import com.wjy.personal_blog.interceptor.AdminInterceptor;
import com.wjy.personal_blog.interceptor.LoginInterceptor;
import com.wjy.personal_blog.json.JacksonObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.*;


import java.util.List;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    /*
    * 配置静态资源位置
    * */

    @Value("${blog.upload.path}")
    private String UPLOAD_PATH;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置图片上传路径
        registry.addResourceHandler("/uploaded-images/**")
                .addResourceLocations("file:"+UPLOAD_PATH);

        // 优先配置Swagger和Knife4j静态资源
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        /*registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/v3/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/");*/
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/knife4j/**")
                .addResourceLocations("classpath:/META-INF/resources/knife4j/");



        //配置静态资源位置
        /*registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");*/
    }


    /*
    * 为RestController类添加前缀 /api
    * */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api",
                HandlerTypePredicate.forAnnotation(RestController.class));
    }



    /*
    * 注册拦截器
    * */

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Spring Security 已处理认证和授权，不再需要自定义拦截器
        // 保留此方法以保持配置结构完整
    }



    /**
     * 全局OpenAPI配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("个人博客系统API文档")
                        .version("1.0.0")
                        .description("个人博客系统的完整API文档，包含管理端和用户端接口")
                        .contact(new Contact().name("WuJY")));
    }

    /**
    * 后台管理端接口文档分组
    * */
    /*@Bean
    public GroupedOpenApi adminOpenAPI(){
        return GroupedOpenApi.builder()
                .group("后台管理端接口")
                .pathsToMatch("/admin/**","/manage/**")
                .build();
    }*/

    /**
     * 用户相关操作接口文档分组
     * */
    //@Bean
   /* public GroupedOpenApi userOpenAPI(){
        return GroupedOpenApi.builder()
                .group("用户相关操作接口")
                .pathsToMatch("/articles/**", "/notes/**", "/category/**", "/home/**")
                .build();
    }
*/
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
