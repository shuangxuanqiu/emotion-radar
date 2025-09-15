package cn.chat.ggy.chataiagent.app;


import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.advisor.MyLoggerAdvisor;
import cn.chat.ggy.chataiagent.chatmemory.RedisChatMemory;
import cn.chat.ggy.chataiagent.config.PromptConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;


import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
@Order(2)
public class ChatBotApp {

    private final ChatClient chatClient;
    @Resource
    private ToolCallback[] allTools;
    @Value("${search-api.api-key}")
    private String searchApiKey;
    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    @Resource
    private MyLoggerAdvisor myLoggerAdvisor;
    
    /**
     * 初始化 AI 客户端（chatClient）
     * @param dashscopeChatModel DashScope ChatModel (qwen-plus)
     * @param redisTemplate Redis模板
     * @param promptConfig 提示词配置
     */
    public ChatBotApp(ChatModel dashscopeChatModel,
                      RedisTemplate<String,Object> redisTemplate,
                      PromptConfig promptConfig,
                      MyLoggerAdvisor myLoggerAdvisor) throws IOException {
        //基于redis的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new RedisChatMemory(redisTemplate))
                .maxMessages(2)
                .build();
        //初始化构建
        String SYSTEM_PROMPT = promptConfig.promptChatCriterion();
        chatClient =  ChatClient.builder(dashscopeChatModel)
                //引入系统提示词
                .defaultSystem(SYSTEM_PROMPT)
                //顾问，拦截器
                .defaultAdvisors(
                        //上下文回答的保存机制
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        //自定义 advisor (通过构造函数注入)
                        myLoggerAdvisor
                )
                .build();
    }



    /**
     * 基础模式-非流式
     * @param message
     * @param chatId
     * @return
     */
    public ResultInfo doChat(String message, String chatId) {
        ResultInfo entity = chatClient.prompt()
                .user(message)
                .toolContext(Map.of("chatId", chatId))//将房间号，存储到工具上下文中
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .toolCallbacks(ToolCallbackProvider.from(allTools))
                .toolCallbacks(toolCallbackProvider)
                .call().entity(ResultInfo.class);
        return entity;
    }
}
