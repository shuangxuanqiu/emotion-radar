package cn.chat.ggy.chataiagent.app;

import cn.chat.ggy.chataiagent.advisor.ImageAnalysisObservabilityAdvisor;
import cn.chat.ggy.chataiagent.advisor.MyLoggerAdvisor;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.Map;

@Component
@Slf4j
public class ImageAnalysisStreamAPP {
    private final ChatClient chatClient;
    private final String systemPrompt;
    @Value("${spring.ai.dashscope.chat-vl.options.model}")
    private String imageAnalysisModel;

    /**
     * 图片解析的 chatclient
     * @param dashscopeChatModel AI聊天模型
     * @param promptImageAnalysisStream 图片分析提示词
     * @param imageAnalysisObservabilityAdvisor 图像分析可观测性顾问
     */
    public ImageAnalysisStreamAPP(ChatModel dashscopeChatModel,
                            @Qualifier("promptImageAnalysisStream") String promptImageAnalysisStream,
                            ImageAnalysisObservabilityAdvisor imageAnalysisObservabilityAdvisor) {
        this.systemPrompt = promptImageAnalysisStream;
        //初始化构建
        chatClient =  ChatClient.builder(dashscopeChatModel)
                //引入系统提示词
                .defaultSystem(systemPrompt)
                //顾问，拦截器
                .defaultAdvisors(
                        //自定义 advisor
                        new MyLoggerAdvisor()
                ,imageAnalysisObservabilityAdvisor
                )
                .build();
    }

    /**
     *  图片 流式的识别
     * @param promptText   提示词
     * @param file          文件
     * @param chatId        房间 id
     * @return
     */
    public Flux<String> doChatByStream(String promptText, MultipartFile file, String chatId) {
        return Flux.defer(() -> {
            try {
                byte[] imageData = file.getBytes();

                ByteArrayResource imageResource = new ByteArrayResource(imageData) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                };

                Media media = new Media(MimeTypeUtils.parseMimeType(file.getContentType()), imageResource);

                UserMessage message = UserMessage.builder()
                        .text(promptText)
                        .media(media)
                        .build();

                message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

                Prompt chatPrompt = new Prompt(message,
                        DashScopeChatOptions.builder()
                                .withModel(imageAnalysisModel)
                                .withMultiModel(true)
                                .withVlHighResolutionImages(true)
                                .withEnableThinking(false)
                                .build());

                return chatClient.prompt(chatPrompt)
                        .toolContext(Map.of("chatId", chatId))
                        .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                        .stream()
                        .content();
            } catch (Exception e) {
                return Flux.error(e);
            }
        });
    }
}
