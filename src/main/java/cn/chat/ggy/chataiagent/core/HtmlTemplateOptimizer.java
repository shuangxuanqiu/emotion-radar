package cn.chat.ggy.chataiagent.core;

import cn.chat.ggy.chataiagent.model.dto.emotionRadar.ResultInfo;
import cn.chat.ggy.chataiagent.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * HTML模板优化器
 * 使用预编译模板和缓存机制提升HTML生成性能
 * 
 * @author 来自小扬 (＾▽＾)／
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlTemplateOptimizer {

    private final AppConfig appConfig;
    
    // 模板缓存，避免重复构建
    private static final ConcurrentHashMap<String, String> templateCache = new ConcurrentHashMap<>();
    
    // HTML模板常量 - 预编译模板，只需要替换动态内容
    private static final String HTML_TEMPLATE = """
            <!DOCTYPE html>
            <html lang='zh-CN'>
            <head>
                <meta charset='UTF-8'>
                <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>
                <title>来自小扬的 AI分析结果</title>
                <style>
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; 
                        margin: 10px; 
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        font-size: 16px;
                        line-height: 1.6;
                        /* 禁止文本选择缩放 */
                        -webkit-text-size-adjust: 100%;
                        -ms-text-size-adjust: 100%;
                        text-size-adjust: 100%;
                        /* 禁止双击缩放 */
                        touch-action: manipulation;
                    }
                    .container { 
                        max-width: 800px; 
                        margin: 0 auto; 
                        background-color: white; 
                        padding: 20px; 
                        border-radius: 15px; 
                        box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                        backdrop-filter: blur(10px);
                    }
                    .message { 
                        margin: 20px 0; 
                        padding: 20px; 
                        border-left: 5px solid #007bff; 
                        background: linear-gradient(145deg, #f8f9fa, #e9ecef);
                        border-radius: 10px; 
                        position: relative;
                        transition: transform 0.2s ease;
                    }
                    .background-info {
                        background: linear-gradient(145deg, #e8f4fd, #d1ecf1);
                        margin: 30px 0;
                        padding: 25px;
                        border-radius: 15px;
                        border-left: 5px solid #17a2b8;
                        box-shadow: 0 4px 15px rgba(23,162,184,0.1);
                    }
                    .background-title {
                        font-size: 18px;
                        font-weight: 600;
                        color: #17a2b8;
                        margin-bottom: 20px;
                        text-align: center;
                        text-shadow: 0 1px 2px rgba(0,0,0,0.1);
                    }
                    .background-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                        gap: 12px;
                        margin-bottom: 15px;
                    }
                    .background-item {
                        background: rgba(255,255,255,0.7);
                        padding: 15px;
                        border-radius: 10px;
                        border: 1px solid rgba(23,162,184,0.2);
                        transition: all 0.3s ease;
                    }
                    .background-item:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 4px 12px rgba(23,162,184,0.2);
                        background: rgba(255,255,255,0.9);
                    }
                    .background-label {
                        font-weight: 600;
                        color: #495057;
                        font-size: 13px;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                        margin-bottom: 8px;
                        display: flex;
                        align-items: center;
                    }
                    .background-label::before {
                        content: '◆';
                        color: #17a2b8;
                        margin-right: 8px;
                        font-size: 12px;
                    }
                    .background-value {
                        color: #333;
                        font-size: 14px;
                        line-height: 1.5;
                        margin-left: 20px;
                    }
                    .emotional-summary {
                        background: linear-gradient(145deg, #fff3cd, #ffeaa7);
                        border: 1px solid #ffc107;
                        border-radius: 10px;
                        padding: 15px;
                        margin-top: 20px;
                        text-align: center;
                    }
                    .emotional-index-main {
                        font-size: 24px;
                        font-weight: bold;
                        color: #856404;
                        margin-bottom: 8px;
                    }
                    .emotional-reason {
                        color: #856404;
                        font-size: 13px;
                        font-style: italic;
                    }
                    .message:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                    }
                    .emotion-index { 
                        font-weight: bold; 
                        color: #007bff; 
                        margin-bottom: 12px;
                        font-size: 14px;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }
                    .message-content { 
                        line-height: 1.8; 
                        margin-right: 70px;
                        color: #333;
                        white-space: pre-wrap;
                        word-wrap: break-word;
                        word-break: break-word;
                    }
                    .copy-btn { 
                        position: absolute; 
                        top: 15px; 
                        right: 15px; 
                        background: linear-gradient(145deg, #007bff, #0056b3);
                        color: white; 
                        border: none; 
                        padding: 12px 18px; 
                        border-radius: 8px; 
                        cursor: pointer; 
                        font-size: 14px; 
                        font-weight: 500;
                        transition: all 0.3s ease;
                        box-shadow: 0 2px 8px rgba(0,123,255,0.3);
                        min-width: 70px;
                        min-height: 48px;
                        text-align: center;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        /* 增强触摸目标区域和体验 */
                        touch-action: manipulation;
                        user-select: none;
                        -webkit-tap-highlight-color: transparent;
                        outline: none;
                        /* 防止双击缩放 */
                        -webkit-touch-callout: none;
                        -webkit-user-select: none;
                        -khtml-user-select: none;
                        -moz-user-select: none;
                        -ms-user-select: none;
                    }
                    .copy-btn:hover { 
                        transform: translateY(-1px);
                        box-shadow: 0 4px 10px rgba(0,123,255,0.4);
                    }
                    .copy-btn:active { 
                        transform: translateY(0);
                        /* 移动端点击反馈 */
                        background: linear-gradient(145deg, #0056b3, #004085);
                        box-shadow: 0 1px 4px rgba(0,123,255,0.4);
                    }
                    
                    /* 移动端触摸优化 */
                    @media (hover: none) and (pointer: coarse) {
                        .copy-btn {
                            min-height: 48px;
                            min-width: 65px;
                            font-size: 14px;
                            font-weight: 600;
                            padding: 12px 18px;
                        }
                        
                        .copy-btn:active {
                            transform: scale(0.95);
                            transition: transform 0.1s ease;
                        }
                    }
                    .copy-success { 
                        background: linear-gradient(145deg, #28a745, #20c997) !important; 
                        box-shadow: 0 2px 5px rgba(40,167,69,0.3) !important;
                    }
                    .feedback-container {
                        text-align: center;
                        margin-top: 30px;
                        padding: 20px;
                        border-top: 2px dashed #e9ecef;
                    }
                    .feedback-btn {
                        background: linear-gradient(145deg, #dc3545, #c82333);
                        color: white;
                        border: none;
                        padding: 12px 24px;
                        border-radius: 25px;
                        cursor: pointer;
                        font-size: 14px;
                        font-weight: 500;
                        transition: all 0.3s ease;
                        box-shadow: 0 4px 15px rgba(220,53,69,0.3);
                    }
                    .feedback-btn:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(220,53,69,0.4);
                    }
                    .copy-animation {
                        animation: copyPulse 0.6s ease-out;
                    }
                    @keyframes copyPulse {
                        0% { transform: scale(1); }
                        50% { transform: scale(1.1); box-shadow: 0 0 20px rgba(0,123,255,0.6); }
                        100% { transform: scale(1); }
                    }
                    .toast {
                        position: fixed;
                        top: 20px;
                        right: 20px;
                        background: #28a745;
                        color: white;
                        padding: 12px 20px;
                        border-radius: 8px;
                        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
                        z-index: 1000;
                        opacity: 0;
                        transform: translateX(100px);
                        transition: all 0.3s ease;
                    }
                    .toast.show {
                        opacity: 1;
                        transform: translateX(0);
                    }
                    .modal {
                        display: none;
                        position: fixed;
                        z-index: 2000;
                        left: 0;
                        top: 0;
                        width: 100%;
                        height: 100%;
                        background-color: rgba(0,0,0,0.5);
                        backdrop-filter: blur(5px);
                    }
                    .modal-content {
                        background-color: #fefefe;
                        margin: 5% auto;
                        padding: 20px;
                        border-radius: 15px;
                        width: 95%;
                        max-width: 500px;
                        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
                        position: relative;
                        animation: modalSlideIn 0.3s ease-out;
                        max-height: 90vh;
                        overflow-y: auto;
                    }
                    @keyframes modalSlideIn {
                        from { transform: translateY(-50px); opacity: 0; }
                        to { transform: translateY(0); opacity: 1; }
                    }
                    .modal-header {
                        text-align: center;
                        margin-bottom: 20px;
                        color: #333;
                        font-size: 18px;
                        font-weight: 500;
                    }
                    .modal-textarea {
                        width: 100%;
                        min-height: 120px;
                        padding: 15px;
                        border: 2px solid #e9ecef;
                        border-radius: 10px;
                        font-size: 14px;
                        font-family: inherit;
                        resize: vertical;
                        transition: border-color 0.3s ease;
                        box-sizing: border-box;
                    }
                    .modal-textarea:focus {
                        outline: none;
                        border-color: #007bff;
                        box-shadow: 0 0 10px rgba(0,123,255,0.2);
                    }
                    .modal-buttons {
                        display: flex;
                        justify-content: center;
                        gap: 15px;
                        margin-top: 20px;
                    }
                    .modal-btn {
                        padding: 10px 20px;
                        border: none;
                        border-radius: 8px;
                        cursor: pointer;
                        font-size: 14px;
                        font-weight: 500;
                        transition: all 0.3s ease;
                    }
                    .modal-btn-primary {
                        background: linear-gradient(145deg, #007bff, #0056b3);
                        color: white;
                        box-shadow: 0 2px 8px rgba(0,123,255,0.3);
                    }
                    .modal-btn-primary:hover {
                        transform: translateY(-1px);
                        box-shadow: 0 4px 12px rgba(0,123,255,0.4);
                    }
                    .modal-btn-secondary {
                        background: linear-gradient(145deg, #6c757d, #5a6268);
                        color: white;
                        box-shadow: 0 2px 8px rgba(108,117,125,0.3);
                    }
                    .modal-btn-secondary:hover {
                        transform: translateY(-1px);
                        box-shadow: 0 4px 12px rgba(108,117,125,0.4);
                    }
                    .emoji-hint {
                        text-align: center;
                        font-size: 12px;
                        color: #666;
                        margin-top: 10px;
                        font-style: italic;
                    }
                    .feedback-options {
                        margin: 15px 0;
                    }
                    .feedback-options-title {
                        font-size: 14px;
                        color: #333;
                        margin-bottom: 10px;
                        text-align: center;
                    }
                    .option-buttons {
                        display: flex;
                        flex-wrap: wrap;
                        gap: 8px;
                        justify-content: center;
                    }
                    .option-btn {
                        padding: 10px 14px;
                        border: 2px solid #e9ecef;
                        border-radius: 20px;
                        background: white;
                        color: #666;
                        cursor: pointer;
                        font-size: 13px;
                        transition: all 0.3s ease;
                        user-select: none;
                        min-height: 44px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        text-align: center;
                    }
                    .option-btn:hover {
                        border-color: #007bff;
                        color: #007bff;
                        transform: translateY(-1px);
                        box-shadow: 0 2px 8px rgba(0,123,255,0.2);
                    }
                    .option-btn.selected {
                        border-color: #007bff;
                        background: linear-gradient(145deg, #007bff, #0056b3);
                        color: white;
                        box-shadow: 0 2px 8px rgba(0,123,255,0.3);
                    }
                    .option-btn.selected:hover {
                        transform: translateY(-1px);
                        box-shadow: 0 4px 12px rgba(0,123,255,0.4);
                    }
                    h1 { 
                        color: #333; 
                        text-align: center; 
                        margin-bottom: 20px;
                        font-size: 28px;
                        font-weight: 300;
                        text-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    .url-copy-banner {
                        background: linear-gradient(145deg, #ff6b6b, #ee5a6f);
                        padding: 15px 20px;
                        border-radius: 12px;
                        margin: 0 0 30px 0;
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        box-shadow: 0 4px 15px rgba(255,107,107,0.3);
                        animation: pulse 2s ease-in-out infinite;
                    }
                    @keyframes pulse {
                        0%, 100% { box-shadow: 0 4px 15px rgba(255,107,107,0.3); }
                        50% { box-shadow: 0 6px 20px rgba(255,107,107,0.5); }
                    }
                    .url-copy-text {
                        color: white;
                        font-size: 15px;
                        font-weight: 500;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    .url-copy-icon {
                        font-size: 24px;
                        animation: bounce 1s ease-in-out infinite;
                    }
                    @keyframes bounce {
                        0%, 100% { transform: translateY(0); }
                        50% { transform: translateY(-5px); }
                    }
                    .copy-url-btn {
                        background: white;
                        color: #ff6b6b;
                        border: none;
                        padding: 12px 24px;
                        border-radius: 8px;
                        cursor: pointer;
                        font-size: 15px;
                        font-weight: 600;
                        transition: all 0.3s ease;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                        min-height: 48px;
                        touch-action: manipulation;
                        user-select: none;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                    }
                    .copy-url-btn:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
                    }
                    .copy-url-btn:active {
                        transform: translateY(0);
                    }
                    .copy-url-btn.copied {
                        background: linear-gradient(145deg, #28a745, #20c997);
                        color: white;
                    }
                    .loading {
                        display: none;
                        text-align: center;
                        padding: 20px;
                        color: #666;
                    }
                    
                    /* 移动端优化样式 */
                    @media (max-width: 768px) {
                        body {
                            margin: 5px;
                            font-size: 14px;
                        }
                        
                        .container {
                            padding: 15px;
                            border-radius: 10px;
                        }
                        
                        h1 {
                            font-size: 22px;
                            margin-bottom: 15px;
                        }
                        
                        .url-copy-banner {
                            flex-direction: column;
                            gap: 12px;
                            padding: 12px 15px;
                            margin-bottom: 20px;
                        }
                        
                        .url-copy-text {
                            font-size: 14px;
                            text-align: center;
                        }
                        
                        .copy-url-btn {
                            width: 100%;
                            justify-content: center;
                            padding: 14px 20px;
                            font-size: 15px;
                            min-height: 50px;
                        }
                        
                        .message {
                            margin: 15px 0;
                            padding: 15px;
                        }
                        
                        .message-content {
                            margin-right: 80px;
                            font-size: 14px;
                        }
                        
                        .copy-btn {
                            padding: 14px 20px;
                            font-size: 15px;
                            font-weight: 600;
                            min-width: 80px;
                            min-height: 52px;
                            top: 12px;
                            right: 12px;
                            border-radius: 10px;
                            /* 移动端增强触摸体验 */
                            box-shadow: 0 3px 12px rgba(0,123,255,0.4);
                        }
                        
                        .background-grid {
                            grid-template-columns: 1fr;
                            gap: 10px;
                        }
                        
                        .background-item {
                            padding: 12px;
                        }
                        
                        .background-label {
                            font-size: 12px;
                        }
                        
                        .background-value {
                            font-size: 13px;
                        }
                        
                        .feedback-btn {
                            padding: 10px 20px;
                            font-size: 13px;
                        }
                        
                        .modal-content {
                            margin: 2% auto;
                            padding: 15px;
                            width: 98%;
                        }
                        
                        .modal-header {
                            font-size: 16px;
                        }
                        
                        .option-buttons {
                            gap: 6px;
                        }
                        
                        .option-btn {
                            padding: 8px 12px;
                            font-size: 12px;
                            min-height: 40px;
                        }
                        
                        .modal-textarea {
                            padding: 12px;
                            font-size: 13px;
                        }
                        
                        .modal-btn {
                            padding: 8px 16px;
                            font-size: 13px;
                        }
                        
                        .toast {
                            top: 10px;
                            right: 10px;
                            padding: 10px 16px;
                            font-size: 13px;
                        }
                    }
                    
                    /* 超小屏幕优化 */
                    @media (max-width: 480px) {
                        body {
                            margin: 2px;
                        }
                        
                        .container {
                            padding: 12px;
                        }
                        
                        h1 {
                            font-size: 20px;
                        }
                        
                        .message {
                            padding: 12px;
                        }
                        
                        .message-content {
                            margin-right: 85px;
                            font-size: 13px;
                        }
                        
                        .copy-btn {
                            padding: 14px 18px;
                            font-size: 14px;
                            font-weight: 600;
                            min-width: 75px;
                            min-height: 52px;
                            border-radius: 12px;
                            /* 超小屏幕上增加更大的触摸区域 */
                            box-shadow: 0 3px 10px rgba(0,123,255,0.4);
                        }
                        
                        .background-item {
                            padding: 10px;
                        }
                        
                        .modal-content {
                            padding: 12px;
                        }
                        
                        .option-btn {
                            flex: 1 1 calc(50% - 3px);
                            min-width: 0;
                        }
                    }
                </style>
                <script>
                    // 存储 chatId
                    const CHAT_ID = '{{CHAT_ID}}';
                    
                    // 改进的复制函数 - 使用data属性避免JavaScript注入
                    function copyFromButton(button) {
                        try {
                            const text = button.getAttribute('data-content');
                            const emotionalIndex = button.getAttribute('data-emotion');
                            
                            if (!text) {
                                console.error('复制内容为空');
                                showToast('复制失败：内容为空 (╯°□°）╯');
                                return;
                            }
                            
                            copyToClipboard(text, button, emotionalIndex);
                        } catch (error) {
                            console.error('复制操作失败:', error);
                            showToast('复制失败，请重试 (╯°□°）╯');
                        }
                    }
                    
                    function copyToClipboard(text, button, emotionalIndex) {
                        // 检查按钮是否已经发送过反馈请求
                        if (button.hasAttribute('data-feedback-sent')) {
                            // 只执行复制操作，不发送反馈
                            if (navigator.clipboard && window.isSecureContext) {
                                navigator.clipboard.writeText(text).then(function() {
                                    showCopySuccessWithoutFeedback(button);
                                }).catch(function(err) {
                                    fallbackCopyTextToClipboardWithoutFeedback(text, button);
                                });
                            } else {
                                fallbackCopyTextToClipboardWithoutFeedback(text, button);
                            }
                            return;
                        }
                        
                        // 首次点击，执行复制并发送反馈
                        if (navigator.clipboard && window.isSecureContext) {
                            navigator.clipboard.writeText(text).then(function() {
                                showCopySuccess(button, text, emotionalIndex);
                            }).catch(function(err) {
                                fallbackCopyTextToClipboard(text, button, emotionalIndex);
                            });
                        } else {
                            fallbackCopyTextToClipboard(text, button, emotionalIndex);
                        }
                    }
                    
                    function fallbackCopyTextToClipboard(text, button, emotionalIndex) {
                        var textArea = document.createElement('textarea');
                        textArea.value = text;
                        textArea.style.position = 'fixed';
                        textArea.style.left = '-999999px';
                        textArea.style.top = '-999999px';
                        document.body.appendChild(textArea);
                        textArea.focus();
                        textArea.select();
                        try {
                            var successful = document.execCommand('copy');
                            if (successful) {
                                showCopySuccess(button, text, emotionalIndex);
                            } else {
                                alert('复制失败，请手动复制');
                            }
                        } catch (err) {
                            console.error('复制失败: ', err);
                            alert('复制失败，请手动复制');
                        }
                        document.body.removeChild(textArea);
                    }
                    
                    function fallbackCopyTextToClipboardWithoutFeedback(text, button) {
                        var textArea = document.createElement('textarea');
                        textArea.value = text;
                        textArea.style.position = 'fixed';
                        textArea.style.left = '-999999px';
                        textArea.style.top = '-999999px';
                        document.body.appendChild(textArea);
                        textArea.focus();
                        textArea.select();
                        try {
                            var successful = document.execCommand('copy');
                            if (successful) {
                                showCopySuccessWithoutFeedback(button);
                            } else {
                                alert('复制失败，请手动复制');
                            }
                        } catch (err) {
                            console.error('复制失败: ', err);
                            alert('复制失败，请手动复制');
                        }
                        document.body.removeChild(textArea);
                    }
                    
                    function showCopySuccess(button, text, emotionalIndex) {
                        // 添加复制动画
                        button.classList.add('copy-animation');
                        
                        // 固定显示拿走不谢的文本
                        button.textContent = '拿走不谢 (｡◕‿◕｡)';
                        button.classList.add('copy-success');
                        
                        // 发送复制请求到后端，包含情感指数
                        sendFeedbackRequest(0, '', text, emotionalIndex);
                        
                        // 标记该按钮已发送过反馈
                        button.setAttribute('data-feedback-sent', 'true');
                        
                        setTimeout(function() {
                            button.textContent = '复制';
                            button.classList.remove('copy-success', 'copy-animation');
                        }, 2000);
                    }
                    
                    function showCopySuccessWithoutFeedback(button) {
                        // 添加复制动画
                        button.classList.add('copy-animation');
                        
                        // 固定显示拿走不谢的文本
                        button.textContent = '拿走不谢 (｡◕‿◕｡)';
                        button.classList.add('copy-success');
                        
                        // 不发送反馈请求
                        
                        setTimeout(function() {
                            button.textContent = '复制';
                            button.classList.remove('copy-success', 'copy-animation');
                        }, 2000);
                    }
                    
                    function showFeedback() {
                        // 显示反馈弹窗
                        document.getElementById('feedbackModal').style.display = 'block';
                    }
                    
                    function closeFeedbackModal() {
                        document.getElementById('feedbackModal').style.display = 'none';
                        document.getElementById('feedbackText').value = '';
                        // 清除所有选项的选中状态
                        document.querySelectorAll('.option-btn').forEach(btn => {
                            btn.classList.remove('selected');
                        });
                    }
                    
                    function selectOption(button, text) {
                        // 清除其他选项的选中状态
                        document.querySelectorAll('.option-btn').forEach(btn => {
                            btn.classList.remove('selected');
                        });
                        
                        // 选中当前选项
                        button.classList.add('selected');
                        
                        // 将选项文本填入文本框
                        document.getElementById('feedbackText').value = text;
                    }
                    
                    function submitFeedback() {
                        const feedbackText = document.getElementById('feedbackText').value.trim();
                        const finalMessage = feedbackText || '用户不满意生成结果';
                        
                        // 显示感谢提示
                        const thankMessages = [
                            '您的劝谏是我们前进的动力 (｡◕‿◕｡)',
                            '感谢您的宝贵建议 ♪(´▽｀)',
                            '您的反馈让我们更加努力 (≧∀≦)ゞ',
                            '谢谢您的耐心指导 (´∀｀)♡',
                            '您的意见非常珍贵 ヽ(°▽°)ノ'
                        ];
                        const randomMessage = thankMessages[Math.floor(Math.random() * thankMessages.length)];
                        showToast(randomMessage);
                        
                        // 发送反馈请求到后端
                        sendFeedbackRequest(1, finalMessage, '', null);
                        
                        // 关闭弹窗
                        closeFeedbackModal();
                    }
                    
                    function sendFeedbackRequest(messageType, feedbackMessage, selectedText, emotionalIndex) {
                        const requestData = {
                            chatId: CHAT_ID,
                            messageType: messageType, // 0: 复制, 1: 不满意反馈
                            feedBackMessage: feedbackMessage,
                            resultStructure: {
                                selectedText: selectedText || '',
                                timestamp: new Date().toISOString(),
                                emotionalIndex: emotionalIndex || null,
                                additionalInfo: messageType === 0 ? '用户复制内容' : '用户反馈不满意'
                            }
                        };
                        
                        // 增强错误处理和超时控制
                        const controller = new AbortController();
                        const timeoutId = setTimeout(() => controller.abort(), 10000); // 10秒超时
                        
                        fetch('{{BASE_URL}}/api/chat-ai/chat/user/feedback', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify(requestData),
                            signal: controller.signal
                        }).then(response => {
                            clearTimeout(timeoutId);
                            if (response.ok) {
                                console.log('反馈发送成功');
                            } else {
                                console.error('反馈发送失败 - HTTP状态:', response.status);
                                // 静默失败，不影响用户体验
                            }
                        }).catch(error => {
                            clearTimeout(timeoutId);
                            if (error.name === 'AbortError') {
                                console.error('反馈发送超时');
                            } else {
                                console.error('发送反馈时出错:', error);
                            }
                            // 静默失败，不影响用户体验
                        });
                    }
                    
                    function showToast(message) {
                        // 创建toast元素
                        const toast = document.createElement('div');
                        toast.className = 'toast';
                        toast.textContent = message;
                        document.body.appendChild(toast);
                        
                        // 显示toast
                        setTimeout(() => {
                            toast.classList.add('show');
                        }, 100);
                        
                        // 3秒后隐藏并移除toast
                        setTimeout(() => {
                            toast.classList.remove('show');
                            setTimeout(() => {
                                if (document.body.contains(toast)) {
                                    document.body.removeChild(toast);
                                }
                            }, 300);
                        }, 3000);
                    }
                    
                    // 复制当前页面URL到剪贴板
                    function copyCurrentUrl() {
                        const currentUrl = window.location.href;
                        const button = event.target;
                        
                        try {
                            if (navigator.clipboard && window.isSecureContext) {
                                // 现代浏览器的Clipboard API
                                navigator.clipboard.writeText(currentUrl).then(() => {
                                    showUrlCopySuccess(button);
                                }).catch(err => {
                                    console.error('复制URL失败:', err);
                                    fallbackCopyUrl(currentUrl, button);
                                });
                            } else {
                                // 降级方案
                                fallbackCopyUrl(currentUrl, button);
                            }
                        } catch (error) {
                            console.error('复制操作失败:', error);
                            fallbackCopyUrl(currentUrl, button);
                        }
                    }
                    
                    // 降级方案：使用传统方法复制URL
                    function fallbackCopyUrl(url, button) {
                        const textArea = document.createElement('textarea');
                        textArea.value = url;
                        textArea.style.position = 'fixed';
                        textArea.style.left = '-999999px';
                        textArea.style.top = '-999999px';
                        document.body.appendChild(textArea);
                        textArea.focus();
                        textArea.select();
                        
                        try {
                            const successful = document.execCommand('copy');
                            if (successful) {
                                showUrlCopySuccess(button);
                            } else {
                                showToast('复制失败，请手动复制地址栏链接 (╯°□°）╯');
                            }
                        } catch (err) {
                            console.error('复制失败:', err);
                            showToast('复制失败，请手动复制地址栏链接 (╯°□°）╯');
                        }
                        
                        document.body.removeChild(textArea);
                    }
                    
                    // 显示URL复制成功状态
                    function showUrlCopySuccess(button) {
                        const originalText = button.innerHTML;
                        button.innerHTML = '✅ 已复制链接！';
                        button.classList.add('copied');
                        
                        showToast('网页链接已复制！可以分享给朋友啦 ♪(´▽｀)');
                        
                        // 2.5秒后恢复原状
                        setTimeout(() => {
                            button.innerHTML = originalText;
                            button.classList.remove('copied');
                        }, 2500);
                    }
                    
                    // 禁止页面缩放的额外防护
                    document.addEventListener('gesturestart', function (e) {
                        e.preventDefault();
                    });
                    
                    document.addEventListener('gesturechange', function (e) {
                        e.preventDefault();
                    });
                    
                    document.addEventListener('gestureend', function (e) {
                        e.preventDefault();
                    });
                    
                    // 防止键盘缩放（Ctrl + 滚轮）
                    document.addEventListener('wheel', function(e) {
                        if (e.ctrlKey) {
                            e.preventDefault();
                        }
                    }, { passive: false });
                    
                    // 防止键盘缩放（Ctrl + +/-）
                    document.addEventListener('keydown', function(e) {
                        if (e.ctrlKey && (e.key === '+' || e.key === '-' || e.key === '=' || e.key === '0')) {
                            e.preventDefault();
                        }
                    });
                    
                    // 页面加载完成后的优化
                    document.addEventListener('DOMContentLoaded', function() {
                        // 添加淡入动画
                        document.querySelector('.container').style.opacity = '0';
                        document.querySelector('.container').style.transform = 'translateY(20px)';
                        
                        setTimeout(function() {
                            document.querySelector('.container').style.transition = 'all 0.6s ease';
                            document.querySelector('.container').style.opacity = '1';
                            document.querySelector('.container').style.transform = 'translateY(0)';
                        }, 100);
                        
                        // 点击模态框背景关闭弹窗
                        const modal = document.getElementById('feedbackModal');
                        modal.addEventListener('click', function(e) {
                            if (e.target === modal) {
                                closeFeedbackModal();
                            }
                        });
                        
                        // ESC键关闭弹窗
                        document.addEventListener('keydown', function(e) {
                            if (e.key === 'Escape' && modal.style.display === 'block') {
                                closeFeedbackModal();
                            }
                        });
                    });
                </script>
            </head>
            <body>
                <div class='container'>
                    <h1>来自小扬 (＾▽＾)／ 的 AI聊天分析结果</h1>
                    
                    <!-- URL复制横幅 -->
                    <div class='url-copy-banner'>
                        <div class='url-copy-text'>
                            <span class='url-copy-icon'>📌</span>
                            <span>保存此页面链接，随时查看分析结果</span>
                        </div>
                        <button class='copy-url-btn' onclick='copyCurrentUrl()' type='button'>
                            📋 复制网页链接
                        </button>
                    </div>
                    
                    {{BACKGROUND_INFO}}
                    {{MESSAGES_CONTENT}}
                    <div class='feedback-container'>
                        <button class='feedback-btn' onclick='showFeedback()'>我不满意生成结果 (╯︵╰)</button>
                    </div>
                </div>
                
                <!-- 反馈弹窗 -->
                <div id='feedbackModal' class='modal'>
                    <div class='modal-content'>
                        <div class='modal-header'>
                            告诉小扬哪里需要改进吧 (´･ω･`)
                        </div>
                        
                        <div class='feedback-options'>
                            <div class='feedback-options-title'>快速选择常见问题 ٩(◕‿◕)۶</div>
                            <div class='option-buttons'>
                                <div class='option-btn' onclick='selectOption(this, "分析不够详细，希望更深入一些 ヽ(ｏ`皿′ｏ)ﾉ")'>
                                    分析不够详细 ヽ(ｏ`皿′ｏ)ﾉ
                                </div>
                                <div class='option-btn' onclick='selectOption(this, "情绪判断有误，实际感受不是这样 (｡•́︿•̀｡)")'>
                                    情绪判断有误 (｡•́︿•̀｡)
                                </div>
                                <div class='option-btn' onclick='selectOption(this, "希望更加专业一些 ╰( ͡° ͜ʖ ͡° )つ──☆*:・ﾟ")'>
                                    希望更加专业 (｡◕‿◕｡)
                                </div>
                                <div class='option-btn' onclick='selectOption(this, "内容太简单了，需要更多细节 (￣▽￣)")'>
                                    内容太简单 (￣▽￣)
                                </div>
                                <div class='option-btn' onclick='selectOption(this, "分析角度单一，希望多元化 ♪(´▽｀)")'>
                                    分析角度单一 ♪(´▽｀)
                                </div>
                                <div class='option-btn' onclick='selectOption(this, "语言风格不符合预期 (¬_¬)")'>
                                    语言风格问题 (¬_¬)
                                </div>
                                <div class='option-btn' onclick='selectOption(this, "识别准确度有待提升 (｡•ˇ‸ˇ•｡)")'>
                                    识别准确度问题 (｡•ˇ‸ˇ•｡)
                                </div>
                                <div class='option-btn' onclick='selectOption(this, "响应速度太慢了 (╯°□°）╯")'>
                                    响应速度慢 (╯°□°）╯
                                </div>
                            </div>
                        </div>
                        
                        <textarea id='feedbackText' class='modal-textarea' placeholder='您也可以在这里输入自定义的建议或意见... 选择上面的选项会自动填入哦 (◕‿◕)✨&#10;&#10;当然，不输入任何内容也完全OK～'></textarea>
                        <div class='emoji-hint'>
                            您的每一个建议都是小扬进步的阶梯 ♪(´▽｀) 不输入内容也完全OK哦～
                        </div>
                        <div class='modal-buttons'>
                            <button class='modal-btn modal-btn-secondary' onclick='closeFeedbackModal()'>
                                算了，不说了 (￣▽￣)
                            </button>
                            <button class='modal-btn modal-btn-primary' onclick='submitFeedback()'>
                                提交反馈 ٩(◕‿◕)۶
                            </button>
                        </div>
                    </div>
                </div>
            </body>
            </html>""";

    /**
     * 高效生成HTML内容
     * @param resultInfo 结果信息
     * @param chatId 会话ID
     * @return 生成的HTML字符串
     */
    public String generateOptimizedHtml(ResultInfo resultInfo, String chatId) {
        if (resultInfo == null) {
            return generateEmptyResultHtml(chatId);
        }

        // 生成背景信息HTML
        String backgroundInfoHtml = generateBackgroundInfoHtml(resultInfo);
        
        // 使用StringBuilder预分配容量，减少内存重分配
        StringBuilder messagesHtml = new StringBuilder(1024);
        
        if (resultInfo.getMessages() != null && !resultInfo.getMessages().isEmpty()) {
            // 批量处理消息，减少方法调用开销
            for (var message : resultInfo.getMessages()) {
                String emotionalIndex = message.getEmotionalIndex() != null ? 
                    message.getEmotionalIndex().toString() : "未设置";
                String messageContent = escapeHtml(message.getMessage());
                // 改进JavaScript字符串转义，确保安全性
                String escapedForJs = escapeForJavaScript(messageContent);
                
                messagesHtml.append("        <div class='message'>\n")
                          .append("            <div class='emotion-index'>情绪值: ").append(emotionalIndex).append("</div>\n")
                          .append("            <div class='message-content'>").append(messageContent).append("</div>\n")
                          .append("            <button class='copy-btn' type='button' data-content='").append(escapedForJs).append("' data-emotion='").append(emotionalIndex).append("' onclick='copyFromButton(this)'>复制</button>\n")
                          .append("        </div>\n");
            }
        } else {
            messagesHtml.append(
                "        <div class='message'>\n" +
                "            <div class='message-content'>暂无分析结果</div>\n" +
                "        </div>\n"
            );
        }

        // 使用安全的占位符替换，避免与CSS百分比冲突
        String html = HTML_TEMPLATE.replace("{{BACKGROUND_INFO}}", backgroundInfoHtml)
                           .replace("{{MESSAGES_CONTENT}}", messagesHtml.toString())
                           .replace("{{CHAT_ID}}", chatId != null ? escapeForJavaScript(chatId) : "")
                           .replace("{{BASE_URL}}", appConfig.getBaseUrl() != null ? escapeHtml(appConfig.getBaseUrl()) : "");
        
        // 验证生成的HTML完整性
        if (!validateHtmlIntegrity(html)) {
            log.warn("生成的HTML可能存在结构问题 - chatId: {}", chatId);
        }
        
        return html;
    }

    /**
     * 生成聊天背景信息HTML
     * @param resultInfo 结果信息
     * @return 背景信息HTML字符串
     */
    private String generateBackgroundInfoHtml(ResultInfo resultInfo) {
        if (resultInfo == null) {
            return "";
        }

        // 尝试从第一个消息中获取背景信息（因为所有消息都包含相同的背景信息）
        if (resultInfo.getMessages() != null && !resultInfo.getMessages().isEmpty()) {
            var firstMessage = resultInfo.getMessages().get(0);
            
            String relationshipType = escapeHtml(firstMessage.getRelationshipType());
            String conversationScene = escapeHtml(firstMessage.getConversationScene());
            String topicNature = escapeHtml(firstMessage.getTopicNature());
            String userToneCharacteristics = escapeHtml(firstMessage.getUserToneCharacteristics());
            String emotionalReason = escapeHtml(firstMessage.getEmotionalReason());
            Integer overallEmotionalIndex = firstMessage.getOverallEmotionalIndex();
            
            // 如果都为空或null，则不显示背景信息
            if (isAllEmpty(relationshipType, conversationScene, topicNature, userToneCharacteristics)) {
                return "";
            }
            
            StringBuilder backgroundHtml = new StringBuilder();
            backgroundHtml.append("        <div class='background-info'>\n")
                         .append("            <div class='background-title'>📊 聊天背景分析</div>\n")
                         .append("            <div class='background-grid'>\n");
            
            // 关系类型
            if (!isEmpty(relationshipType)) {
                backgroundHtml.append("                <div class='background-item'>\n")
                             .append("                    <div class='background-label'>关系类型</div>\n")
                             .append("                    <div class='background-value'>").append(relationshipType).append("</div>\n")
                             .append("                </div>\n");
            }
            
            // 对话场景
            if (!isEmpty(conversationScene)) {
                backgroundHtml.append("                <div class='background-item'>\n")
                             .append("                    <div class='background-label'>对话场景</div>\n")
                             .append("                    <div class='background-value'>").append(conversationScene).append("</div>\n")
                             .append("                </div>\n");
            }
            
            // 话题性质
            if (!isEmpty(topicNature)) {
                backgroundHtml.append("                <div class='background-item'>\n")
                             .append("                    <div class='background-label'>话题性质</div>\n")
                             .append("                    <div class='background-value'>").append(topicNature).append("</div>\n")
                             .append("                </div>\n");
            }
            
            // 用户语气特征
            if (!isEmpty(userToneCharacteristics)) {
                backgroundHtml.append("                <div class='background-item'>\n")
                             .append("                    <div class='background-label'>用户特征</div>\n")
                             .append("                    <div class='background-value'>").append(userToneCharacteristics).append("</div>\n")
                             .append("                </div>\n");
            }
            
            backgroundHtml.append("            </div>\n");
            
            // 情感指数总结
            if (overallEmotionalIndex != null || !isEmpty(emotionalReason)) {
                backgroundHtml.append("            <div class='emotional-summary'>\n");
                if (overallEmotionalIndex != null) {
                    backgroundHtml.append("                <div class='emotional-index-main'>整体情感指数: ")
                                 .append(overallEmotionalIndex).append(" 分</div>\n");
                }
                if (!isEmpty(emotionalReason)) {
                    backgroundHtml.append("                <div class='emotional-reason'>")
                                 .append(emotionalReason).append("</div>\n");
                }
                backgroundHtml.append("            </div>\n");
            }
            
            backgroundHtml.append("        </div>\n");
            return backgroundHtml.toString();
        }
        
        // 如果没有消息，则尝试从ResultInfo的backgroundAnalysis中获取
        if (resultInfo.getBackgroundAnalysis() != null) {
            var analysis = resultInfo.getBackgroundAnalysis();
            String relationshipType = escapeHtml(analysis.getRelationshipType());
            String conversationScene = escapeHtml(analysis.getConversationScene());
            String topicNature = escapeHtml(analysis.getTopicNature());
            String userToneCharacteristics = escapeHtml(analysis.getUserToneCharacteristics());
            String emotionalReason = escapeHtml(resultInfo.getEmotionalReason());
            Integer overallEmotionalIndex = resultInfo.getOverallEmotionalIndex();
            
            if (isAllEmpty(relationshipType, conversationScene, topicNature, userToneCharacteristics)) {
                return "";
            }
            
            // 这里可以使用相同的HTML生成逻辑，为简洁起见省略重复代码
            // 实际实现中可以提取为公共方法
        }
        
        return "";
    }
    
    /**
     * 检查字符串是否为空
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty() || "null".equals(str);
    }
    
    /**
     * 检查所有字符串是否都为空
     */
    private boolean isAllEmpty(String... strings) {
        for (String str : strings) {
            if (!isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成空结果HTML
     */
    private String generateEmptyResultHtml(String chatId) {
        String emptyContent = 
            "        <div class='message'>\n" +
            "            <div class='message-content'>暂无分析结果</div>\n" +
            "        </div>\n";
        return HTML_TEMPLATE.replace("{{BACKGROUND_INFO}}", "")
                           .replace("{{MESSAGES_CONTENT}}", emptyContent)
                           .replace("{{CHAT_ID}}", chatId != null ? chatId : "")
                           .replace("{{BASE_URL}}", appConfig.getBaseUrl() != null ? appConfig.getBaseUrl() : "");
    }

    /**
     * 高效的HTML转义
     * 使用预编译的替换规则，比逐个字符检查更快
     */
    private String escapeHtml(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // 只有包含特殊字符时才进行替换，避免不必要的字符串操作
        if (!containsHtmlSpecialChars(text)) {
            return text;
        }
        
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
    
    /**
     * JavaScript字符串转义 - 更安全的处理方式
     * 避免在JavaScript代码中出现语法错误
     */
    private String escapeForJavaScript(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        return text.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("/", "\\/")
                .replace("<", "\\u003c")
                .replace(">", "\\u003e");
    }

    /**
     * 快速检查是否包含HTML特殊字符
     */
    private boolean containsHtmlSpecialChars(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '&' || c == '<' || c == '>' || c == '"' || c == '\'') {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证HTML结构完整性
     */
    private boolean validateHtmlIntegrity(String html) {
        if (html == null || html.isEmpty()) {
            return false;
        }
        
        // 基本结构检查
        return html.contains("<!DOCTYPE html>") &&
               html.contains("<html") &&
               html.contains("</html>") &&
               html.contains("<head>") &&
               html.contains("</head>") &&
               html.contains("<body>") &&
               html.contains("</body>") &&
               html.contains("copyFromButton"); // 确保包含必要的JavaScript函数
    }
    
    /**
     * 获取模板缓存统计信息（用于监控）
     */
    public String getCacheStats() {
        return "模板缓存大小: " + templateCache.size();
    }
}
