package cn.chat.ggy.chataiagent.controller;

import cn.chat.ggy.chataiagent.common.BaseResponse;
import cn.chat.ggy.chataiagent.common.ResultUtils;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.chat.ggy.chataiagent.exception.ThrowUtils;
import cn.chat.ggy.chataiagent.model.dto.imageanalysis.ImageAnalysisQueryRequest;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import cn.chat.ggy.chataiagent.model.entity.ImageAnalysis;
import cn.chat.ggy.chataiagent.service.ImageAnalysisService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 图片解析信息表 控制层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@RestController
@RequestMapping("/imageAnalysis")
@Tag(name = "图片解析管理", description = "图片解析信息的增删改查操作接口，用于管理AI图片分析结果")
public class ImageAnalysisController {

    @Autowired
    private ImageAnalysisService imageAnalysisService;

    /**
     * 保存图片解析信息表。
     *
     * @param imageAnalysis 图片解析信息表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(summary = "保存图片解析信息", description = "创建新的图片解析记录，存储AI对图片的分析结果")
    public boolean save(@Parameter(description = "图片解析信息实体对象", required = true) @RequestBody ImageAnalysis imageAnalysis) {
        return imageAnalysisService.save(imageAnalysis);
    }

    /**
     * 根据主键删除图片解析信息表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(summary = "删除图片解析信息", description = "根据主键ID删除指定的图片解析记录")
    public boolean remove(@Parameter(description = "图片解析信息主键ID", required = true, example = "1") @PathVariable Long id) {
        return imageAnalysisService.removeById(id);
    }

    /**
     * 根据主键更新图片解析信息表。
     *
     * @param imageAnalysis 图片解析信息表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(summary = "更新图片解析信息", description = "根据主键更新图片解析信息内容")
    public boolean update(@Parameter(description = "图片解析信息实体对象", required = true) @RequestBody ImageAnalysis imageAnalysis) {
        return imageAnalysisService.updateById(imageAnalysis);
    }

    /**
     * 查询所有图片解析信息表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(summary = "查询所有图片解析信息", description = "获取系统中所有图片解析记录列表")
    public List<ImageAnalysis> list() {
        return imageAnalysisService.list();
    }

    /**
     * 根据主键获取图片解析信息表。
     *
     * @param id 图片解析信息表主键
     * @return 图片解析信息表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(summary = "获取图片解析信息详情", description = "根据主键ID获取单个图片解析记录的详细信息")
    public ImageAnalysis getInfo(@Parameter(description = "图片解析信息主键ID", required = true, example = "1") @PathVariable Long id) {
        return imageAnalysisService.getById(id);
    }

    /**
     * 分页查询图片解析信息表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(summary = "分页查询图片解析信息", description = "根据分页参数查询图片解析信息列表")
    public Page<ImageAnalysis> page(@Parameter(description = "分页查询参数", required = true) Page<ImageAnalysis> page) {
        return imageAnalysisService.page(page);
    }

    /**
     * 分页 参数 查询图片解析信息表
     *
     * @param imageAnalysisQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页 参数 查询图片解析信息", description = "根据分页参数和chatId查询图片解析信息列表")
    public BaseResponse<Page<ImageAnalysis>> listImageAnalysisByPage(@RequestBody ImageAnalysisQueryRequest imageAnalysisQueryRequest) {
        ThrowUtils.throwIf(imageAnalysisQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = imageAnalysisQueryRequest.getPageNum();
        long pageSize = imageAnalysisQueryRequest.getPageSize();
        Page<ImageAnalysis> imageAnalysisPage = imageAnalysisService.page(Page.of(pageNum, pageSize),
                imageAnalysisService.getQueryWrapper(imageAnalysisQueryRequest));
        return ResultUtils.success(imageAnalysisPage);
    }

    /**
     * 图片展示接口。
     *
     * @param request HTTP请求对象
     * @return 图片资源
     */
    @GetMapping("/display/**")
    @Operation(summary = "图片展示", description = "根据图片路径参数展示图片资源")
    public ResponseEntity<Resource> displayImage(HttpServletRequest request) {
        // 从请求URI中提取图片路径
        String requestURI = request.getRequestURI();
        String imagePath = requestURI.substring(requestURI.indexOf("/display/") + "/display".length());
        
        // 调用服务层方法处理图片展示
        return imageAnalysisService.getImageResource(imagePath);
    }

}
