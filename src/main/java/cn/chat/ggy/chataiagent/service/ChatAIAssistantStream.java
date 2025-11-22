package cn.chat.ggy.chataiagent.service;

import cn.chat.ggy.chataiagent.model.VO.EmotionRadarStreamRequestVO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

public interface ChatAIAssistantStream {
    SseEmitter chatHelpMe(MultipartFile file, EmotionRadarStreamRequestVO request,  Map<String, SseEmitter> chatEmitters) throws IOException;

    SseEmitter continuationChatStream(Long emotionalIndex, String emotionalLabels, String chatId);


}
