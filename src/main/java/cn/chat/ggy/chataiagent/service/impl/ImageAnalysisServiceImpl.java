package cn.chat.ggy.chataiagent.service.impl;

import cn.chat.ggy.chataiagent.Constant.FileConstant;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.chat.ggy.chataiagent.entity.ImageAnalysis;
import cn.chat.ggy.chataiagent.mapper.ImageAnalysisMapper;
import cn.chat.ggy.chataiagent.service.ImageAnalysisService;
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

/**
 * 图片解析信息表 服务层实现。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@Service
@Slf4j
public class ImageAnalysisServiceImpl extends ServiceImpl<ImageAnalysisMapper, ImageAnalysis>  implements ImageAnalysisService{
    /**
     * 保存上传的图片文件
     * @param file 上传的文件
     * @return 保存后的文件路径
     * @throws IOException 文件操作异常
     */
    @Override
    public String saveImageFile(MultipartFile file) throws IOException {
        // 创建保存目录
        String uploadDir = FileConstant.FILE_SAVE_DIR + "/images";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成带时间戳的唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 格式化当前时间为：yyyyMMddHHmmss
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 生成文件名：时间戳_UUID前8位.扩展名
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);
        String filename = timestamp + "_" + shortUuid + extension;

        // 保存文件
        Path filePath = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), filePath);

        // 返回相对路径
        return "/images/" + filename;
    }

    /**
     * 异步保存图片分析信息到数据库
     * @param imagePath 图片路径
     * @param analysisResult 分析结果
     * @param userId 用户ID
     */
    @Async("databaseAsyncExecutor")
    @Override
    public void saveImageAnalysisAsync(String imagePath, String analysisResult,String chatId, Long userId) {
        try {
            ImageAnalysis imageAnalysis = ImageAnalysis.builder()
                    .imagePath(imagePath)
                    .imageTxt(analysisResult)
                    .chatId(chatId)
                    .userId(userId)
                    .tokenQuantity(0L) // 可以根据实际需要计算token数量
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .isDelete(0)
                    .build();

            boolean saved = save(imageAnalysis);
            if (saved) {
                log.info("图片分析信息保存成功，路径: {}", imagePath);
            } else {
                log.error("图片分析信息保存失败，路径: {}", imagePath);
            }
        } catch (Exception e) {
            log.error("保存图片分析信息到数据库失败: {}", e.getMessage(), e);
        }
    }
}
