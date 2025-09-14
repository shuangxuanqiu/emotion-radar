package cn.chat.ggy.chataiagent.config;

import cn.chat.ggy.chataiagent.interceptor.TTLContextInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 注册TTL上下文拦截器
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TTLContextInterceptor ttlContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ttlContextInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                    "/favicon.ico",
                    "/error",
                    "/actuator/**"  // 排除健康检查等端点
                );
    }
}
