package cn.chat.ggy.chataiagent.core.saver;

import cn.chat.ggy.chataiagent.model.saver.HtmlCodeResult;
import cn.chat.ggy.chataiagent.core.CodeFileSaverTemplate;
import cn.chat.ggy.chataiagent.exception.BusinessException;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.hutool.core.util.StrUtil;

import static jodd.util.PropertiesUtil.writeToFile;


public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {


    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        //保存 HTML 文件
        writeToFile(baseDirPath,"index.html",result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        //HTML代码不能为空
        if(StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"html代码内容不能为空");
        }
    }
}
