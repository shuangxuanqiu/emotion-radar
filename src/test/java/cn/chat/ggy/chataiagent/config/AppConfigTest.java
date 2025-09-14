package cn.chat.ggy.chataiagent.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 应用配置测试类
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@SpringBootTest
@ActiveProfiles("local")
class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    void testBaseUrlConfiguration() {
        assertNotNull(appConfig);
        assertNotNull(appConfig.getBaseUrl());
        assertEquals("http://localhost:8123", appConfig.getBaseUrl());
    }
}
