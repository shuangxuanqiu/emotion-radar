package cn.chat.ggy.chataiagent.DTO.example;

import cn.chat.ggy.chataiagent.DTO.ChatBackgroundAnalysis;
import cn.chat.ggy.chataiagent.DTO.ResultInfo;
import cn.chat.ggy.chataiagent.DTO.ResultStructure;

import java.util.Arrays;
import java.util.List;

/**
 * 聊天回复结构化输出示例
 * 展示如何构建符合新提示词要求的结构化响应
 */
public class ChatResponseExample {
    
    /**
     * 创建朋友间日常闲聊的示例响应
     */
    public static ResultInfo createFriendChatExample() {
        // 背景信息（每个回复选项都会包含）
        String relationshipType = "亲密朋友";
        String conversationScene = "周末休闲时光的日常分享";
        String topicNature = "轻松娱乐，分享生活趣事";
        String userToneCharacteristics = "活泼外向，喜用表情符号，表达直接热情";
        Integer overallEmotionalIndex = 7;
        String emotionalReason = "基于轻松友好的聊天氛围和用户的活泼特质";
        
        // 创建背景分析（保持兼容性）
        ChatBackgroundAnalysis backgroundAnalysis = new ChatBackgroundAnalysis();
        backgroundAnalysis.setRelationshipType(relationshipType);
        backgroundAnalysis.setConversationScene(conversationScene);
        backgroundAnalysis.setTopicNature(topicNature);
        backgroundAnalysis.setUserToneCharacteristics(userToneCharacteristics);
        
        // 创建回复选项（每个选项都包含完整背景信息）
        ResultStructure option1 = new ResultStructure();
        option1.setRelationshipType(relationshipType);
        option1.setConversationScene(conversationScene);
        option1.setTopicNature(topicNature);
        option1.setUserToneCharacteristics(userToneCharacteristics);
        option1.setOverallEmotionalIndex(overallEmotionalIndex);
        option1.setEmotionalReason(emotionalReason);
        option1.setMessage("这个主意很棒！我加入～[呲牙]");
        option1.setEmotionalIndex(7);
        
        ResultStructure option2 = new ResultStructure();
        option2.setRelationshipType(relationshipType);
        option2.setConversationScene(conversationScene);
        option2.setTopicNature(topicNature);
        option2.setUserToneCharacteristics(userToneCharacteristics);
        option2.setOverallEmotionalIndex(overallEmotionalIndex);
        option2.setEmotionalReason(emotionalReason);
        option2.setMessage("哈哈听起来超有趣的[哇]");
        option2.setEmotionalIndex(8);
        
        ResultStructure option3 = new ResultStructure();
        option3.setRelationshipType(relationshipType);
        option3.setConversationScene(conversationScene);
        option3.setTopicNature(topicNature);
        option3.setUserToneCharacteristics(userToneCharacteristics);
        option3.setOverallEmotionalIndex(overallEmotionalIndex);
        option3.setEmotionalReason(emotionalReason);
        option3.setMessage("好啊好啊，什么时候？");
        option3.setEmotionalIndex(7);
        
        List<ResultStructure> messages = Arrays.asList(option1, option2, option3);
        
        // 创建完整结果
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setBackgroundAnalysis(backgroundAnalysis);
        resultInfo.setOverallEmotionalIndex(overallEmotionalIndex);
        resultInfo.setEmotionalReason(emotionalReason);
        resultInfo.setMessages(messages);
        
        return resultInfo;
    }
    
    /**
     * 创建工作场合礼貌拒绝的示例响应
     */
    public static ResultInfo createWorkRefusalExample() {
        // 背景信息（每个回复选项都会包含）
        String relationshipType = "普通同事";
        String conversationScene = "工作时间的任务邀请";
        String topicNature = "工作相关，需要明确回应";
        String userToneCharacteristics = "职场礼貌，表达含蓄但清晰";
        Integer overallEmotionalIndex = 1;
        String emotionalReason = "需要拒绝但保持职场关系和谐";
        
        // 创建背景分析（保持兼容性）
        ChatBackgroundAnalysis backgroundAnalysis = new ChatBackgroundAnalysis();
        backgroundAnalysis.setRelationshipType(relationshipType);
        backgroundAnalysis.setConversationScene(conversationScene);
        backgroundAnalysis.setTopicNature(topicNature);
        backgroundAnalysis.setUserToneCharacteristics(userToneCharacteristics);
        
        // 创建回复选项（每个选项都包含完整背景信息）
        ResultStructure option1 = new ResultStructure();
        option1.setRelationshipType(relationshipType);
        option1.setConversationScene(conversationScene);
        option1.setTopicNature(topicNature);
        option1.setUserToneCharacteristics(userToneCharacteristics);
        option1.setOverallEmotionalIndex(overallEmotionalIndex);
        option1.setEmotionalReason(emotionalReason);
        option1.setMessage("这次先不啦，谢谢邀请[抱拳]");
        option1.setEmotionalIndex(1);
        
        ResultStructure option2 = new ResultStructure();
        option2.setRelationshipType(relationshipType);
        option2.setConversationScene(conversationScene);
        option2.setTopicNature(topicNature);
        option2.setUserToneCharacteristics(userToneCharacteristics);
        option2.setOverallEmotionalIndex(overallEmotionalIndex);
        option2.setEmotionalReason(emotionalReason);
        option2.setMessage("抱歉这边有点忙，下次吧");
        option2.setEmotionalIndex(1);
        
        ResultStructure option3 = new ResultStructure();
        option3.setRelationshipType(relationshipType);
        option3.setConversationScene(conversationScene);
        option3.setTopicNature(topicNature);
        option3.setUserToneCharacteristics(userToneCharacteristics);
        option3.setOverallEmotionalIndex(overallEmotionalIndex);
        option3.setEmotionalReason(emotionalReason);
        option3.setMessage("时间冲突了，改天再约[微笑]");
        option3.setEmotionalIndex(1);
        
        List<ResultStructure> messages = Arrays.asList(option1, option2, option3);
        
        // 创建完整结果
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setBackgroundAnalysis(backgroundAnalysis);
        resultInfo.setOverallEmotionalIndex(overallEmotionalIndex);
        resultInfo.setEmotionalReason(emotionalReason);
        resultInfo.setMessages(messages);
        
        return resultInfo;
    }
}
