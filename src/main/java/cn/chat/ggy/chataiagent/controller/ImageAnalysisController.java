package cn.chat.ggy.chataiagent.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.chat.ggy.chataiagent.entity.ImageAnalysis;
import cn.chat.ggy.chataiagent.service.ImageAnalysisService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 图片解析信息表 控制层。
 *
 * @author 来自小扬 (＾▽＾)／
 */
@RestController
@RequestMapping("/imageAnalysis")
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
    public boolean save(@RequestBody ImageAnalysis imageAnalysis) {
        return imageAnalysisService.save(imageAnalysis);
    }

    /**
     * 根据主键删除图片解析信息表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return imageAnalysisService.removeById(id);
    }

    /**
     * 根据主键更新图片解析信息表。
     *
     * @param imageAnalysis 图片解析信息表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody ImageAnalysis imageAnalysis) {
        return imageAnalysisService.updateById(imageAnalysis);
    }

    /**
     * 查询所有图片解析信息表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
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
    public ImageAnalysis getInfo(@PathVariable Long id) {
        return imageAnalysisService.getById(id);
    }

    /**
     * 分页查询图片解析信息表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<ImageAnalysis> page(Page<ImageAnalysis> page) {
        return imageAnalysisService.page(page);
    }

}
