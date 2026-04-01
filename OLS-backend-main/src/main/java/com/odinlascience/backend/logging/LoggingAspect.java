package com.odinlascience.backend.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
public class LoggingAspect {

    private static final Set<String> SENSITIVE_METHODS = Set.of(
            "login", "register", "resetPassword", "forgotPassword",
            "requestPasswordReset", "refreshToken"
    );

    private static final Set<String> SENSITIVE_PARAMS = Set.of(
            "password", "newPassword", "token", "refreshToken", "accessToken"
    );

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {}

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {}

    @Around("controllerPointcut()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();

        if (log.isDebugEnabled()) {
            if (SENSITIVE_METHODS.contains(methodName)) {
                log.debug("→ {} with args: [REDACTED]", methodName);
            } else {
                log.debug("→ {} with args: {}", methodName, sanitizeArgs(joinPoint));
            }
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

    private String sanitizeArgs(ProceedingJoinPoint joinPoint) {
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            String name = (paramNames != null && i < paramNames.length) ? paramNames[i] : "arg" + i;
            if (SENSITIVE_PARAMS.contains(name)) {
                sb.append(name).append("=***");
            } else {
                sb.append(name).append("=").append(args[i]);
            }
        }

        return sb.append("]").toString();
    }
}
