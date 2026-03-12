package com.wjy.personal_blog.configs;

import com.wjy.personal_blog.service.impl.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthorizeService authorizeService;


    /*
    * 密码加密
    * */
    @Bean
    public PasswordEncoder MypasswordEncoder() {
        return  new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                // 直接比较明文密码
                String raw = rawPassword.toString();
                String encoded = encodedPassword;
                System.out.println("Raw password: " + raw);
                System.out.println("Encoded password: " + encoded);
                System.out.println("Password match: " + raw.equals(encoded));
                return raw.equals(encoded);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(authorizeService)
                .passwordEncoder(MypasswordEncoder());
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz   //配置http授权，使用lambda表达式
                        .requestMatchers("/Login/**","/admin/loginCheck","/admin/register"
                                )//"/doc.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**"
                        .permitAll()    //允许所有用户访问 requestMatchers()方法指定路径,
                                        // permitAll()方法指定允许所有用户访问  两者搭配使用
                        .requestMatchers("/admin/**", "/manage/**")
                        .hasAuthority("admin")//基于权限的授权，只有admin权限的用户才能访问
                         //配置用户访问权限，指令路径需要登录认证后才能访问
                        .requestMatchers("/Home/**", "/Article/**", "/Note/**", "/articles/**", "/notes/**")
                        .authenticated()
                        .anyRequest().permitAll()                  //其他所有未特别指定的请求都允许匿名访问
                ).formLogin(form -> {
                    form.loginPage("/Login/login.html") //自定义登录页面，指定登录页面
                            .loginProcessingUrl("/login")   //使用不同的路径，避免与我们的接口冲突(登录表单提交的位置)
                            .defaultSuccessUrl("/Home/home1.html",true)//登录成功跳转路径
                            .permitAll()
                            .failureUrl("/Login/login.html?error=true");
                })/*.rememberMe(rm -> {
                    rm.alwaysRemember(false)
                            .rememberMeParameter("remember")
                            .rememberMeCookieName("remember")
                            .tokenValiditySeconds(86400); //1天有效期
                })*/
                .logout(logout -> {
                    logout.logoutUrl("/admin/logout")
                            .logoutSuccessUrl("/Login/login.html?logout=true")
                            .permitAll();
                })
                .csrf(csf -> {
                    csf.disable();  //让csf验证失效
                    //csf.ignoringRequestMatchers("/xxx/**");
                });

        return http.build();
    }
}
