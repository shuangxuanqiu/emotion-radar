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
 * 图片解析信息表 实体类。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("image_analysis")
public class ImageAnalysis implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    /**
     * 房间 id
     */
    @Column("chatId")
    private String chatId;
    /**
     * 图片内容
     */
    @Column("imageTxt")
    private String imageTxt;

    /**
     * 文件路径
     */
    @Column("imagePath")
    private String imagePath;

    /**
     * token 数量
     */
    @Column("tokenQuantity")
    private Long tokenQuantity;

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
