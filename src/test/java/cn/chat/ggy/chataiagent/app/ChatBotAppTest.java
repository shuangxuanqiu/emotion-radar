package cn.chat.ggy.chataiagent.app;


import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;

@SpringBootTest
class ChatBotAppTest {

    @Resource
    private ChatBotApp chatBotApp;

    @Test
    void doChat() {
        String info = """
                 User1：“已经连续加班第十天了…眼皮都在打架”
                 
                 User2：“快喝点咖啡提神，我给你点了热粥，半小时后到”
                 
                 User1：“可是方案还卡在第三页…脑子根本转不动”
                 
                 User2：“把文档发我，我帮你顺逻辑，你趴着睡15分钟”
                 
                 User1：“老板刚发邮件说明早又要提前开会…”
                 
                 User2：“简直疯了！我假装客户急call帮你请一小时假？”
                 
                 User1：“算了…撑到周五就好了吧（苦笑）”
                 
                 User2：“周五必须押你去吃火锅，不然我真要报警了”
                 
                 User1：“键盘上好像滴了水…原来是眼泪啊”
                 
                 User2：“（打语音过来）现在对着话筒哭五分钟，我帮你骂老板”
                 
                 User1：“…你骂人词汇量好贫乏”
                 
                 User2：“因为我在同时搜《劳动法》第36条给你看！”
                 
                 User1：“同事都走了…灯自动熄了一半”
                 
                 User2：“看窗外！我在楼下举着奶茶和你挥手👋”
                 
                 User1：“突然有点想笑…你好像偷地雷的”
                 
                 User2：“快下来！奶茶快凉了，顺便把辞职信草稿带给你✨”
                 """;

    }
    @Test
    void doChatImage() throws FileNotFoundException {



    }
}