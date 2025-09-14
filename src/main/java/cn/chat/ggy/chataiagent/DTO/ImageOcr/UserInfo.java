package cn.chat.ggy.chataiagent.DTO.ImageOcr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "返回用户实体 ")
@Data
public class UserInfo {
    @Schema(description = "用户名称")
    private String userName;
    @Schema(description = "用户消息内容")
    private String message;
}
