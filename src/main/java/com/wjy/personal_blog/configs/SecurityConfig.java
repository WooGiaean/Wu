/*
package com.wjy.personal_blog.configs;

import com.wjy.personal_blog.service.impl.AuthorizeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  */
/*  @Bean
    public UserDetailsService userDetailsService(){


        基于内存验证：直接在后台以代码的格式进行验证

        UserDetails wuJY = User
                .withDefaultPasswordEncoder()
                .username("WuJY")
                .password("123321")
                .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("123321")
                .build();

        return new InMemoryUserDetailsManager(wuJY,admin);

    }*//*


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthorizeService authorizeService() {
        return new AuthorizeService();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz   //配置http授权，使用lambda表达式
                        .requestMatchers("/Admin/login.html", "/admin/loginCheck","/admin/register")
                        .permitAll()    //允许所有用户访问 requestMatchers()方法指定路径,
                                        // permitAll()方法指定允许所有用户访问  两者搭配使用
                        .requestMatchers("/admin/**").hasRole("admin")
                         //配置用户访问权限，指令路径需要登录认证后才能访问
                        .anyRequest().authenticated()                  //其他所有未特别指定的请求都允许匿名访问
                ).formLogin(form -> {
                    form.loginPage("/Admin/login.html") //自定义登录页面，指定登录页面
                            .loginProcessingUrl("/admin/loginCheck")   //自定义登录处理路径，指定登录验证接口
                            .defaultSuccessUrl("/Home/home.html",true)
                            .failureUrl("/Admin/login.html?error=true");//登录成功跳转路径
                }).logout(logout -> {
                    logout.logoutUrl("/admin/logout")
                            .logoutSuccessUrl("/Admin/login.html?logout=true")
                            .permitAll();
                })
                .csrf(csf -> {
                    csf.disable();  //让csf验证失效
                    //csf.ignoringRequestMatchers("/xxx/**");
                });

        return http.build();
    }
}
*/
