package cn.chat.ggy.chataiagent.core;

import cn.chat.ggy.chataiagent.Constant.AppConstant;
import cn.chat.ggy.chataiagent.exception.BusinessException;
import cn.chat.ggy.chataiagent.exception.ErrorCode;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;


import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 抽象代码文件保存器 - 模板方法模式
 * @param <T>
 */
public abstract class CodeFileSaverTemplate<T> {
    //文件保存根目录
    private static final String FILE_SAVE_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;


    public final File saveCode(T result,String chatId){
        //1.验证输入
        validateInput(result);
        //2.构建唯一目录
        String baseDirPath = buildUniqueDir(chatId);
        //3.保存文件（具体实现由子类提供）
        saveFiles(result,baseDirPath);
        //4.返回目录文件对象
        return new File(baseDirPath);
    }

    /**
     * 验证输入参数（可由子类覆盖）
     * @param result 代码结果对象
     */
    protected void validateInput(T result){
        if(result == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"代码结果对象不能为空");
        }
    }
    /**
     * 构建唯一目录路径 ： tmp/code_output/bizType_雪花 ID
     */
    protected final String buildUniqueDir(String chatId){
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + chatId;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }
    /**
     * 写入单个文件
     */
    protected void writeToFile(String dirPath,String filename,String content){
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content,filePath, StandardCharsets.UTF_8);
    }


    /**
     * 保存文件的具体实现（由子类实现）
     * @param result       代码结果对象
     * @param baseDirPath  基础目录路径
     */
    protected abstract void saveFiles(T result,String baseDirPath);
}
