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

    /**
     * 情感雷达的核心提示词-全能知识库-文本内容解析
     * @return
     */
    @Bean(name="promptChatCriterion")
    public String promptChatCriterion() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/chat-criterion.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load chat criterion prompt", e);
        }
    }
    /**
     * 情感雷达-续写提示词
     * @return
     */
    @Bean(name="promptContinuationCriterion")
    public String promptContinuationCriterion() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/chat-continuation-criterion.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load chat criterion prompt", e);
        }
    }

    /**
     * 基础聊天的bean
     * @return
     */
    @Bean(name="promptChatBsicsn")
    public String promptChatBsicsn() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/chat-basics.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load chat criterion prompt", e);
        }
    }
    /**
     * 图片识别的 bean - 流式
     * @return
     */
    @Bean(name="promptImageAnalysisStream")
    public String promptImageAnalysisStream() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/image-analysis-stream.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image analysis prompt", e);
        }
    }
    /**
     * 图片识别的 bean
     * @return
     */
    @Bean(name="promptImageAnalysis")
    public String promptImageAnalysis() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/image-analysis.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image analysis prompt", e);
        }
    }
    /**
     * 图片识别的 bean
     * @return
     */
    @Bean(name="promptImageAnalysisSimplify")
    public String promptImageAnalysisSimplify() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/image-analysis2.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image analysis prompt", e);
        }
    }

    /**
     * 智能体路由的 bean
     * @return
     */
    @Bean(name="promptRouting")
    public String promptRouting() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/chat-routing.txt");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image analysis prompt", e);
        }
    }

}
