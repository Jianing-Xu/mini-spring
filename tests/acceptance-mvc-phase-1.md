# MVC Phase 1 验收用例清单

## 1. 说明

本阶段只验收以下范围：

- `DispatcherServlet`
- `AnnotationMapping`
- `HandlerMethod`
- `HandlerMapping`
- `HandlerAdapter`
- 简单参数绑定
- `String/void` 返回值处理
- 默认异常处理

不验收：

- `ViewResolver`
- `HandlerInterceptor`
- `ExceptionResolver` 链式扩展
- JSON body
- 路径变量
- 复杂对象绑定

## 2. 正常路径用例

### TC-1.1 初始化时扫描 Controller 并构建映射表

- Given：`mini-spring` 容器中存在多个 `@Controller` Bean 和 `RequestMappingHandlerMapping`
- When：执行 `DispatcherServlet.init(context)`
- Then：能成功构建 `method + path -> HandlerMethod` 映射表
- Then：映射表数量与控制器方法数量一致
- Then：Dispatcher 运行时流水线已完成排序与冻结

### TC-1.2 精确路径与方法匹配成功

- Given：存在 `GET /users/detail` 对应的控制器方法
- When：发送 `GET /users/detail`
- Then：`HandlerMapping` 返回唯一 `HandlerExecutionChain`
- Then：`HandlerAdapter` 成功调用目标方法
- Then：响应状态为 200

### TC-1.3 类级路径前缀与方法级路径拼接成功

- Given：Controller 类上声明 `/users`，方法上声明 `/detail`
- When：发送 `GET /users/detail`
- Then：能够匹配到目标方法

### TC-1.4 @RequestParam String 参数绑定成功

- Given：控制器方法参数为 `@RequestParam("name") String name`
- When：请求参数中包含 `name=alice`
- Then：方法接收到 `alice`
- Then：响应体包含预期结果

### TC-1.5 @RequestParam int/long/boolean 参数绑定成功

- Given：控制器方法参数分别为 `int`、`long`、`boolean`
- When：请求参数值合法
- Then：参数被正确转换
- Then：响应体正确写出

### TC-1.6 原生 WebRequest/WebResponse 注入成功

- Given：控制器方法参数包含 `WebRequest` 或 `WebResponse`
- When：请求进入 Dispatcher
- Then：目标方法拿到当前请求和响应抽象
- Then：可基于当前对象完成业务处理

### TC-1.7 String 返回值写入响应体

- Given：控制器方法返回 `String`
- When：方法正常返回
- Then：在 `view=DISABLED` 模式下，该值被直接写入响应体
- Then：不进入视图解析流程

### TC-1.8 void 返回值正常结束

- Given：控制器方法返回 `void`
- When：方法正常执行完成
- Then：Dispatcher 正常结束请求处理
- Then：不会报“不支持的返回值类型”错误

## 3. 失败路径用例

### TC-2.1 找不到 Handler 返回 404

- Given：容器中不存在匹配当前请求的映射
- When：发送请求
- Then：Dispatcher 返回 404
- Then：错误信息包含请求方法与请求路径

### TC-2.2 请求方法不匹配返回 404

- Given：只存在 `POST /users/detail`
- When：发送 `GET /users/detail`
- Then：返回 404
- Then：不执行控制器方法

### TC-2.3 映射冲突初始化失败

- Given：两个 Controller 方法声明了相同的 `method + path`
- When：执行 `DispatcherServlet.init(context)`
- Then：初始化直接失败
- Then：异常包含冲突路径、请求方法、两个候选处理器信息

> [注释] 映射冲突必须在初始化阶段暴露
> - 背景：注解路由是静态元数据
> - 影响：延迟到运行时才发现冲突，会导致线上请求不稳定
> - 取舍：Phase 1 验收要求 init 直接 fail-fast
> - 可选增强：后续增加冲突报告格式化输出

### TC-2.4 缺少必填参数返回 400

- Given：控制器方法声明 `@RequestParam("id") long id`
- When：请求中缺少 `id`
- Then：返回 400
- Then：错误信息包含缺失参数名 `id`

### TC-2.5 参数类型转换失败返回 400

- Given：控制器方法声明 `@RequestParam("id") long id`
- When：请求中传入 `id=abc`
- Then：返回 400
- Then：错误信息包含参数名 `id`
- Then：错误信息包含目标类型 `long`

### TC-2.6 控制器抛异常返回 500

- Given：控制器方法内部抛出运行时异常
- When：请求进入 Dispatcher
- Then：返回 500
- Then：错误信息包含处理器标识和异常摘要

### TC-2.7 没有可用 HandlerAdapter 直接失败

- Given：`HandlerMapping` 找到了 `HandlerMethod`
- When：Dispatcher 在 `HandlerAdapter` 列表中找不到支持者
- Then：直接抛出配置异常
- Then：错误信息包含 Handler 类型信息

### TC-2.8 多个 HandlerAdapter 同时支持同一个 Handler 直接失败

- Given：两个 `HandlerAdapter` 都声明支持同一个 `HandlerMethod`
- When：Dispatcher 选择适配器
- Then：直接失败
- Then：错误信息包含候选适配器数量和类型

## 4. 集成边界用例

### TC-3.1 MVC 组件由 mini-spring 容器管理

- Given：`RequestMappingHandlerMapping`、`RequestMappingHandlerAdapter`、`DefaultHandlerExceptionResolver` 都注册为 Bean
- When：初始化 Dispatcher
- Then：这些组件均从 `mini-spring` 容器获取，而不是由 MVC 自行实例化

### TC-3.2 Controller 被 AOP 代理时仍能建立映射

- Given：Controller Bean 被 `mini-spring` AOP 代理
- When：`RequestMappingHandlerMapping` 扫描 Controller
- Then：仍能识别原始类上的 `@Controller` 和 `@RequestMapping`
- Then：请求分发成功

### TC-3.3 view=DISABLED 时 String 不解释为视图名

- Given：Controller 方法返回 `"user-detail"`
- When：请求完成处理
- Then：响应体内容为 `user-detail`
- Then：不会尝试查找 `ViewResolver`

## 5. 验收结论标准

本阶段通过标准：

- 正常路径用例全部通过
- 失败路径用例全部按预期返回或 fail-fast
- 集成边界用例全部通过
- 未出现跨阶段能力：
  - 无 `ViewResolver`
  - 无拦截器链
  - 无异常解析责任链
  - 无 JSON body 绑定
