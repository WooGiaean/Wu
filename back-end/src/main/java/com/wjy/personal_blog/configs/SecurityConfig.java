package com.wjy.personal_blog.configs;

import com.wjy.personal_blog.interceptor.JwtAuthenticationFilter;
import com.wjy.personal_blog.service.impl.AuthorizeService;
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
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    /*
    * 密码加密
    * */
    @Bean
    public PasswordEncoder MypasswordEncoder() {
        return new PasswordEncoder() {
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
                .loginProcessingUrl("/api/login") //后端登录验证接口
                .successHandler((request, response, authentication) -> {
                    // 返回登录成功的JSON响应
                    response.setContentType("application/json");
                    response.getWriter().write("{\"code\": 1, \"message\": \"登录成功\"}");
                })
                .failureHandler((request, response, exception) -> {
                    // 返回登录失败的JSON响应
                    response.setContentType("application/json");
                    response.getWriter().write("{\"code\": 0, \"message\": \"登录失败: " + exception.getMessage() + "\"}");
                })
                .permitAll();
        }).logout(logout -> {
            logout.logoutUrl("/api/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    // 返回退出成功的JSON响应
                    response.setContentType("application/json");
                    response.getWriter().write("{\"code\": 1, \"message\": \"退出成功\"}");
                })
                .permitAll();
        }).csrf(csf -> {
            csf.disable();
        }).authorizeHttpRequests(authz -> {
            authz.requestMatchers("/api/v1/auth/**", "/api/login", "/api/logout", "/api/register").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("admin")
                .anyRequest().permitAll();
        });

        // 添加JWT认证过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //允许本地开发前端访问
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173"
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