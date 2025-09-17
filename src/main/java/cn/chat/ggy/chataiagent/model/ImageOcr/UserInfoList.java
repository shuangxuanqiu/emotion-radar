package cn.chat.ggy.chataiagent.model.ImageOcr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "聊天界面分析结果")
@Data
public class UserInfoList {
    
    @Schema(description = "社交软件类型")
    private String platform;
    
    @Schema(description = "聊天类型", allowableValues = {"单聊", "群聊"})
    private String chatType;
    
    @Schema(description = "提取的时间范围")
    private String extractedTime;
    
    @Schema(description = "消息列表")
    private List<UserInfo> messages;
}
