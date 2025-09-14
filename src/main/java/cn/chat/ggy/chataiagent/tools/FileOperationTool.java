package cn.chat.ggy.chataiagent.tools;

import cn.chat.ggy.chataiagent.Constant.FileConstant;
import cn.hutool.core.io.FileUtil;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class FileOperationTool {
    private final String FILE_PATH = FileConstant.FILE_SAVE_DIR+"/file";

    @Tool(description = "Read content from a file ")
    public String readFile(@ToolParam(description = "Name of the file to read") String fileName){
        String filePath = FILE_PATH+"/"+fileName;
        try{
           return  FileUtil.readUtf8String(filePath);
        }catch (Exception e){
            return "Error reading file: "+e.getMessage();
        }
    }
    @Tool(description = "Write content to a file")
    public String wirteFie(
            @ToolParam(description = "Name of the file to write") String fineName,
            @ToolParam(description = "Content to write to the file") String content
    ){
        String filePath = FILE_PATH+"/"+fineName;
        try{
            //创建目录
            FileUtil.mkdir(FILE_PATH);
             FileUtil.writeUtf8String(content,filePath);
             return "File write successfullty to :"+filePath;
        } catch (Exception e) {
            return "Error writing file: "+e.getMessage();

        }

    }
}
