# CompletableFuture 异常处理流程图

## 三层异常处理机制

```
┌─────────────────────────────────────────────────────────────┐
│                     主流程开始                                 │
└─────────────────────┬───────────────────────────────────────┘
                      │
          ┌───────────┴────────────┐
          │                        │
          ▼                        ▼
┌──────────────────┐    ┌──────────────────┐
│  异步任务 1       │    │  异步任务 2       │
│  图片分析         │    │  路由判断         │
│                  │    │                  │
│ supplyAsync()    │    │ supplyAsync()    │
└────────┬─────────┘    └────────┬─────────┘
         │                       │
         │ 【第一层异常处理】      │
         ▼                       ▼
┌──────────────────┐    ┌──────────────────┐
│ exceptionally()  │    │ exceptionally()  │
│ 失败→返回null     │    │ 失败→返回DEFAULT  │
└────────┬─────────┘    └────────┬─────────┘
         │                       │
         └───────────┬───────────┘
                     │
                     ▼
         ┌────────────────────────┐
         │    thenCombine()       │
         │  组合两个任务的结果      │
         │                        │
         │  - 保存数据库           │
         │  - 返回组合结果         │
         └───────────┬────────────┘
                     │
                     │ 【第二层异常处理】
                     ▼
         ┌────────────────────────┐
         │   orTimeout(30s)       │
         │   设置超时时间          │
         └───────────┬────────────┘
                     │
                     ▼
         ┌────────────────────────┐
         │  exceptionally()       │
         │  处理组合/超时异常      │
         │  失败→返回默认结果      │
         └───────────┬────────────┘
                     │
                     │ 【第三层异常处理】
                     ▼
         ┌────────────────────────┐
         │      handle()          │
         │   最终异常保底处理      │
         │   throwable != null?   │
         │     Yes→返回默认值      │
         │     No→返回正常结果     │
         └───────────┬────────────┘
                     │
                     ▼
         ┌────────────────────────┐
         │       join()           │
         │    等待最终结果         │
         └───────────┬────────────┘
                     │
                     ▼
         ┌────────────────────────┐
         │     提取结果数据        │
         │  analysisResult        │
         │  routedScene           │
         └────────────────────────┘
```

## 异常传播路径

### 正常流程（无异常）

```
Task1 [Success] ──┐
                  ├──> thenCombine [Success] ──> handle [Success] ──> Result
Task2 [Success] ──┘
```

### Task1 失败场景

```
Task1 [Fail] ──> exceptionally [Return null] ──┐
                                                ├──> thenCombine [Success with null] ──> Result
Task2 [Success] ────────────────────────────────┘
```

### Task2 失败场景

```
Task1 [Success] ────────────────────────────────┐
                                                ├──> thenCombine [Success with default] ──> Result
Task2 [Fail] ──> exceptionally [Return DEFAULT] ┘
```

### 组合失败场景

```
Task1 [Success] ──┐
                  ├──> thenCombine [Fail] ──> exceptionally [Return default] ──> Result
Task2 [Success] ──┘
```

### 超时场景

```
Task1 [Running...] ──┐
                     ├──> thenCombine [Running...] ──> orTimeout [Timeout!] 
Task2 [Running...] ──┘                                      │
                                                            ▼
                                              exceptionally [Return default] ──> Result
```

### 最坏情况（所有异常处理失败）

```
Task1 [Fail] ──> exceptionally [Fail again] ──┐
                                               ├──> thenCombine [Fail] 
Task2 [Fail] ──> exceptionally [Fail again] ──┘         │
                                                         ▼
                                            exceptionally [Fail again]
                                                         │
                                                         ▼
                                              handle [Catch all! Return default]
                                                         │
                                                         ▼
                                                      Result
```

## 代码示例对照

### 第一层：单个任务异常处理

```java
CompletableFuture<String> future1 = CompletableFuture
    .supplyAsync(() -> {
        // 可能抛出异常的逻辑
        return "result";
    })
    .exceptionally(ex -> {
        // ✅ 第一层捕获
        log.error("Task failed", ex);
        return null;  // 返回默认值，阻止异常传播
    });
```

### 第二层：组合任务异常处理

