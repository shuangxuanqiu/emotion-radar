package cn.chat.ggy.chataiagent.app.chatScene;

import cn.chat.ggy.chataiagent.advisor.MyLoggerAdvisor;
import cn.chat.ggy.chataiagent.advisor.TextChatObservabilityAdvisor;
import cn.chat.ggy.chataiagent.chatmemory.RedisChatMemory;
import cn.chat.ggy.chataiagent.config.PromptConfig;
import cn.chat.ggy.chataiagent.monitor.MonitorContextHolder;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * DeepSeek 流式内容
 */
@Component
@Slf4j
public class DeepSeekStreamAPP {
    //基础提示词
    private final String systemPrompt;
    //续写提示词
    private final String systemPromptContinuation;

    private final ChatClient chatClient;
    @Value("${spring.ai.dashscope.deepseek.options.model}")
    private String deepSeekModel;
    @Resource
    private Advisor TikTokViralRagClaudeAdvisor;
    /**
     * DeepSeek聊天客户端
     * @param dashscopeChatModel AI聊天模型
     * @param promptChatCriterion 提示词配置
     * @param textChatObservabilityAdvisor 文字聊天可观测性顾问
     */
    public DeepSeekStreamAPP(ChatModel dashscopeChatModel,
                         RedisTemplate<String,Object> redisTemplate,
                         MyLoggerAdvisor myLoggerAdvisor,
                         TextChatObservabilityAdvisor textChatObservabilityAdvisor,
                         @Qualifier("promptChatCriterion") String promptChatCriterion,
                         @Qualifier("promptContinuationCriterion") String promptContinuationCriterion
                         ) throws IOException {
        this.systemPrompt  = promptChatCriterion;
        this.systemPromptContinuation = promptContinuationCriterion;
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new RedisChatMemory(redisTemplate))
                .maxMessages(1)
                .build();

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(this.systemPrompt)
                .defaultAdvisors(
                        //上下文回答的保存机制
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        myLoggerAdvisor,
                        //文字聊天AI可观测性 advisor
                        textChatObservabilityAdvisor)
                .build();
    }


    /**
     * DeepSeek基础聊天方法 - 流式的内容
     * @param message 用户消息
     * @param chatId 聊天ID
     * @return 聊天结果
     */
    public Flux<String> doChatStream(String message, String chatId) {
        log.info("DeepSeek模型处理聊天 - chatId: {}, model: {}", chatId, deepSeekModel);

        // 设置监控上下文
        MonitorContextHolder.setContext("chatId", chatId);
        MonitorContextHolder.setContext("requestUri", "/api/chat/deepseek");

        // 明确指定使用DeepSeek模型
        Prompt prompt = new Prompt(message,
                DashScopeChatOptions.builder()
                        .withModel(deepSeekModel)  // 使用配置的deepseek的模型
                        .withEnableThinking(false)
                        .build());

        return chatClient.prompt(prompt)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(TikTokViralRagClaudeAdvisor) // 添加知识库
//                .toolCallbacks(toolCallbackProvider)
                .stream().content();
    }
    /**
     * DeepSeek续写聊天方法 - 流式的内容
     * @param message 用户消息
     * @param chatId 聊天ID
     * @return 聊天结果
     */
    public Flux<String> continuationChatStream(String message, String chatId) {
        log.info("DeepSeek模型处理聊天 - chatId: {}, model: {}", chatId, deepSeekModel);

        // 设置监控上下文
        MonitorContextHolder.setContext("chatId", chatId);
        MonitorContextHolder.setContext("requestUri", "/api/chat/deepseek");

        // 明确指定使用DeepSeek模型
        Prompt prompt = new Prompt(message,
                DashScopeChatOptions.builder()
                        .withModel(deepSeekModel)  // 使用配置的deepseek的模型
                        .withEnableThinking(false)
                        .build());

        return chatClient.prompt(prompt)
                .system(systemPromptContinuation)//用续写提示词
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(TikTokViralRagClaudeAdvisor) // 添加知识库
//                .toolCallbacks(toolCallbackProvider)
                .stream().content();
    }
}
