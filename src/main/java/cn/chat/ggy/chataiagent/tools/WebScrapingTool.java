package cn.chat.ggy.chataiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * 网页抓取-工具
 */
public class WebScrapingTool {

    // 最大内容长度限制（字符数）
    private static final int MAX_CONTENT_LENGTH = 100000; // 设置为100K字符，留一些余量

    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            // 获取页面文本内容
            String content = doc.text().trim();
            
            // 检查内容长度
            if (content.length() > MAX_CONTENT_LENGTH) {
                return "The web page contains too much content. Please just keep the website address for the user. Website：" + url;
            }
            
            return content;
        } catch (IOException e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