```java
future1.thenCombine(future2, (r1, r2) -> {
        return combine(r1, r2);
    })
    .orTimeout(30, TimeUnit.SECONDS)
    .exceptionally(ex -> {
        // ✅ 第二层捕获（组合异常或超时）
        log.error("Combine failed or timeout", ex);
        return defaultResult;
    });
```

### 第三层：最终保底处理

```java
    .handle((result, throwable) -> {
        if (throwable != null) {
            // ✅ 第三层捕获（兜底）
            log.error("Final fallback", throwable);
            return defaultResult;
        }
        return result;
    })
    .join();
```

## 异常类型对照表

| 异常来源 | 异常类型 | 被哪层捕获 | 处理方式 |
|---------|---------|-----------|---------|
| Task 执行失败 | Exception | 第一层 exceptionally | 返回 null 或默认值 |
| 路由判断失败 | RuntimeException | 第一层 exceptionally | 返回 DEFAULT_ALL |
| 组合逻辑失败 | CompletionException | 第二层 exceptionally | 返回默认组合结果 |
| 超时 | TimeoutException | 第二层 exceptionally | 返回默认组合结果 |
| 未知异常 | Throwable | 第三层 handle | 保底返回默认值 |

## 对比：传统 try-catch vs CompletableFuture

### ❌ 传统方式（命令式）

```java
String result1 = null;
String result2 = null;

try {
    result1 = task1();  // 可能抛异常
} catch (Exception e) {
    log.error("Task1 failed", e);
    result1 = null;
}

try {
    result2 = task2();  // 可能抛异常
} catch (Exception e) {
    log.error("Task2 failed", e);
    result2 = "default";
}

try {
    Result combined = combine(result1, result2);  // 可能抛异常
} catch (Exception e) {
    log.error("Combine failed", e);
    combined = getDefault();
}
```

**问题**：
- 串行执行，效率低
- 代码冗长，嵌套深
- 不符合函数式编程

### ✅ CompletableFuture 方式（函数式）

```java
CompletableFuture<String> future1 = CompletableFuture
    .supplyAsync(() -> task1())
    .exceptionally(ex -> null);

CompletableFuture<String> future2 = CompletableFuture
    .supplyAsync(() -> task2())
    .exceptionally(ex -> "default");

Result combined = future1
    .thenCombine(future2, (r1, r2) -> combine(r1, r2))
    .exceptionally(ex -> getDefault())
    .handle((result, throwable) -> 
        throwable != null ? getDefault() : result)
    .join();
```

**优势**：
- ✅ 并行执行，效率高
- ✅ 代码简洁，链式调用
- ✅ 完全函数式编程
- ✅ 三层异常保护

## 关键要点

1. **永远不要在异步编程中使用 try-catch**
   - 破坏函数式编程范式
   - 无法利用 CompletableFuture 的异常处理链

2. **为每个异步任务添加 exceptionally**
   - 阻止异常向上传播
   - 提供默认值作为兜底

3. **组合任务后添加 exceptionally**
   - 处理组合过程的异常
   - 处理超时异常

4. **最后添加 handle 作为最终保底**
   - 捕获所有未处理的异常
   - 保证一定返回结果

5. **使用 join() 而非 get()**
   - 不抛出受检异常
   - 代码更简洁

## 实战建议

### 开发阶段
- 保留详细的日志记录
- 每层异常都要记录
- 便于调试和排查问题

### 生产阶段
- 监控异常发生频率
- 分析异常类型分布
- 优化异常处理策略

### 性能优化
- 合理设置超时时间
- 避免过度的异常处理
- 使用缓存减少重试

## 总结

CompletableFuture 的三层异常处理机制：

```
┌────────────────────────────────────┐
│ 第一层：单个任务的 exceptionally    │
│  - 处理任务执行异常                 │
│  - 返回默认值                       │
└──────────────┬─────────────────────┘
               │
┌──────────────▼─────────────────────┐
│ 第二层：组合任务的 exceptionally    │
│  - 处理组合异常和超时               │
│  - 返回默认组合结果                 │
└──────────────┬─────────────────────┘
               │
┌──────────────▼─────────────────────┐
│ 第三层：最终的 handle              │
│  - 捕获所有未处理异常               │
│  - 保证一定返回结果                 │
└────────────────────────────────────┘
```

**这是最安全、最优雅的异步异常处理方式！** 🎉

