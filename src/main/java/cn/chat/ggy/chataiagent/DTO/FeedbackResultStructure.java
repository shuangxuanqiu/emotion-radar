package cn.chat.ggy.chataiagent.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户反馈时的结构化数据
 * 专门用于接收前端反馈请求中的选择内容
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Schema(description = "用户反馈结构化数据")
@Data
public class FeedbackResultStructure {
    
    @Schema(description = "用户选择的文本内容", example = "这个主意很棒！我加入～[呲牙]")
    private String selectedText;
    
    @Schema(description = "选择时间戳", example = "2025-09-14T16:30:00.000Z")
    private String timestamp;
    
    @Schema(description = "用户选择的消息情感指数", example = "7")
    private Integer emotionalIndex;
    
    @Schema(description = "额外的反馈信息")
    private String additionalInfo;
}
