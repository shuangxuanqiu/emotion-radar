# 配置说明文档

## 配置文件结构

本项目使用 Spring Boot 的多环境配置机制：

- `application.yml` - 主配置文件（会上传到远程仓库）
- `application-local.yml` - 本地环境配置（不会上传到远程仓库）
- `application-local.yml.example` - 本地配置示例文件
- `mcp-servers.json` - MCP 服务配置（不会上传到远程仓库）
- `mcp-servers.json.example` - MCP 服务配置示例文件

## 快速开始

### 1. 配置本地环境

```bash
# 复制示例配置文件
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml

# 如果需要使用 MCP 服务，也复制 MCP 配置文件
cp src/main/resources/mcp-servers.json.example src/main/resources/mcp-servers.json
```

### 2. 修改配置信息

编辑 `src/main/resources/application-local.yml` 文件，配置以下必要信息：

#### 必需配置

1. **阿里云通义千问 API Key**

   ```yaml
   spring:
     ai:
       dashscope:
         api-key: your-dashscope-api-key-here
   ```

   获取地址: <https://dashscope.console.aliyun.com/>

2. **数据库密码**

   ```yaml
   spring:
     datasource:
       password: your-database-password
   ```

3. **Search API Key** (如果使用搜索功能)

   ```yaml
   search-api:
     api-key: your-search-api-key-here
   ```

4. **MCP 服务配置** (如果使用 MCP 功能)

   ```bash
   # 复制 MCP 服务配置文件
   cp src/main/resources/mcp-servers.json.example src/main/resources/mcp-servers.json
   ```

   然后编辑 `src/main/resources/mcp-servers.json` 文件，配置你的 API keys

#### 可选配置

1. **Redis 密码** (如果 Redis 需要认证)

   ```yaml
   spring:
     data:
       redis:
         password: your-redis-password
   ```

2. **自定义端口**

   ```yaml
   server:
     port: 12844
   ```

3. **应用基础 URL**

   ```yaml
   app:
     base-url: http://your-server-domain:port
   ```

## 环境依赖

### 必需服务

- MySQL 数据库 (默认端口 3306)
- Redis 服务 (默认端口 6379)

### 可选服务

- MCP 服务 (如需使用 MCP 功能)
- PgVector 数据库 (如需使用向量搜索功能)

## 安全注意事项

1. **永远不要将 `application-local.yml` 和 `mcp-servers.json` 文件提交到版本控制系统**
2. **API Key 和密码等敏感信息只配置在本地配置文件中**
3. **生产环境部署时，请使用环境变量或安全的配置管理工具**
4. **项目的 `.gitignore` 文件已配置忽略这些敏感文件**

## 故障排除

### 常见问题

1. **API Key 无效**
   - 检查 API Key 是否正确
   - 确认 API Key 权限和余额

2. **数据库连接失败**
   - 检查数据库是否启动
   - 确认用户名、密码和数据库名称

3. **Redis 连接失败**
   - 检查 Redis 服务是否启动
   - 确认端口和密码配置

4. **MCP 服务连接失败**
   - 检查 `mcp-servers.json` 文件是否存在
   - 确认 API Key 配置是否正确
   - 检查网络连接是否正常
   - 确认 Node.js 和 npm 环境是否正确安装

### 日志配置

开发环境建议使用 DEBUG 级别查看详细日志：

```yaml
logging:
  level:
    org.springframework.ai: DEBUG
```

生产环境建议使用 INFO 级别：

```yaml
logging:
  level:
    org.springframework.ai: INFO
```

## 联系支持

如果遇到配置问题，请检查：

1. 配置文件格式是否正确 (YAML 语法)
2. 所有必需的服务是否正常运行
3. 网络连接是否正常
4. API Key 是否有效且有足够权限
