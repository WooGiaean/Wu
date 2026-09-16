package com.wjy.personal_blog.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogMsgAspect {
    /**
     * 定义日志切点
     * */
    @Pointcut("execution(* com.wjy.personal_blog.controllers.*.*(..))" +
            "||execution(* com.wjy.personal_blog.service.impl.*(..))")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object logMsgMethod (ProceedingJoinPoint joinPoint)throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("方法开始：{}.{}", className, methodName);
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        }catch (Throwable e){
            log.error("方法异常：{}.{}，错误：{}", className, methodName, e.getMessage());
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("方法结束：{}.{}，耗时：{}ms", className, methodName, executionTime);
        }
    }
}
