package cn.chat.ggy.chataiagent.DTO.ImageOcr;

import cn.chat.ggy.chataiagent.DTO.ResultStructure;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "返回用户实体 列表")
@Data
public class UserInfoList {

    private List<UserInfo> messages;
}
