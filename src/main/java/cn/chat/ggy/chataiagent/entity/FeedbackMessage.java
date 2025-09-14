package cn.chat.ggy.chataiagent.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 生成内容反馈表 实体类。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("feedback_message")
public class FeedbackMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @Id(keyType = KeyType.Auto)
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

    /**
     * 反馈内容
     */
    @Column("feedBackMessage")
    private String feedBackMessage;

    /**
     * 用户最终选择内容
     */
    @Column("resultStructure")
    private String resultStructure;

    /**
     * 创建用户id
     */
    @Column("userId")
    private Long userId;

    /**
     * 编辑时间
     */
    @Column("editTime")
    private LocalDateTime editTime;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;

}
