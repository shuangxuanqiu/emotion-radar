package cn.chat.ggy.chataiagent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 提示词的配置
 */
@Configuration
@Order(1)
public class PromptConfig {


    @Bean(name="promptChatCriterion")
    public String promptChatCriterion() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/chat-criterion.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load chat criterion prompt", e);
        }
    }
    @Bean(name="promptChatBsicsn")
    public String promptChatBsicsn() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/chat-basics.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load chat criterion prompt", e);
        }
    }

    @Bean(name="promptImageAnalysis")
    public String promptImageAnalysis() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/image-analysis.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image analysis prompt", e);
        }
    }

}
