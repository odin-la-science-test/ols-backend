package com.odinlascience.backend.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {}

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {}

    @Around("controllerPointcut()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();
        
        if (log.isDebugEnabled()) {
            log.debug("→ {} with args: {}", methodName, Arrays.toString(joinPoint.getArgs()));
        }
        
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        
        if (duration > 1000) {
            log.warn("← {} completed in {}ms (SLOW)", methodName, duration);
        } else if (log.isDebugEnabled()) {
            log.debug("← {} completed in {}ms", methodName, duration);
        }
        
        return result;
    }

    @Around("servicePointcut()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();
        
        if (log.isTraceEnabled()) {
            log.trace("→ {}", methodName);
        }
        
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        
        if (duration > 500) {
            log.warn("← {} took {}ms (SLOW)", methodName, duration);
        } else if (log.isTraceEnabled()) {
            log.trace("← {} completed in {}ms", methodName, duration);
        }
        
        return result;
    }

    @AfterThrowing(pointcut = "controllerPointcut() || servicePointcut()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        log.error("Exception in {}.{}(): {}", 
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            ex.getMessage());
    }
}
