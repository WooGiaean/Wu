package com.wjy.personal_blog.aspect;

import com.wjy.personal_blog.annotation.autoCheck;
import com.wjy.personal_blog.exceptions.BusinessException;
import com.wjy.personal_blog.service.custom.CustomerUserDetails;
import com.wjy.personal_blog.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoginCheckAspect {

    /**
     * 定义切点：拦截所有带有 @autoCheck 注解的方法
     */
    @Pointcut("@annotation(com.wjy.personal_blog.annotation.autoCheck)")
    public void loginCheckPointcut() {
    }


    @Around("loginCheckPointcut()")
    public Object checkLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        // 从 SecurityUtil 中获取当前登录用户的 ID
        Integer currentUserId = SecurityUtil.getCurrentUserId();

        // 检查用户是否登录
        if (currentUserId == null) {
            log.warn("用户未登录，尝试访问需要登录的方法：{}" ,
                    joinPoint.getSignature().getName());
            throw new BusinessException("请先登录");
        }

        // 记录日志
        log.debug("用户 {} 调用方法：{}", currentUserId, joinPoint.getSignature().getName());

        return joinPoint.proceed();
      /*  MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        autoCheck annotation = signature.getMethod().getAnnotation(autoCheck.class);

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        // 1. 日志打印（如果启用）
        if (annotation.logEnabled()) {
            log.info("========== 方法开始：{}.{} ==========", className, methodName);
            if (!annotation.description().isEmpty()) {
                log.info("描述：{}", annotation.description());
            }
        }

        // 2. 用户认证（如果需要）
        if (annotation.requireLogin()) {
            CustomerUserDetails currentUser = SecurityUtil.getCurrentUser();
            if (currentUser == null) {
                log.warn("用户未登录，访问方法：{}.{}", className, methodName);
                throw new BusinessException("请先登录");
            }
            if (annotation.logEnabled()) {
                log.info("当前用户：ID={}, 用户名={}",
                        currentUser.getUserId(), currentUser.getUsername());
            }
        }

        // 3. 执行方法
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            log.error("方法异常：{}.{}，错误：{}", className, methodName, e.getMessage());
            throw e;
        } finally {
            // 4. 打印结束日志
            if (annotation.logEnabled()) {
                long executionTime = System.currentTimeMillis() - startTime;
                log.info("========== 方法结束：{}.{}，耗时：{}ms ==========",
                        className, methodName, executionTime);
            }
        }*/
    }
}
