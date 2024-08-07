package airdnb.be.config;

import airdnb.be.interceptor.LoggingInterceptor;
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
                /*
                 * ExceptionResolver 가 처리하지 못한 오류가 있을 때,
                 * 부트가 기본 제공하는 /error 요청에 대해 인터셉터를 미 호출
                 */
                .excludePathPatterns("/error");
    }
}
