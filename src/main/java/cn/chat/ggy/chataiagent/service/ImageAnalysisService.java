package cn.chat.ggy.chataiagent.service;

import com.mybatisflex.core.service.IService;
import cn.chat.ggy.chataiagent.entity.ImageAnalysis;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 图片解析信息表 服务层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
public interface ImageAnalysisService extends IService<ImageAnalysis> {


    String saveImageFile(MultipartFile file) throws IOException;


    void saveImageAnalysisAsync(String imagePath, String analysisResult,String chatId, Long userId);

    /**
     * 根据图片路径获取图片资源
     *
     * @param imagePath 图片路径
     * @return 图片资源响应
     */
    ResponseEntity<Resource> getImageResource(String imagePath);
}
