package cn.chat.ggy.chataiagent.model.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "情感雷达流式识别请求参数")
@Data
public class EmotionRadarStreamRequestVO {
    @Schema(description = "总提示词", example = "请识别这张聊天界面截图")
    private String message;

    @Schema(description = "情绪背景", example = "工作朋友")
    private String conversationScene;

    @Schema(description = "情感指数参数", example = "5")
    private Long emotionalIndex;

    @Schema(description = "会话ID")
    private String chatId;
}

