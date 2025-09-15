package cn.chat.ggy.chataiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

public class ContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createInstance(){
        PromptTemplate promptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能回答已有内容的问题，别的没办法帮到您哦
                """);
        return ContextualQueryAugmenter.builder().allowEmptyContext(false)
                .emptyContextPromptTemplate(promptTemplate).build();

    }
}



















