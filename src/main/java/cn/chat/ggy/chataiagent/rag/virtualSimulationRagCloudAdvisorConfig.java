package cn.chat.ggy.chataiagent.rag;


import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义基于阿里云服务的 rag 增强顾问
 */
@Configuration
@Slf4j
public class virtualSimulationRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String dashScopeApiKey;

    @Bean
    public Advisor virtualSimulationRagCloudAdvisor() {

        DashScopeApi dashScopeApi =   DashScopeApi.builder().apiKey(dashScopeApiKey).build();
        final String KNOWLEDGE_INDEX = "虚拟仿真在职业教育上的应用-知识库";
        DashScopeDocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi, DashScopeDocumentRetrieverOptions.builder()
                .withIndexName(KNOWLEDGE_INDEX)
                .build());
        return RetrievalAugmentationAdvisor.builder().documentRetriever(documentRetriever).build();
    }
}



















