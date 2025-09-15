package cn.chat.ggy.chataiagent.model.VO;

import cn.chat.ggy.chataiagent.model.dto.emotionRadar.FeedbackResultStructure;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户反馈 - 实体")
@Data
public class FeedbackMessageVO {
    @Schema(description = "会话 id")
    private  String chatId;
    @Schema(description = " 反馈内容")
    private  String  feedBackMessage;
    @Schema(description = "用户最终选择内容")
    private FeedbackResultStructure resultStructure;
    @Schema(description = "用户选择类型，0正常复制，1不满意反馈")
    private Integer messageType;
}




