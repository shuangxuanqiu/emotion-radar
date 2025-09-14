package cn.chat.ggy.chataiagent.service;

import cn.chat.ggy.chataiagent.DTO.ResultInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

/**
 * 异步图片处理服务接口
 * 提供非阻塞的图片分析和HTML生成功能
 * 
 * @author 来自小扬 (＾▽＾)／
 */
public interface AsyncImageProcessingService {
    
    /**
     * 异步处理图片并生成HTML
     * @param file 上传的图片文件
     * @param emotionalIndex 情绪指数
     * @param chatId 聊天ID
     * @return CompletableFuture包装的HTML URL
     */
    CompletableFuture<String> processImageAndGenerateHtmlAsync(MultipartFile file, Long emotionalIndex, String chatId);
    
    /**
     * 异步处理图片分析
     * @param message 分析消息
     * @param file 上传的图片文件
     * @param emotionalIndex 情绪指数
     * @param chatId 聊天ID
     * @return CompletableFuture包装的分析结果
     */
    CompletableFuture<ResultInfo> processImageAnalysisAsync(String message, MultipartFile file, Long emotionalIndex, String chatId);
    
    /**
     * 批量处理多个图片
     * @param files 图片文件数组
     * @param emotionalIndex 情绪指数
     * @param chatId 聊天ID
     * @return CompletableFuture包装的处理结果列表
     */
    CompletableFuture<String[]> batchProcessImagesAsync(MultipartFile[] files, Long emotionalIndex, String chatId);
}
