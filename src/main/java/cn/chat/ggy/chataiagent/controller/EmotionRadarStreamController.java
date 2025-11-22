package cn.chat.ggy.chataiagent.controller;

import cn.chat.ggy.chataiagent.app.ImageAnalysisStreamAPP;
import cn.chat.ggy.chataiagent.model.VO.EmotionRadarStreamRequestVO;
import cn.chat.ggy.chataiagent.monitor.MonitorContextHolder;
import cn.chat.ggy.chataiagent.service.ChatAIAssistantStream;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/stream-ai")
@Slf4j
public class EmotionRadarStreamController {
    private final Map<String, SseEmitter> chatEmitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private ImageAnalysisStreamAPP imageAnalysisStreamAPP;
    @Resource
    private ChatAIAssistantStream chatAIAssistantStream;

    @PostMapping(value = "/travel_guide/chat/sse/continuation")
    @Operation(summary = "情感雷达流式续写接口", description = "根据情感参数续写，返回 NDJSON（ResultStructure）")
    public SseEmitter continuationChatStream(
            @Parameter(description = "情感指数参数", required = true, example = "5") @RequestParam("emotionalIndex") Long emotionalIndex,
            @Parameter(description = "情绪标签", required = true, example = "推荐精神") @RequestParam("emotionalLabels") String emotionalLabels,
            @Parameter(description = "会话 id", required = true, example = "123QS") @RequestParam("chatId") String chatId
    ){
        return chatAIAssistantStream.continuationChatStream(emotionalIndex,emotionalLabels,chatId);
    }


    @PostMapping(value = "/travel_guide/chat/sse/emitter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "情感雷达流式接口", description = "上传图片进行AI识别，返回 JSON 文本流（{G:\"文本\"}）")
    public SseEmitter doChatWithLoveAppSseEmitter(@Parameter(description = "上传的图片文件", required = true) @RequestParam("file") MultipartFile file,
                                                  @Parameter(description = "请求参数VO", required = true) @ModelAttribute EmotionRadarStreamRequestVO request) throws IOException, IOException {
        String chatId = RandomUtil.randomString(6);
        request.setChatId(chatId);
        // 验证文件
        if (file == null || file.isEmpty()) {
            SseEmitter errorEmitter = new SseEmitter(1000L);
            try {
                errorEmitter.send(SseEmitter.event().name("error").data("{\"type\":\"error\",\"message\":\"文件不能为空\"}"));
                errorEmitter.complete();
            } catch (Exception e) {
                log.error("发送错误信息失败", e);
            }
            return errorEmitter;
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            SseEmitter errorEmitter = new SseEmitter(1000L);
            try {
                errorEmitter.send(SseEmitter.event().name("error").data("{\"type\":\"error\",\"message\":\"文件类型必须是图片\"}"));
                errorEmitter.complete();
            } catch (Exception e) {
                log.error("发送错误信息失败", e);
            }
            return errorEmitter;
        }
        log.info("总提示词：{}", request.getMessage());

//将会话 id 放到ThreadLocal 中
        MonitorContextHolder.setContext("chatId", chatId);

        return chatAIAssistantStream.chatHelpMe(file, request, chatEmitters);
    }

}
