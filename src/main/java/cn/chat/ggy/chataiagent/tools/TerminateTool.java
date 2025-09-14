package cn.chat.ggy.chataiagent.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;

public class TerminateTool {

    @Tool(description = """  
            Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.  
            "When you have finished all the tasks, call this tool to end the work.  
            """)
    public String terminate(ToolContext toolContext){
        Object chatId = toolContext.getContext().get("chatId");
        String content = "任务结束。房间："+chatId;
        System.out.println(content);
        return content;
    }
}

