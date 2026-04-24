package com.example.bookstore.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(public * com.example.bookstore.controller..*(..))")
    public void apiLoggers(){}

    @Before("apiLoggers()")
    public void logBefore(JoinPoint jp){
        String requestMethod;
        String requestUri;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //Ensure API is populated in context
        if(attributes != null){
            HttpServletRequest req = attributes.getRequest();
            requestMethod = req.getMethod();
            requestUri = req.getRequestURI();
            log.info("Calling {} | {} ", requestMethod, requestUri);
        }
    }

    @Around("apiLoggers()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        //Execution time
        long start = System.currentTimeMillis(); //Start timer

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        log.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);
        return proceed;
    }

    //Log the HTTP response status after a successful return from controller
    @AfterReturning(pointcut = "apiLoggers()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletResponse resp = attributes.getResponse();
            int status = resp != null ? resp.getStatus() : -1;
            log.info("{} completed with status {}", joinPoint.getSignature(), status);
        } else {
            // Fallback when not in an HTTP request context
            log.error("{} completed (no HTTP context available)", joinPoint.getSignature());
        }
    }
}
