package cn.chat.ggy.chataiagent.model.dto.imageanalysis;

import cn.chat.ggy.chataiagent.common.PageRequest;
import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImageAnalysisQueryRequest extends PageRequest implements Serializable {
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
}
