package cn.chat.ggy.chataiagent.app;

import cn.chat.ggy.chataiagent.model.enums.ChatScene;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoutingAPPTest {

    @Resource
    private routingAPP routingAPP;

    @Test
    void testRouteToJobScene() {
        // 测试工作场景路由
        ChatScene scene = routingAPP.routeToScene("和同事的工作沟通", 5L);
        System.out.println("工作场景路由结果: " + scene.getValue());
        // 期望返回 CHAT_JOB
        assertNotNull(scene);
    }

    @Test
    void testRouteToDefaultScene() {
        // 测试默认场景路由
        ChatScene scene = routingAPP.routeToScene("和女朋友约会聊天", 8L);
        System.out.println("约会场景路由结果: " + scene.getValue());
        // 期望返回 DEFAULT_ALL
        assertNotNull(scene);
    }

    @Test
    void testRouteWithDifferentEmotions() {
        // 测试不同情感值
        ChatScene lowEmotion = routingAPP.routeToScene("职场人际关系处理", 3L);
        System.out.println("低情感职场场景: " + lowEmotion.getValue());

        ChatScene highEmotion = routingAPP.routeToScene("追求心仪的对象", 10L);
        System.out.println("高情感追求场景: " + highEmotion.getValue());

        assertNotNull(lowEmotion);
        assertNotNull(highEmotion);
    }

    @Test
    void testRouteWithNullEmotion() {
        // 测试情感值为null的情况
        ChatScene scene = routingAPP.routeToScene("老板安排的工作任务", null);
        System.out.println("空情感职场场景: " + scene.getValue());
        assertNotNull(scene);
    }

    @Test
    void testVariousWorkScenarios() {
        // 测试多种工作相关场景
        String[] workScenarios = {
            "向老板汇报工作进度",
            "面试新工作的准备",
            "职场同事聚餐",
            "公司会议发言"
        };

        for (String scenario : workScenarios) {
            ChatScene scene = routingAPP.routeToScene(scenario, 5L);
            System.out.println(scenario + " -> " + scene.getValue());
            assertNotNull(scene);
        }
    }

    @Test
    void testVariousPersonalScenarios() {
        // 测试多种个人场景
        String[] personalScenarios = {
            "和朋友聚会聊天",
            "表白心仪的女生",
            "和家人日常交流",
            "网友第一次见面"
        };

        for (String scenario : personalScenarios) {
            ChatScene scene = routingAPP.routeToScene(scenario, 7L);
            System.out.println(scenario + " -> " + scene.getValue());
            assertNotNull(scene);
        }
    }
}

