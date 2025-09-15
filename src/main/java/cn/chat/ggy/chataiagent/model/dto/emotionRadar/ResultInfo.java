package cn.chat.ggy.chataiagent.model.dto.emotionRadar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "聊天回复完整结果实体")
@Data
public class ResultInfo {
    
    @Schema(description = "聊天背景分析")
    private ChatBackgroundAnalysis backgroundAnalysis;
    
    @Schema(description = "整体情感指数(0-10分)")
    private Integer overallEmotionalIndex;
    
    @Schema(description = "情感指数判断依据")
    private String emotionalReason;
    
    @Schema(description = "回复选项列表")
    private List<ResultStructure> messages;
}
