package cn.chat.ggy.chataiagent.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.Scanner;

public class AskHuman {


    @Tool(description = "Seek help from humans")
    public String askUser(@ToolParam(description = "your problem") String question){
        System.out.println("智能体需要你的帮助："+question);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }



}
