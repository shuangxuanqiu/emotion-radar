package cn.chat.ggy.chataiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.chat.ggy.chataiagent.mapper")
public class ChatAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatAiAgentApplication.class, args);
    }

}
