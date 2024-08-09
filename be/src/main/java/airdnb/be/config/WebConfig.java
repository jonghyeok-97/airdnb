package airdnb.be.config;

import airdnb.be.interceptor.LoggingInterceptor;
import airdnb.be.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/error"); // ExceptionResolver 가 처리하지 못한 BasicErrorController 의 /error 요청시 인터셉터를 호출 X

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/member", "/member/exist", "/member/email/authenticate", "member/login");
    }
}
