package team9.ddang.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import team9.ddang.global.exception.AuthenticationException;
import team9.ddang.member.jwt.service.JwtService;

import static team9.ddang.walk.exception.WalkExceptionMessage.*;

@Component
@Aspect
public class JwtAuthenticationAspect {

    private final JwtService jwtService;

    public JwtAuthenticationAspect(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Around("@annotation(ExtractEmail)")
    public Object authenticateToken(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        SimpMessageHeaderAccessor headerAccessor = findHeaderAccessor(args);
        if (headerAccessor == null) {
            throw new IllegalArgumentException("SimpMessageHeaderAccessor not found in method arguments.");
        }

        String token = jwtService.extractAccessToken(headerAccessor)
                .orElseThrow(() -> new AuthenticationException(TOKEN_NOT_FOUND));
        String email = jwtService.extractEmail(token)
                .orElseThrow(() -> new AuthenticationException(TOKEN_DO_NOT_EXTRACT_EMAIL));

        // 이메일을 메서드 파라미터에 추가하거나, ThreadLocal 등을 사용하여 전달
        AuthenticationContext.setEmail(email);

        try {
            return joinPoint.proceed();
        } finally {
            AuthenticationContext.clear(); // ThreadLocal 정리
        }
    }

    private SimpMessageHeaderAccessor findHeaderAccessor(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof SimpMessageHeaderAccessor) {
                return (SimpMessageHeaderAccessor) arg;
            }
        }
        return null;
    }
}
