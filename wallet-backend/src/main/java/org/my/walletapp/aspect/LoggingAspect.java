package org.my.walletapp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("(within(org.my.walletapp.controller..*) || within(org.my.walletapp.service..*)) " +
            "&& !within(org.my.walletapp.controller.AuthController) " +
            "&& !within(org.my.walletapp.service.auth..*) " +
            "&& !execution(* org.my.walletapp.controller.UserController.changeUserPasswordByJwt(..)) " +
            "&& !execution(* org.my.walletapp.service.user.UserServiceImpl.changeUserPassword(..))")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();

        log.debug("Enter: {}.{}() with argument[s] = {}", className, methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long elapsedTime = System.currentTimeMillis() - start;
            log.debug("Exit: {}.{}() with result = {}, Execution time: {} ms", className, methodName, result, elapsedTime);

            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(args), className, methodName);
            throw e;
        }
    }
}
