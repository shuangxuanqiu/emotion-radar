package cn.chat.ggy.chataiagent.service;

import cn.chat.ggy.chataiagent.DTO.ResultInfo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ChatAIAssistant {
    ResultInfo chatHelpMe(String message, MultipartFile file,Long emotionalIndex,String ChatId) throws IOException;


    String htmlStorage(ResultInfo resultInfo, String chatId);
}
