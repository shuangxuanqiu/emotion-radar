package cn.chat.ggy.chataiagent.service.impl;

import cn.chat.ggy.chataiagent.Constant.FileConstant;
import cn.chat.ggy.chataiagent.exception.BusinessException;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.chat.ggy.chataiagent.model.dto.imageanalysis.ImageAnalysisQueryRequest;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.chat.ggy.chataiagent.model.entity.ImageAnalysis;
import cn.chat.ggy.chataiagent.mapper.ImageAnalysisMapper;
import cn.chat.ggy.chataiagent.service.ImageAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    /**
     * 根据图片路径获取图片资源
     *
     * @param imagePath 图片路径
     * @return 图片资源响应
     */
    @Override
    public ResponseEntity<Resource> getImageResource(String imagePath) {
        try {
            // 构建完整的文件路径
            String fullPath = FileConstant.FILE_SAVE_DIR + imagePath;
            Path path = Paths.get(fullPath);
            
            // 检查文件是否存在
            if (!Files.exists(path)) {
                log.warn("图片文件不存在: {}", fullPath);
                return ResponseEntity.notFound().build();
            }
            
            // 检查是否为文件（不是目录）
            if (!Files.isRegularFile(path)) {
                log.warn("请求的不是文件: {}", fullPath);
                return ResponseEntity.badRequest().build();
            }
            
            // 创建文件资源
            Resource resource = new FileSystemResource(path.toFile());
            
            // 获取文件的MIME类型
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                // 如果无法确定MIME类型，根据文件扩展名设置
                String fileName = path.getFileName().toString().toLowerCase();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    contentType = MediaType.IMAGE_JPEG_VALUE;
                } else if (fileName.endsWith(".png")) {
                    contentType = MediaType.IMAGE_PNG_VALUE;
                } else if (fileName.endsWith(".gif")) {
                    contentType = MediaType.IMAGE_GIF_VALUE;
                } else {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }
            }
            
            log.info("成功获取图片资源: {}", imagePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName().toString() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("获取图片资源失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取 QueryWrapper
     * @param imageAnalysisQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ImageAnalysisQueryRequest imageAnalysisQueryRequest) {
        if (imageAnalysisQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = imageAnalysisQueryRequest.getId();
        String chatId = imageAnalysisQueryRequest.getChatId();
        String sortField = imageAnalysisQueryRequest.getSortField();
        String sortOrder = imageAnalysisQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("chatId", chatId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }
}
