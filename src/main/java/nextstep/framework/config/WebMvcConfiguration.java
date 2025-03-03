package nextstep.framework.config;

import nextstep.framework.auth.AuthorizationInterceptor;
import nextstep.framework.auth.jwt.JwtTokenProvider;
import nextstep.framework.auth.principal.AuthenticationPrincipalArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public WebMvcConfiguration(AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver,
                               JwtTokenProvider jwtTokenProvider) {
        this.authenticationPrincipalArgumentResolver = authenticationPrincipalArgumentResolver;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index.html");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationPrincipalArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationInterceptor(jwtTokenProvider))
                .addPathPatterns("/**");
    }
}
