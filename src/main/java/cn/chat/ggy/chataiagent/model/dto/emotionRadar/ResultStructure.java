package cn.chat.ggy.chataiagent.model.dto.emotionRadar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "聊天回复结构化实体")
@Data
public class ResultStructure {
    
    // 聊天背景分析信息
    @Schema(description = "关系类型", example = "亲密朋友")
    private String relationshipType;
    
    @Schema(description = "对话场景", example = "周末休闲时光的日常分享")
    private String conversationScene;
    
    @Schema(description = "话题性质", example = "轻松娱乐，分享生活趣事")
    private String topicNature;
    
    @Schema(description = "用户语气特征", example = "活泼外向，喜用表情符号，表达直接热情")
    private String userToneCharacteristics;
    
    // 情感指数相关
    @Schema(description = "整体情感指数(0-10分)", example = "7")
    private Integer overallEmotionalIndex;
    
    @Schema(description = "情感指数判断依据", example = "基于轻松友好的聊天氛围和用户的活泼特质")
    private String emotionalReason;
    
    // 回复文本内容
    @Schema(description = "回复文本内容", example = "这个主意很棒！我加入～[呲牙]")
    private String message;
    
    // 该回复选项的情感指数（兼容原有字段）
    @Schema(description = "该回复选项的情感指数(0-10分)", example = "7")
    private Integer emotionalIndex;
}
