package cn.chat.ggy.chataiagent.app;

import cn.chat.ggy.chataiagent.advisor.MyLoggerAdvisor;
import cn.chat.ggy.chataiagent.chatmemory.RedisChatMemory;
import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.config.PromptConfig;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DeepSeekAPP {
    @Resource
    private ToolCallback[] allTools;
    private final ChatClient chatClient;
    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    @Value("${spring.ai.dashscope.deepseek.options.model}")
    private String deepSeekModel;
    @Resource
    private Advisor TikTokViralRagClaudeAdvisor;
    /**
     * DeepSeek聊天客户端
     * @param dashscopeChatModel AI聊天模型
     * @param promptConfig 提示词配置
     */
    public DeepSeekAPP(ChatModel dashscopeChatModel,
                       RedisTemplate<String,Object> redisTemplate,
                       MyLoggerAdvisor myLoggerAdvisor,
                       PromptConfig promptConfig) throws IOException {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new RedisChatMemory(redisTemplate))
                .maxMessages(1)
                .build();
        // 使用现有的chat-criterion提示词
        String SYSTEM_PROMPT = promptConfig.promptChatCriterion();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        //上下文回答的保存机制
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        myLoggerAdvisor)
                .build();
    }
    
    /**
     * DeepSeek聊天方法 - 参考ChatBotApp的doChat方法
     * @param message 用户消息
     * @param chatId 聊天ID
     * @return 聊天结果
     */
    public ResultInfo doChat(String message, String chatId) {
        log.info("DeepSeek模型处理聊天 - chatId: {}, model: {}", chatId, deepSeekModel);

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
                .call()
                .entity(ResultInfo.class);
    }

    public String doChat(String message){
        // 明确指定使用DeepSeek模型
        Prompt prompt = new Prompt(message,
                DashScopeChatOptions.builder()
                        .withModel(deepSeekModel)  // 使用配置的deepseek的模型
                        .withEnableThinking(false) // 关闭所谓的思考模式
                        .build());

        return chatClient.prompt(prompt)
                .toolCallbacks(toolCallbackProvider)
                .call().content();
    }


}
