package cn.chat.ggy.chataiagent.app;

import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.service.ChatAIAssistant;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class DeepSeekAPPTest {
    @Resource
    private DeepSeekAPP deepSeekAPP;
    @Resource
    private ChatAIAssistant chatAIAssistant;
    @Test
    void doChat() {
        ResultInfo str =   JSONUtil.toBean("""
                {
                  "backgroundAnalysis": {
                    "relationshipType": "暧昧关系",
                    "conversationScene": "追求女神",
                    "topicNature": "约会邀请",
                    "userToneCharacteristics": "活泼热情"
                  },
                  "overallEmotionalIndex": 10,
                  "emotionalReason": "追求女神时需要展现热情和幽默感，同时保持一定的神秘感",
                  "messages": [
                    {
                      "relationshipType": "暧昧关系",
                      "conversationScene": "追求女神",
                      "topicNature": "约会邀请",
                      "userToneCharacteristics": "活泼热情",
                      "overallEmotionalIndex": 10,
                      "emotionalReason": "保持高情感指数以展现热情",
                      "message": "嘿嘿，被你发现了我的小心思～",
                      "emotionalIndex": 10
                    },
                    {
                      "relationshipType": "暧昧关系",
                      "conversationScene": "追求女神",
                      "topicNature": "约会邀请",
                      "userToneCharacteristics": "活泼热情",
                      "overallEmotionalIndex": 10,
                      "emotionalReason": "用幽默回应对方的调侃",
                      "message": "这不叫坏，这叫懂得欣赏你的美[旺柴]",
                      "emotionalIndex": 10
                    },
                    {
                      "relationshipType": "暧昧关系",
                      "conversationScene": "追求女神",
                      "topicNature": "约会邀请",
                      "userToneCharacteristics": "活泼热情",
                      "overallEmotionalIndex": 10,
                      "emotionalReason": "延续暧昧氛围",
                      "message": "今晚要不要来场'链接'测试？保证信号满格[奸笑]",
                      "emotionalIndex": 10
                    }
                  ]
                }
                """,ResultInfo.class);

        //制作 html 文件
        String resultUrl = chatAIAssistant.htmlStorage(str, "1233");
        System.out.println("返回的结果："+resultUrl);
    }
}