package cn.chat.ggy.chataiagent.model.dto.feedback;

import cn.chat.ggy.chataiagent.common.PageRequest;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackQueryRequest extends PageRequest implements Serializable {
    /**
     * 编号
     */
    @Column("id")
    private Long id;
    /**
     * 会话 id
     */
    @Column("chatId")
    private String chatId;

    /**
     * 用户选择类型，0正常复制，1不满意反馈
     */
    @Column("messageType")
    private Integer messageType;
}
