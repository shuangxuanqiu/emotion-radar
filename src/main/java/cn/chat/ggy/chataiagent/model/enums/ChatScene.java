package cn.chat.ggy.chataiagent.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum ChatScene{


    CHAT_JOB("工作场景，只包含工作方面", " chat_job"),
    DEFAULT_ALL("默认场景，包含所有内容", "default_all"),;

    private final String text;
    private final String value;

    ChatScene(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static ChatScene getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (ChatScene anEnum : ChatScene.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
