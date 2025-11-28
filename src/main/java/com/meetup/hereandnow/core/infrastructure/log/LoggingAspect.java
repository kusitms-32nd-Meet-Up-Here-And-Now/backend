package com.meetup.hereandnow.core.infrastructure.log;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Aspect
@Log4j2
@Component
@Profile("!local")
public class LoggingAspect {

    @Pointcut("execution(* com.meetup.hereandnow..presentation.controller..*(..))")
    public void controller() {}

    @Around("controller()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
        long startTime = System.currentTimeMillis();

        String controllerName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String decodedUri = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("[SUCCESS] [{}] {} [Class :{}.{}] time: {}ms",
                    request.getMethod(), decodedUri, controllerName, methodName, endTime - startTime);

            return result;
        } catch(Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("[FAIL] [{}] {} [Class :{}.{}] time: {}ms",
                    request.getMethod(), decodedUri, controllerName, methodName, endTime - startTime);

            throw e;
        }

    }
}
