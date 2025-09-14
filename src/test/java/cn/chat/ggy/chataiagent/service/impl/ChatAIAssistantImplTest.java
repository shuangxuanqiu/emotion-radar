package cn.chat.ggy.chataiagent.service.impl;

import cn.chat.ggy.chataiagent.DTO.ResultInfo;
import cn.chat.ggy.chataiagent.DTO.ResultStructure;
import cn.chat.ggy.chataiagent.service.ChatAIAssistant;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ChatAIAssistantImplTest {
    @Resource
    private ChatAIAssistant chatAIAssistant;
    @Test
    void htmlStorage() {
        ResultInfo resultInfo = new ResultInfo();
        List<ResultStructure> list = new ArrayList<>();
        
        // 创建第一个回复选项（包含背景信息）
        ResultStructure resultStructure1 = new ResultStructure();
        resultStructure1.setRelationshipType("亲密朋友");
        resultStructure1.setConversationScene("周末休闲时光的日常分享");
        resultStructure1.setTopicNature("轻松娱乐，分享生活趣事");
        resultStructure1.setUserToneCharacteristics("活泼外向，喜用表情符号，表达直接热情");
        resultStructure1.setOverallEmotionalIndex(7);
        resultStructure1.setEmotionalReason("基于轻松友好的聊天氛围和用户的活泼特质");
        resultStructure1.setEmotionalIndex(7);
        resultStructure1.setMessage("这个主意很棒！我加入～[呲牙]");
        list.add(resultStructure1);
        
        // 创建第二个回复选项
        ResultStructure resultStructure2 = new ResultStructure();
        resultStructure2.setRelationshipType("亲密朋友");
        resultStructure2.setConversationScene("周末休闲时光的日常分享");
        resultStructure2.setTopicNature("轻松娱乐，分享生活趣事");
        resultStructure2.setUserToneCharacteristics("活泼外向，喜用表情符号，表达直接热情");
        resultStructure2.setOverallEmotionalIndex(7);
        resultStructure2.setEmotionalReason("基于轻松友好的聊天氛围和用户的活泼特质");
        resultStructure2.setEmotionalIndex(8);
        resultStructure2.setMessage("哈哈听起来超有趣的[哇]");
        list.add(resultStructure2);
        
        // 创建第三个回复选项
        ResultStructure resultStructure3 = new ResultStructure();
        resultStructure3.setRelationshipType("亲密朋友");
        resultStructure3.setConversationScene("周末休闲时光的日常分享");
        resultStructure3.setTopicNature("轻松娱乐，分享生活趣事");
        resultStructure3.setUserToneCharacteristics("活泼外向，喜用表情符号，表达直接热情");
        resultStructure3.setOverallEmotionalIndex(7);
        resultStructure3.setEmotionalReason("基于轻松友好的聊天氛围和用户的活泼特质");
        resultStructure3.setEmotionalIndex(7);
        resultStructure3.setMessage("好啊好啊，什么时候？");
        list.add(resultStructure3);
        
        resultInfo.setMessages(list);
        String chatId = RandomUtil.randomString(6);
        String s = chatAIAssistant.htmlStorage(resultInfo, chatId);
        System.out.println(s);
    }
}