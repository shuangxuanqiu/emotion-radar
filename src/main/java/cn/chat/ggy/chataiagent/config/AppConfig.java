package cn.chat.ggy.chataiagent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用配置类
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    /**
     * 应用基础URL
     */
    private String baseUrl;
}
