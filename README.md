# 🎯 小扬情感雷达 - AI聊天助手

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-green.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue.svg)](https://spring.io/projects/spring-ai)
[![Vue 3](https://img.shields.io/badge/Vue-3.5.18-green.svg)](https://vuejs.org/)
[![Ant Design Vue](https://img.shields.io/badge/Ant%20Design%20Vue-4.2.6-blue.svg)](https://antdv.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 🚀 一个基于 AI 的智能聊天回复助手，通过苹果快捷指令与聊天界面截图分析，为你提供个性化的回复建议，告别尬聊困扰！
> 
> 📱 **核心特色**：三次轻敲手机背部 → 截图上传 → AI智能分析 → 生成个性化回复建议（约28秒完成）

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

#### 📋 环境要求

**必需环境**
- ☕ **Java 21+** - 核心运行环境
- 🗄️ **MySQL 8.0+** - 主数据库
- 🔴 **Redis 6.0+** - 缓存和会话管理
- 📦 **Maven 3.8+** - 构建工具
- 🌐 **Node.js 20.19+** - 前端构建环境

**可选环境**
- 📊 **PgVector** - 向量数据库（如需RAG功能）
- 🔧 **MCP 服务器** - 扩展工具调用（如高德地图API）

#### 🚀 快速启动

**1. 克隆项目**
```bash
git clone https://github.com/your-username/my-ai-agent.git
cd my-ai-agent
```

**2. 数据库初始化**
```sql
# 创建数据库
CREATE DATABASE ggy_picture CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户（可选）
CREATE USER 'ggy_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ggy_picture.* TO 'ggy_user'@'localhost';
FLUSH PRIVILEGES;
```

**3. 核心配置**
```bash
# 复制配置模板
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml

# 编辑配置文件
vim src/main/resources/application-local.yml
```

**必需配置项**：
```yaml
spring:
  ai:
    dashscope:
      api-key: sk-your-dashscope-api-key  # 阿里云通义千问API密钥
  datasource:
    password: your-database-password      # MySQL数据库密码
    
search-api:
  api-key: your-search-api-key           # SearchAPI密钥（可选）
```

**4. 服务启动**
```bash
# 启动基础服务
redis-server                           # 启动Redis
mysql.server start                     # 启动MySQL

# 后端服务
mvn clean package -DskipTests
java -jar target/chat-ai-agent-0.0.1-SNAPSHOT.jar

# 前端服务（新终端）
cd ggy-chat-ai-agent/ggy-chat-ai-agent
npm install
npm run dev
```

**5. 访问验证**
- 🔍 **API文档**: http://localhost:8123/api/swagger-ui.html
- 🎨 **前端界面**: http://localhost:5173
- ❤️ **健康检查**: http://localhost:8123/api/chat-ai/health

## 🏗 技术架构

### 🔧 后端技术栈
- **核心框架**：Spring Boot 3.4.4 + Java 21
- **AI 引擎**：Spring AI 1.0.0 + 阿里云百练 DashScope
- **视觉模型**：通义千问 VL (qwen-vl-max) - 多模态图像理解
- **数据存储**：MySQL 8.0 + MyBatis-Flex 1.11.1
- **缓存系统**：Redis 6.0+ + Redisson 3.50.0
- **图像处理**：智能压缩算法 + JPEG 质量优化
- **API 文档**：Knife4j 4.5.0 (基于 OpenAPI 3)
- **工具集成**：Web搜索、PDF生成、终端操作、文件处理等

### 🎨 前端技术栈
- **核心框架**：Vue 3.5.18 + TypeScript 5.8+
- **构建工具**：Vite 7.0.6 + Node.js 20.19+
- **UI 组件库**：Ant Design Vue 4.2.6
- **数据可视化**：ECharts 5.6.0 + Vue-ECharts 7.0.3
- **路由管理**：Vue Router 4.5.1
- **状态管理**：Pinia 3.0.3
- **HTTP 客户端**：Axios 1.12.1
- **代码质量**：ESLint + Prettier

### 🧠 AI 核心能力
- **多模态分析**：文本 + 图像联合理解，支持微信/QQ/钉钉等主流聊天软件
- **情感雷达系统**：深度分析对话情境、关系类型、情感背景
- **智能提示词工程**：409行专业提示词，确保回复的原创性和真人化
- **对话记忆管理**：基于 Redis 的会话状态持久化
- **工具调用链**：Web搜索、PDF处理、终端操作等多种工具集成
- **结构化输出**：JSON Schema 约束，确保响应格式一致性
- **性能优化**：图像智能压缩、并行处理、缓存策略

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

## 🔧 API 接口文档

### 🎯 核心业务接口

#### 1. 情感雷达核心服务（推荐）
```http
POST /api/chat-ai/emotion-radar
Content-Type: multipart/form-data

参数:
- file: 聊天界面截图 (必需)
- emotionalIndex: 情感指数 1-10 (必需)
- conversationScene: 聊天背景描述 (必需)

返回: HTML格式的可视化分析报告
```

#### 2. JSON格式图片分析
```http
POST /api/chat-ai/is-json
Content-Type: multipart/form-data

参数:
- file: 聊天界面截图
- emotionalIndex: 情感指数

返回: JSON格式的分析结果
```

#### 3. SSE流式对话
```http
GET /api/chat-ai/travel_guide/chat/sse/emitter
参数:
- message: 用户消息
- chatId: 会话ID

返回: Server-Sent Events 流
```

### 📊 管理接口

#### 系统健康检查
```http
GET /api/chat-ai/health

返回: 系统状态、缓存统计、活跃连接数等
```

#### 获取聊天记录
```http
GET /api/chat-ai/chat/memory/redis?chatId={chatId}

返回: Redis中的聊天历史记录
```

#### 用户反馈提交
```http
POST /api/chat-ai/chat/user/feedback
Content-Type: application/json

{
  "chatId": "会话ID",
  "messageType": "反馈类型",
  "feedBackMessage": "反馈内容",
  "resultStructure": {
    "selectedText": "选择的文本",
    "timestamp": "时间戳",
    "emotionalIndex": 5
  }
}
```

### 🛠 数据管理接口

#### 图片解析记录管理
```http
# 分页查询
POST /api/imageAnalysis/list/page/vo
Content-Type: application/json

# 查看详情
GET /api/imageAnalysis/getInfo/{id}

# 图片展示
GET /api/imageAnalysis/display/**
```

### 📈 完整API文档
- **Swagger UI**: http://localhost:8123/api/doc.htm

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

- **🏠 本地部署优先**：支持完全本地化部署，数据不出本地环境
- **🔐 传输加密**：HTTPS + 图片上传加密传输，保护数据安全
- **🚪 会话隔离**：基于 chatId 的完全隔离，不同用户数据互不干扰
- **🗑️ 自动清理**：定期清理临时文件、缓存数据和过期会话
- **🛡️ 访问控制**：API 接口权限控制，防止未授权访问
- **📊 数据脱敏**：敏感信息自动脱敏处理，保护用户隐私
- **⚡ 内存安全**：ThreadLocal 上下文自动清理，防止内存泄漏

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

## 📊 项目配置详解

### 🔑 API密钥获取

#### 阿里云通义千问 (必需)
1. 访问 [阿里云百练控制台](https://dashscope.console.aliyun.com/)
2. 注册并实名认证阿里云账户
3. 开通 DashScope 服务
4. 创建 API Key，复制到配置文件中

#### SearchAPI (可选)
1. 访问 [SearchAPI官网](https://www.searchapi.io/)
2. 注册账户并获取 API Key
3. 配置到 `search-api.api-key`

#### 高德地图 MCP (可选)
1. 访问 [高德开放平台](https://lbs.amap.com/)
2. 申请 Web 服务 API Key
3. 配置到 `mcp-servers.json` 中

### ⚙️ 高级配置

#### 性能调优
```yaml
# 数据库连接池优化
spring:
  datasource:
    hikari:
      maximum-pool-size: 20      # 最大连接数
      minimum-idle: 5            # 最小空闲连接
      idle-timeout: 300000       # 空闲超时时间
      connection-timeout: 20000   # 连接超时时间

# AI模型配置
spring:
  ai:
    dashscope:
      chat-vl:
        options:
          model: qwen-vl-max      # 视觉模型
          temperature: 0.6        # 创造性参数
```

#### 日志配置
```yaml
logging:
  level:
    org.springframework.ai: DEBUG     # AI调用日志
    cn.chat.ggy.chataiagent: INFO    # 应用日志
  file:
    name: logs/ggy-ai-agent.log      # 日志文件路径
```

### 🚀 生产环境部署

#### Docker 部署 (推荐)
```dockerfile
# 后续版本将提供 Docker 支持
# 敬请期待...
```

#### 环境变量配置
```bash
# 生产环境建议使用环境变量
export DASHSCOPE_API_KEY=your-api-key
export DATABASE_PASSWORD=your-password
export REDIS_PASSWORD=your-redis-password
```

## 📞 联系我们

- **📧 邮箱联系**：2694732783@qq.com
- **💬 技术交流**：欢迎提交Issue讨论技术问题
- **🔧 功能建议**：通过Issue提出新功能需求
---

<div align="center">
  <strong>让 AI 成为你的聊天助手，告别尬聊时代！</strong>
  <br>
  <sub>如果这个项目对你有帮助，请给个 ⭐️ Star 支持一下！</sub>
</div>
