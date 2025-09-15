package cn.chat.ggy.chataiagent.service.impl;

import cn.chat.ggy.chataiagent.Constant.AppConstant;
import cn.chat.ggy.chataiagent.Constant.FileConstant;
import cn.chat.ggy.chataiagent.DTO.ImageOcr.UserInfoList;
import cn.chat.ggy.chataiagent.DTO.ResultInfo;
import cn.chat.ggy.chataiagent.DTO.saver.HtmlCodeResult;
import cn.chat.ggy.chataiagent.app.ChatBotApp;
import cn.chat.ggy.chataiagent.app.ImageAnalysisAPP;
import cn.chat.ggy.chataiagent.core.saver.HtmlCodeFileSaverTemplate;
import cn.chat.ggy.chataiagent.core.HtmlTemplateOptimizer;
import cn.chat.ggy.chataiagent.entity.ChatContent;
import cn.chat.ggy.chataiagent.entity.ImageAnalysis;
import cn.chat.ggy.chataiagent.exception.BusinessException;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.chat.ggy.chataiagent.service.ChatAIAssistant;
import cn.chat.ggy.chataiagent.service.ChatContentService;
import cn.chat.ggy.chataiagent.service.ImageAnalysisService;
import cn.chat.ggy.chataiagent.service.CacheService;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class ChatAIAssistantImpl implements ChatAIAssistant {


    @Resource
    private ChatBotApp chatBotApp;
    @Resource
    private ImageAnalysisAPP imageAnalysisAPP;
    @Resource
    private ImageAnalysisService imageAnalysisService;

    @Resource
    private HtmlTemplateOptimizer htmlTemplateOptimizer;
    @Resource
    private CacheService cacheService;
    private static final HtmlCodeFileSaverTemplate htmlCodeFileSaver = new HtmlCodeFileSaverTemplate();

    /**
     * 核心解析方法
     * @param message  用户的 message
     * @param file     上传的图片
     * @param emotionalIndex 情绪值
     * @param chatId   会话 id
     * @return
     * @throws IOException
     */
    @Override
    public ResultInfo chatHelpMe(String message, MultipartFile file, Long emotionalIndex,String conversationScene, String chatId) throws IOException {
        long startTime = System.currentTimeMillis();
        try {
            String imagePath = null;
            String imageAnalysisResult = null;
            String fileHash = null;
            
            // 第一步：处理图片（如果有的话）
            if (file != null && !file.isEmpty()) {
                log.info("开始处理图片，文件名: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
                // todo 待优化，现在，先暂时废弃缓存
//                // 计算文件哈希值用于缓存
//                byte[] fileBytes = file.getBytes();
//                fileHash = cacheService.calculateFileHash(fileBytes);
//
//                // 尝试从缓存获取分析结果
//                ResultInfo cachedResult = cacheService.getCachedImageAnalysisResult(fileHash, emotionalIndex);
//                if (cachedResult != null) {
//                    long endTime = System.currentTimeMillis();
//                    log.info("使用缓存结果 - 耗时: {}ms, chatId: {}, fileHash: {}",
//                            (endTime - startTime), chatId, fileHash);
//                    return cachedResult;
//                }
                // 缓存未命中，执行图片分析
                log.info("缓存未命中，开始图片分析 - fileHash: {}", fileHash);
                
                // 保存图片文件
                imagePath = imageAnalysisService.saveImageFile(file);
                log.info("图片保存成功，路径: {}", imagePath);
                
                // 进行图片分析
                UserInfoList userInfoList = imageAnalysisAPP.ocrImage("请分析这张图片的内容", file,chatId);
                imageAnalysisResult = JSONUtil.toJsonStr(userInfoList);
                log.info("图片分析完成：{}", imageAnalysisResult);
                
                // 异步保存图片分析信息到数据库（不阻塞主流程）
                imageAnalysisService.saveImageAnalysisAsync(imagePath, imageAnalysisResult,chatId, null);
            }
            
            // 第二步：准备聊天消息
            String chatMessage;
            if (imageAnalysisResult != null) {
                chatMessage = imageAnalysisResult;
            } else {
                chatMessage = message;
            }
            
            // 添加情绪值信息
            if (emotionalIndex != null && emotionalIndex >= 1 && emotionalIndex <= 10) {
                chatMessage += " \n 用户要求的情绪值：" + emotionalIndex+" 分 \n";
            }
            //添加聊天背景
            chatMessage += " \n 用户要求的聊天背景："+conversationScene+" \n";
            
            // 第三步：进行AI聊天
            log.info("开始AI聊天处理，chatId: {}, 消息长度: {}", chatId, chatMessage.length());
            ResultInfo result = chatBotApp.doChat(chatMessage, chatId);
            log.info("AI聊天处理完成，chatId: {}", chatId);
            
            // 如果有图片且结果有效，缓存分析结果
            if (fileHash != null && result != null) {
                cacheService.cacheImageAnalysisResult(fileHash, emotionalIndex, result, 3600); // 缓存1小时
                log.debug("分析结果已缓存 - fileHash: {}, emotionalIndex: {}", fileHash, emotionalIndex);
            }

            long endTime = System.currentTimeMillis();
            log.info("接口总耗时: {}ms, chatId: {}", (endTime - startTime), chatId);
            return result;
            
        } catch (Exception e) {
            log.error("处理失败，chatId: {}, 错误: {}", chatId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "处理失败: " + e.getMessage());
        }
    }
    

    

    


    @Override
    public String htmlStorage(ResultInfo resultInfo, String chatId) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 使用优化后的HTML模板生成器
            String htmlCode = htmlTemplateOptimizer.generateOptimizedHtml(resultInfo, chatId);
            
            // 构建结果对象并保存
            HtmlCodeResult build = HtmlCodeResult.builder().htmlCode(htmlCode).build();
            htmlCodeFileSaver.saveCode(build, chatId);
            
            long endTime = System.currentTimeMillis();
            log.info("HTML生成完成 - 耗时: {} ms, chatId: {}, HTML大小: {} bytes", 
                    endTime - startTime, chatId, htmlCode.length());
            
            return String.format("%s/%s", AppConstant.CODE_DEPLOY_HOST, chatId);
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("HTML生成失败 - 耗时: {} ms, chatId: {}, 错误: {}", 
                    endTime - startTime, chatId, e.getMessage(), e);
            throw new RuntimeException("HTML生成失败: " + e.getMessage(), e);
        }
    }
}
