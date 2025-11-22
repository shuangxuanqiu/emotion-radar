package cn.chat.ggy.chataiagent.app;

import cn.chat.ggy.chataiagent.advisor.ImageAnalysisObservabilityAdvisor;
import cn.chat.ggy.chataiagent.advisor.MyLoggerAdvisor;
import cn.chat.ggy.chataiagent.model.ImageOcr.UserInfoList;
import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.monitor.MonitorContextHolder;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Component
@Slf4j
public class ImageAnalysisSimplifyAPP {
    private final ChatClient chatClient;
    private final String systemPrompt;

    @Value("${spring.ai.dashscope.chat-vl.options.model}")
    private String imageAnalysisModel;

    /**
     * 图片解析的 chatclient
     * @param dashscopeChatModel AI聊天模型
     * @param promptImageAnalysisSimplify 图片分析提示词
     * @param imageAnalysisObservabilityAdvisor 图像分析可观测性顾问
     */
    public ImageAnalysisSimplifyAPP(ChatModel dashscopeChatModel,
                            @Qualifier("promptImageAnalysisSimplify") String promptImageAnalysisSimplify,
                            ImageAnalysisObservabilityAdvisor imageAnalysisObservabilityAdvisor) {
        this.systemPrompt = promptImageAnalysisSimplify;
        //初始化构建
        chatClient =  ChatClient.builder(dashscopeChatModel)
                //引入系统提示词
                .defaultSystem(systemPrompt)
                //顾问，拦截器
                .defaultAdvisors(
                        //自定义 advisor
                        new MyLoggerAdvisor()
                        //图像分析AI可观测性 advisor
                        ,imageAnalysisObservabilityAdvisor
                )
                .build();
    }

    /**
     * 图片识别核心方法 - 优化版本
     * @param prompt  提示词
     * @param file    图片
     * @param chatId  会话 id
     * @return
     */
    public ResultInfo ocrImage(@RequestParam(defaultValue = "请分析这张图片的内容") String prompt, MultipartFile file, String chatId) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("开始图片OCR处理 - chatId: {}, 原始文件大小: {} bytes", chatId, file.getSize());

            // 步骤1：直接使用原图
            byte[] imageData = file.getBytes();
            log.info("使用原图进行分析 - chatId: {}, 图片大小: {} bytes", chatId, imageData.length);

            // 步骤2：创建优化的Media对象
            ByteArrayResource imageResource = new ByteArrayResource(imageData) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            Media media = new Media(MimeTypeUtils.parseMimeType(file.getContentType()), imageResource);

            // 步骤3：构建消息和提示词
            UserMessage message = UserMessage.builder()
                    .text(prompt)
                    .media(media)
                    .build();

            message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

            // 步骤4：优化的模型配置 - 降低分辨率处理以提升速度
            Prompt chatPrompt = new Prompt(message,
                    DashScopeChatOptions.builder()
                            .withModel(imageAnalysisModel)

                            .withMultiModel(true)
                            .withVlHighResolutionImages(true)  // 高分辨率模式
                            .withTemperature(0.6)               // 降低随机性以提升响应速度
                            .build());

            // 步骤5：执行AI分析
            long analysisStart = System.currentTimeMillis();

            // 设置监控上下文
            MonitorContextHolder.setContext("chatId", chatId);
            MonitorContextHolder.setContext("requestUri", "/api/image/analysis");

            ResultInfo entity = chatClient.prompt(chatPrompt)
                    .toolContext(Map.of("chatId", chatId))
                    .call()
                    .entity(ResultInfo.class);

            long totalTime = System.currentTimeMillis() - startTime;
            long analysisTime = System.currentTimeMillis() - analysisStart;

            log.info("图片OCR完成 - chatId: {}, AI分析耗时: {} ms, 总耗时: {} ms",
                    chatId, analysisTime, totalTime);

            return entity;

        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            log.error("图片OCR处理失败 - chatId: {}, 耗时: {} ms", chatId, totalTime, e);
            throw new RuntimeException("图片分析失败: " + e.getMessage(), e);
        }
    }
}
