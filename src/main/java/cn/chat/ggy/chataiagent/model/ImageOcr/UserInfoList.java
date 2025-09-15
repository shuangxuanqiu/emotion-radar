package cn.chat.ggy.chataiagent.model.ImageOcr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "返回用户实体 列表")
@Data
public class UserInfoList {

    private List<UserInfo> messages;
}
