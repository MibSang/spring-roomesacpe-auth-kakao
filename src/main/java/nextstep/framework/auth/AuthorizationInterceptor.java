package nextstep.framework.auth;

import nextstep.framework.auth.jwt.JwtTokenProvider;
import nextstep.framework.auth.util.AuthorizationExtractor;
import nextstep.framework.exception.AuthErrorCode;
import nextstep.framework.exception.BusinessException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorizationInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthorizationInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Authorization authorization = ((HandlerMethod) handler).getMethodAnnotation(Authorization.class);
        if (authorization == null) {
            return true;
        }

        String token = AuthorizationExtractor.extractTokenFromRequest();
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }

        AuthorizationLevel apiLevel = authorization.level();
        AuthorizationLevel memberLevel = jwtTokenProvider.getAuthorization(token);
        if (memberLevel.canAccessTo(apiLevel)) {
            return true;
        }
        throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
    }
}
