package cn.chat.ggy.chataiagent.app;

import cn.chat.ggy.chataiagent.advisor.MyLoggerAdvisor;
import cn.chat.ggy.chataiagent.model.enums.ChatScene;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class routingAPP {
    private final ChatClient chatClient;
    private final String systemPrompt;

    @Value("${spring.ai.dashscope.routing-chat-model.options.model}")
    private String routingChatModel;

    public routingAPP(ChatModel dashscopeChatModel,
                            @Qualifier("promptRouting") String promptRouting) {
        this.systemPrompt = promptRouting;
        //初始化构建
        chatClient =  ChatClient.builder(dashscopeChatModel)
                //引入系统提示词
                .defaultSystem(systemPrompt)
                //顾问，拦截器
                .defaultAdvisors(
                        //自定义 advisor
                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     * 根据聊天背景和情感参数进行路由判断
     * @param conversationScene 聊天背景
     * @param emotionalIndex 情感参数(1-10)
     * @return ChatScene枚举值
     */
    public ChatScene routeToScene(String conversationScene, Long emotionalIndex) {
        log.info("开始路由分析 - 聊天背景: {}, 情感参数: {}", conversationScene, emotionalIndex);
        
        try {
            // 构建路由请求消息
            String routingMessage = String.format(
                "请分析以下信息并返回最合适的场景类型：\n" +
                "聊天背景：%s\n" +
                "情感参数：%s\n" +
                "请只返回以下值之一：chat_job 或 default_all",
                conversationScene,
                emotionalIndex != null ? emotionalIndex + "分" : "未指定"
            );

            // 调用AI进行路由判断
            Prompt prompt = new Prompt(routingMessage,
                    DashScopeChatOptions.builder()
                            .withModel(routingChatModel)
                            .build());

            String response = chatClient.prompt(prompt)
                    .call()
                    .content();

            log.info("路由AI返回结果: {}", response);

            // 解析返回结果
            ChatScene scene = parseRouteResponse(response);
            log.info("路由判断结果: {}", scene.getValue());
            return scene;

        } catch (Exception e) {
            log.error("路由判断失败，使用默认场景: {}", e.getMessage(), e);
            return ChatScene.DEFAULT_ALL; // 失败时返回默认场景
        }
    }

    /**
     * 解析AI返回的路由结果
     */
    private ChatScene parseRouteResponse(String response) {
        if (response == null || response.isEmpty()) {
            return ChatScene.DEFAULT_ALL;
        }

        // 清理响应文本
        String cleanResponse = response.trim().toLowerCase();

        // 判断是否包含职场相关关键词
        if (cleanResponse.contains("chat_job") || cleanResponse.contains("职场") || 
            cleanResponse.contains("工作")) {
            return ChatScene.CHAT_JOB;
        }

        // 默认返回全能场景
        return ChatScene.DEFAULT_ALL;
    }
}
