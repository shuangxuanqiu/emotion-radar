package cn.chat.ggy.chataiagent.entity;

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
 * token统计表 实体类。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("consume_statistic")
public class ConsumeStatistic implements Serializable {

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
     * 总消耗 tokens
     */
    @Column("totalTokens")
    private Long totalTokens;

    /**
     * 提示词（输入）的 token
     */
    @Column("promptTokens")
    private Long promptTokens;

    /**
     * 完成任务(产出)的 token
     */
    @Column("completionTokens")
    private Long completionTokens;

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
