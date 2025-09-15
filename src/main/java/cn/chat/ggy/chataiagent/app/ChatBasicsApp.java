package cn.chat.ggy.chataiagent.app;

import cn.chat.ggy.chataiagent.advisor.MyLoggerAdvisor;
import cn.chat.ggy.chataiagent.chatmemory.RedisChatMemory;
import cn.chat.ggy.chataiagent.config.PromptConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

@Component
@Slf4j
@Order(2)
public class ChatBasicsApp {


    private final ChatClient chatClient;
    @Resource
    private ToolCallback[] allTools;
    @Value("${search-api.api-key}")
    private String searchApiKey;
    @Resource
    private Advisor virtualSimulationRagCloudAdvisor;
    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    public ChatBasicsApp(ChatModel dashscopeChatModel,
                         RedisTemplate<String,Object> redisTemplate,
                         PromptConfig promptConfig) {
        //基于redis的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new RedisChatMemory(redisTemplate))
                .maxMessages(5)
                .build();
        //初始化构建
        String SYSTEM_PROMPT = promptConfig.promptChatBsicsn();
        chatClient =  ChatClient.builder(dashscopeChatModel)
                //引入系统提示词
                .defaultSystem(SYSTEM_PROMPT)
                //顾问，拦截器
                .defaultAdvisors(
                        //上下文回答的保存机制
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                        //自定义 advisor
//                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     * 基础对话
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient.prompt()
                .user(message)
                .toolContext(Map.of(
                        "chatId", chatId
                ))//将房间 id 放入上下文
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
//                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                .advisors(virtualSimulationRagCloudAdvisor)//加载云知识库
                //开启自定义日志
                .advisors(new MyLoggerAdvisor())
//                .toolCallbacks(ToolCallbackProvider.from(allTools))
                .toolCallbacks(toolCallbackProvider)
                .stream()
                .content();
    }
}
