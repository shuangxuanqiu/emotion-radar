package cn.chat.ggy.chataiagent.config;

import cn.chat.ggy.chataiagent.interceptor.ContextInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 注册上下文拦截器，在JDK21虚拟线程环境下自动管理ThreadLocal上下文
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ContextInterceptor contextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(contextInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                    "/favicon.ico",
                    "/error",
                    "/actuator/**"  // 排除健康检查等端点
                );
    }
}
