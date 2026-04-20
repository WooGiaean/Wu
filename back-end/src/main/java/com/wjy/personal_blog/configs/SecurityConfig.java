package com.wjy.personal_blog.configs;


import com.wjy.personal_blog.filter.JwtAuthenticationFilter;
import com.wjy.personal_blog.handler.CustomFailureHandler;
import com.wjy.personal_blog.handler.CustomLogoutSuccessHandler;
import com.wjy.personal_blog.handler.CustomSuccessHandler;
import com.wjy.personal_blog.service.auth.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthorizeService authorizeService;


    @Autowired
    private CustomSuccessHandler customSuccessHandler;

    @Autowired
    private CustomFailureHandler customFailureHandler;


    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    /*
    * 密码加密
    * */
    @Bean
    public PasswordEncoder MypasswordEncoder() {
        return new PasswordEncoder() {

            // 使用BCryptPasswordEncoder进行加密
            private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            @Override
            public String encode(CharSequence rawPassword) {
                //新密码进行加密
                return bCryptPasswordEncoder.encode(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                // 检查密码是否已经加密（BCrypt加密的密码以$2a$开头）
                if (encodedPassword.startsWith("$2a$")) {
                    // 对已加密的密码使用BCrypt验证
                    return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
                } else {
                    // 对明文密码直接比较
                    return rawPassword.equals(encodedPassword);
                }

            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(authorizeService).passwordEncoder(MypasswordEncoder());
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors-> cors
                        .configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> {
            // JWT是无状态的，设置会话创建策略为无状态
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }).formLogin(form -> {
            form
                .loginProcessingUrl("/api/login") //前端页面表单请求登录验证的路径
                .successHandler(customSuccessHandler)
                .failureHandler(customFailureHandler)
                    .permitAll();
        }).logout(logout -> {
            logout.logoutUrl("/api/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .permitAll();
        }).csrf(csf -> {    // 关闭CSRF保护
            csf.disable();
        }).authorizeHttpRequests(authz -> {
            // 公开接口
            authz.requestMatchers("/api/auth/**", "/api/articles/**",
                            "/api/comments/article/**", "/api/category/**",
                            "/api/tags/list","/uploaded-images/**").permitAll()
                    // 接口文档无需认证权限
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                            "/doc.html", "/webjars/**", "/swagger-resources/**",
                            "/swagger-config/**", "/swagger-ui.html", "/favicon.ico",
                            "/knife4j/**").permitAll()
                // 用户权限接口
                .requestMatchers("/api/user/**","/api/home/**").authenticated()
                // 管理员权限接口
                .requestMatchers("/api/admin/**", "/api/manage/**").hasAuthority("admin")
                // 其他所有请求需要认证
                .anyRequest().authenticated();
        });

        // 添加JWT认证过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 配置CORS跨域
     * */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //允许本地开发前端访问
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174"
        ));
        //允许所有请求方法
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        //允许所有请求头
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        //允许携带凭证cookie
        configuration.setAllowCredentials(true);
        //应用到所有路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // 配置防火墙，允许双斜杠
    @Bean
    public HttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);
        return firewall;
    }
}