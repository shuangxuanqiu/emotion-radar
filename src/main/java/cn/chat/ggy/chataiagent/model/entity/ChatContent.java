package cn.chat.ggy.chataiagent.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对话内容表 实体类。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("chat_content")
public class ChatContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 创建用户id
     */
    @Column("userId")
    private Long userId;
    /**
     * 房间 id
     */
    @Column("chatId")
    private String chatId;
    /**
     * 生成的 html 文件地址
     */
    @Column("resultUrl")
    private String resultUrl;

    /**
     * token 数量
     */
    @Column("tokenQuantity")
    private Long tokenQuantity;

    /**
     * 返回的内容
     */
    @Column("resultContent")
    private String resultContent;

    /**
     * 用户选择内容
     */
    @Column("choiceContent")
    private String choiceContent;

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
