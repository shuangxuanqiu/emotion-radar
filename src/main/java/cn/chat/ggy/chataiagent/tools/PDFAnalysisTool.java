package cn.chat.ggy.chataiagent.tools;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PDFAnalysisTool {

    @Tool(description = "Pass in the path of the PDF file and parse it into a string")
    public Map<Integer, String> readFilePDF(@ToolParam(description = "Path of the PDF file") String filePath) throws IOException {
        Map<Integer, String> pageContentMap = new HashMap<>();
        PdfReader reader = new PdfReader(filePath);
        PdfDocument pdfDoc = new PdfDocument(reader);

        int numberOfPages = pdfDoc.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            System.out.println("解析第"+i+"个开始");
            String pageText = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
            pageContentMap.put(i, pageText);
            System.out.println("解析第"+i+"个完成");
        }
        pdfDoc.close();
        reader.close();
        return pageContentMap;
    }
}
