package cn.chat.ggy.chataiagent.app;

import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.service.ChatAIAssistant;
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
        String message = """
                {
                   "platform": "微信",
                   "chatType": "private",
                   "extractedTime": "18:12",
                   "messages": [
                     {
                       "sender": "我",
                       "timestamp": "18:01",
                       "messageType": "text",
                       "content": "拼好饭+塞尔达，今晚快乐拉满啊🔥",
                       "contentDescription": "文字消息，包含表情符号火焰"
                     },
                     {
                       "sender": "我",
                       "timestamp": "18:01",
                       "messageType": "link",
                       "content": "http://ddns.6010.top:12845/woZcn7/",
                       "contentDescription": "链接分享，指向一个网页地址"
                     },
                     {
                       "sender": "曹洪源",
                       "timestamp": "18:09",
                       "messageType": "text",
                       "content": "太会了！这波操作封神了😺",
                       "contentDescription": "文字消息，包含猫脸表情"
                     },
                     {
                       "sender": "曹洪源",
                       "timestamp": "18:09",
                       "messageType": "text",
                       "content": "有没有快递",
                       "contentDescription": "文字消息，询问是否有快递"
                     },
                     {
                       "sender": "曹洪源",
                       "timestamp": "18:09",
                       "messageType": "text",
                       "content": "帮你取了",
                       "contentDescription": "文字消息，表示帮助对方取快递"
                     },
                     {
                       "sender": "我",
                       "timestamp": "18:12",
                       "messageType": "image",
                       "content": "[截图]",
                       "contentDescription": "手机屏幕截图，显示快递信息和订单详情"
                     },
                     {
                       "sender": "我",
                       "timestamp": "18:12",
                       "messageType": "emoji",
                       "content": "🤔",
                       "contentDescription": "思考表情符号"
                     }
                   ]
                 }
                """;
        ResultInfo str = deepSeekAPP.doChat(message, "1");
        //制作 html 文件
        String resultUrl = chatAIAssistant.htmlStorage(str, "1");
        System.out.println("返回的结果："+resultUrl);
    }
}