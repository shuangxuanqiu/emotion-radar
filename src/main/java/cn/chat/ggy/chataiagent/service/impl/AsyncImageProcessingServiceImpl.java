package cn.chat.ggy.chataiagent.service.impl;

import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.service.AsyncImageProcessingService;
import cn.chat.ggy.chataiagent.service.ChatAIAssistant;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;
import java.util.Arrays;

/**
 * 异步图片处理服务实现类
 * 提供高性能的异步图片处理能力
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Slf4j
@Service
public class AsyncImageProcessingServiceImpl implements AsyncImageProcessingService {

    @Resource
    private ChatAIAssistant chatAIAssistant;

    @Override
    @Async("databaseAsyncExecutor")
    public CompletableFuture<String> processImageAndGenerateHtmlAsync(MultipartFile file, Long emotionalIndex, String chatId) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                log.info("开始异步处理图片和生成HTML - chatId: {}, 文件: {}", chatId, file.getOriginalFilename());
                
                // 执行图片分析
                ResultInfo resultInfo = chatAIAssistant.chatHelpMe("请分析这张图片的内容", file, emotionalIndex,"", chatId);
                
                // 生成HTML
                String htmlUrl = chatAIAssistant.htmlStorage(resultInfo, chatId);
                
                long endTime = System.currentTimeMillis();
                log.info("异步处理完成 - 耗时: {}ms, chatId: {}, 结果URL: {}", 
                        endTime - startTime, chatId, htmlUrl);
                
                return htmlUrl;
                
            } catch (Exception e) {
                long endTime = System.currentTimeMillis();
                log.error("异步处理失败 - 耗时: {}ms, chatId: {}, 错误: {}", 
                        endTime - startTime, chatId, e.getMessage(), e);
                throw new RuntimeException("异步处理失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    @Async("databaseAsyncExecutor")
    public CompletableFuture<ResultInfo> processImageAnalysisAsync(String message, MultipartFile file, Long emotionalIndex, String chatId) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                log.info("开始异步图片分析 - chatId: {}, 消息长度: {}", chatId, message.length());
                
                ResultInfo resultInfo = chatAIAssistant.chatHelpMe(message, file, emotionalIndex,"", chatId);
                
                long endTime = System.currentTimeMillis();
                log.info("异步图片分析完成 - 耗时: {}ms, chatId: {}", endTime - startTime, chatId);
                
                return resultInfo;
                
            } catch (Exception e) {
                long endTime = System.currentTimeMillis();
                log.error("异步图片分析失败 - 耗时: {}ms, chatId: {}, 错误: {}", 
                        endTime - startTime, chatId, e.getMessage(), e);
                throw new RuntimeException("异步图片分析失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    @Async("databaseAsyncExecutor")
    public CompletableFuture<String[]> batchProcessImagesAsync(MultipartFile[] files, Long emotionalIndex, String chatId) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                log.info("开始批量异步处理图片 - chatId: {}, 文件数量: {}", chatId, files.length);
                
                // 并行处理所有图片
                CompletableFuture<String>[] futures = Arrays.stream(files)
                    .map(file -> {
                        String subChatId = chatId + "_" + RandomUtil.randomString(3);
                        return processImageAndGenerateHtmlAsync(file, emotionalIndex, subChatId);
                    })
                    .toArray(CompletableFuture[]::new);
                
                // 等待所有任务完成
                CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);
                String[] results = allOf.thenApply(v -> 
                    Arrays.stream(futures)
                        .map(CompletableFuture::join)
                        .toArray(String[]::new)
                ).join();
                
                long endTime = System.currentTimeMillis();
                log.info("批量异步处理完成 - 耗时: {}ms, chatId: {}, 成功处理: {}/{}", 
                        endTime - startTime, chatId, results.length, files.length);
                
                return results;
                
            } catch (Exception e) {
                long endTime = System.currentTimeMillis();
                log.error("批量异步处理失败 - 耗时: {}ms, chatId: {}, 错误: {}", 
                        endTime - startTime, chatId, e.getMessage(), e);
                throw new RuntimeException("批量异步处理失败: " + e.getMessage(), e);
            }
        });
    }
}
