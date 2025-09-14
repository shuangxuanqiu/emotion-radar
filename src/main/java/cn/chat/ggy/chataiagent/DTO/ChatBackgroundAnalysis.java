package cn.chat.ggy.chataiagent.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "聊天背景分析实体")
@Data
public class ChatBackgroundAnalysis {
    
    @Schema(description = "关系类型", example = "亲密朋友")
    private String relationshipType;
    
    @Schema(description = "对话场景", example = "周末休闲时光的日常分享")
    private String conversationScene;
    
    @Schema(description = "话题性质", example = "轻松娱乐，分享生活趣事")
    private String topicNature;
    
    @Schema(description = "用户语气特征", example = "活泼外向，喜用表情符号，表达直接热情")
    private String userToneCharacteristics;
}
