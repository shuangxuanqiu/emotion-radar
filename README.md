# 小扬情感雷达 - AI聊天助手

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-green.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue.svg)](https://spring.io/projects/spring-ai)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 🚀 一个基于 AI 的智能聊天回复助手，通过苹果快捷指令与聊天界面截图分析，为你提供个性化的回复建议，告别尬聊困扰！

## ✨ 项目特色

### 🎯 核心功能
- **智能截图分析**：基于 AI 视觉模型分析各类聊天软件界面
- **情感雷达系统**：深度理解对话情境和情感背景
- **个性化回复建议**：生成符合语境的多样化回复选项
- **苹果快捷指令集成**：三次轻敲背部即可启动分析

### 🔥 应用场景
- **社交困难救星**：不知道怎么回复时的智能助手
- **尬聊终结者**：提供自然流畅的对话建议
- **情感理解**：分析对话背景和情绪状态
- **多平台支持**：支持微信、QQ、钉钉、飞书等主流聊天软件

## 🚀 快速开始

### 📱 苹果快捷指令配置

1. **下载快捷指令**
   - 访问：[快捷指令下载链接](https://www.icloud.com/shortcuts/7b2052379bd24d95a4cc08ae6e5bc3cf)
   - 点击获取快捷指令并添加到快捷指令 App

2. **设置轻敲背部触发**
   - 打开 iPhone「设置」→「辅助功能」→「触控」
   - 选择「轻点背面」→「轻点三下」
   - 选择刚刚添加的快捷指令

3. **使用方法**
   - 在任意聊天界面截图或直接在聊天界面
   - 轻敲手机背部三次
   - 等待 AI 分析并获得回复建议（约28秒）

### 🛠 本地部署

#### 环境要求
- Java 21+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

#### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/your-username/my-ai-agent.git
cd my-ai-agent
```

2. **配置数据库**
```sql
CREATE DATABASE ggy_picture CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **配置环境变量**
```bash
# 复制配置文件
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml

# 编辑配置文件，填入你的配置信息
vim src/main/resources/application-local.yml
```

4. **启动服务**
```bash
# 启动 Redis
redis-server

# 启动 MySQL
mysql.server start

# 编译并运行项目
mvn clean package -DskipTests
java -jar target/chat-ai-agent-0.0.1-SNAPSHOT.jar
```

5. **访问服务**
   - API 文档：http://localhost:8123/api/swagger-ui.html
   - 前端界面：http://localhost:8123

## 🏗 技术架构

### 后端技术栈
- **框架**：Spring Boot 3.4.4
- **AI 引擎**：Spring AI + 阿里云百练 (DashScope)
- **数据库**：MySQL 8.0 + MyBatis-Flex
- **缓存**：Redis + Redisson
- **图像处理**：Java AWT + ImageIO
- **文档**：Knife4j (Swagger)

### 前端技术栈
- **框架**：Vue 3 + TypeScript
- **构建工具**：Vite
- **UI 组件**：Element Plus
- **路由**：Vue Router 4
- **状态管理**：Pinia

### AI 能力
- **多模态理解**：文本 + 图像联合分析
- **对话记忆**：基于 Redis 的会话状态管理
- **智能工具调用**：支持多种 AI 工具集成
- **结构化输出**：JSON Schema 约束的响应格式

## 📊 性能优化

### 图像处理优化
- **智能压缩**：根据图片尺寸自动调整压缩策略
- **格式优化**：JPEG 高质量压缩，减少传输时间
- **分辨率适配**：关闭高分辨率模式，提升处理速度

### 响应时间优化
- **并行处理**：图像压缩与 AI 分析并行执行
- **缓存策略**：Redis 缓存常用分析结果
- **连接池**：数据库连接池优化
- **当前平均响应时间**：~28秒

## 🔧 API 接口

### 核心接口

#### 图像分析
```http
POST /api/image/analysis
Content-Type: multipart/form-data

参数:
- file: 聊天界面截图
- prompt: 分析提示词（可选）
- chatId: 会话ID
```

#### 文本聊天
```http
POST /api/chat
Content-Type: application/json

{
  "message": "用户消息",
  "chatId": "会话ID"
}
```

#### 情感雷达分析
```http
GET /api/emotion-radar/{chatId}
```

更多 API 详情请查看：[API 文档](http://localhost:8123/api/swagger-ui.html)

## 🎨 功能特性

### 智能图像识别
- **多平台支持**：微信、QQ、钉钉、飞书、Telegram 等
- **消息类型识别**：文字、图片、语音、视频、文件、红包、转账等
- **对话角色区分**：准确识别发送者和接收者
- **时间信息提取**：识别消息时间戳

### 情感分析引擎
- **情境理解**：分析对话背景和情感状态
- **个性化建议**：根据聊天风格生成匹配的回复
- **多样性保证**：提供多种回复选项供选择
- **语言自然性**：生成自然流畅的对话内容

### 系统监控
- **性能监控**：实时监控 AI 模型调用性能
- **错误追踪**：完整的错误日志和异常处理
- **使用统计**：用户使用情况和功能统计
- **缓存监控**：Redis 缓存命中率监控

## 📱 客户端支持

### 苹果快捷指令
- **一键启动**：轻敲背部三次即可使用
- **截图自动上传**：自动获取当前屏幕截图
- **结果语音播报**：支持语音播报分析结果
- **历史记录**：保存最近的分析历史

### Web 界面
- **响应式设计**：支持桌面端和移动端
- **实时预览**：上传图片实时预览分析结果
- **历史管理**：查看和管理聊天记录
- **设置中心**：个性化配置选项

## 🔒 隐私安全

- **本地部署**：支持完全本地化部署，数据不出本地
- **图片加密**：上传图片采用加密传输
- **会话隔离**：不同用户会话完全隔离
- **数据清理**：定期清理临时文件和缓存数据
- **访问控制**：支持 API 访问权限控制

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 开发环境设置
1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/your-feature`
3. 提交更改：`git commit -am 'Add some feature'`
4. 推送分支：`git push origin feature/your-feature`
5. 提交 Pull Request

### 代码规范
- 遵循 Google Java Style Guide
- 使用 Lombok 简化代码
- 添加必要的单元测试
- 更新相关文档

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🙏 致谢

- [Spring AI](https://spring.io/projects/spring-ai) - 强大的 AI 集成框架
- [阿里云百练](https://www.aliyun.com/product/bailian) - 优秀的大语言模型服务
- [苹果快捷指令](https://support.apple.com/zh-cn/guide/shortcuts/welcome/ios) - 便捷的自动化工具

## 📞 联系我们

- **邮箱联系**：2694732783@qq.com
- **项目主页**：https://github.com/your-username/my-ai-agent
- **问题反馈**：https://github.com/your-username/my-ai-agent/issues
- **讨论交流**：https://github.com/your-username/my-ai-agent/discussions

---

<div align="center">
  <strong>让 AI 成为你的聊天助手，告别尬聊时代！</strong>
  <br>
  <sub>如果这个项目对你有帮助，请给个 ⭐️ Star 支持一下！</sub>
</div>
