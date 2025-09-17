package cn.chat.ggy.chataiagent.model.ImageOcr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "聊天消息实体")
@Data
public class UserInfo {
    @Schema(description = "发送者名称")
    private String sender;
    
    @Schema(description = "消息发送时间")
    private String timestamp;
    
    @Schema(description = "消息类型", allowableValues = {"文字", "图片", "视频", "语音", "文件", "转账", "红包", "位置", "链接", "联系人", "系统", "混合"})
    private String messageType;
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "内容描述（针对多媒体内容）")
    private String contentDescription;
}
