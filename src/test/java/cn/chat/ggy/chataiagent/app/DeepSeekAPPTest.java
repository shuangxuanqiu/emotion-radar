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
    void doChatN(){
      String s =   deepSeekAPP.doChat("现在有哪些洗衣机可用");
        System.out.println("返回的信息："+s);
    }
    @Test
    void doChatDeepSeek(){
        String message = """
                {
                  "platform": "WeChat",
                  "chatType": "single",
                  "extractedTime": "2023-10-05T23:53:00Z",
                  "messages": [
                    {
                      "sender": "曹洪源",
                      "timestamp": "18:12",
                      "messageType": "image",
                      "content": "[图片]",
                      "contentDescription": "一张截图，显示一个网页界面，包含订单信息和二维码。"
                    },
                    {
                      "sender": "我",
                      "timestamp": "18:16",
                      "messageType": "emoji",
                      "content": "[表情包：思考的表情]",
                      "contentDescription": "一个黄色的思考表情包。"
                    },
                    {
                      "sender": "我",
                      "timestamp": "18:16",
                      "messageType": "text",
                      "content": "回头请你喝奶茶，宇宙第一杯🎉",
                      "contentDescription": "文字消息，表达请对方喝奶茶的承诺，并附带庆祝表情。"
                    },
                    {
                      "sender": "曹洪源",
                      "timestamp": "18:18",
                      "messageType": "text",
                      "content": "小羊？",
                      "contentDescription": "文字消息，询问对方是否是‘小羊’。"
                    },
                    {
                      "sender": "曹洪源",
                      "timestamp": "18:18",
                      "messageType": "image",
                      "content": "[图片]",
                      "contentDescription": "一张快递单据的照片，显示了收件人信息、条形码和价格。"
                    },
                    {
                      "sender": "我",
                      "timestamp": "18:26",
                      "messageType": "text",
                      "content": "太聪明了吧",
                      "contentDescription": "文字消息，表示对对方行为的赞赏。"
                    },
                    {
                      "sender": "曹洪源",
                      "timestamp": "18:26",
                      "messageType": "text",
                      "content": "太会了！这波操作封神了🐱",
                      "contentDescription": "文字消息，高度赞扬对方的操作，并附带猫的表情。"
                    }
                  ]
                }
                
                """;
        ResultInfo resultInfo = deepSeekAPP.doChat(message, "133");
        System.out.println(JSONUtil.toJsonStr(resultInfo));
        //制作 html 文件
        String resultUrl = chatAIAssistant.htmlStorage(resultInfo, "133");
    }
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