package cn.chat.ggy.chataiagent.model.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 图片识别流式返回 VO
 * 用于前端根据坐标在原始图片中画框
 */
@Schema(description = "图片识别流式返回实体")
@Data
public class ImageOcrStreamVO {
    
    @Schema(description = "消息类型", example = "ocr_progress")
    private String type;
    
    @Schema(description = "消息发送方")
    private String sender;
    
    @Schema(description = "文字内容")
    private String text;
    
    @Schema(description = "文字坐标，格式为 [x1, y1, x2, y2] 或 [x1, y1, x2, y2, x3, y3, x4, y4]")
    private List<Double> bbox;
    
    @Schema(description = "置信度 (0-1)")
    private Double confidence;
    
    @Schema(description = "行索引（从1开始）")
    private Integer lineIndex;
    
    @Schema(description = "页码（从1开始）")
    private Integer page;
    
    @Schema(description = "是否完成", example = "false")
    private Boolean isComplete;
}

