# MVC Phase 2 验收用例清单

## 1. 说明

本阶段只验收以下范围：

- `HandlerMethodArgumentResolver`
- `HandlerMethodArgumentResolverComposite`
- `HandlerMethodReturnValueHandler`
- `HandlerMethodReturnValueHandlerComposite`
- `SimpleTypeConverter`
- `RequestMappingHandlerAdapter` 对扩展点的集成
- `view=DISABLED` 下的 `String/void` 返回值处理

不验收：

- `ViewResolver`
- `ModelAndView`
- `HandlerInterceptor`
- `ExceptionResolver` 链式扩展
- JSON body
- 路径变量
- 复杂对象绑定

## 2. 正常路径用例

### TC-1.1 初始化时收集并排序参数解析器

- Given：`mini-spring` 容器中注册了多个 `HandlerMethodArgumentResolver` Bean
- When：执行 `DispatcherServlet.init(context)`
- Then：`RequestMappingHandlerAdapter` 能收集全部解析器
- Then：按 `PriorityOrdered -> Ordered -> 默认最低优先级` 排序
- Then：排序结果在请求期保持不变

### TC-1.2 初始化时收集并排序返回值处理器

- Given：容器中注册了多个 `HandlerMethodReturnValueHandler` Bean
- When：执行 `DispatcherServlet.init(context)`
- Then：`RequestMappingHandlerAdapter` 能收集全部处理器
- Then：按统一排序规则排序
- Then：排序结果在请求期保持不变

### TC-1.3 默认 @RequestParam String 参数解析成功

- Given：控制器方法参数为 `@RequestParam("name") String name`
- When：请求参数中包含 `name=alice`
- Then：默认 `RequestParamArgumentResolver` 成功解析参数
- Then：控制器拿到 `alice`

### TC-1.4 默认简单类型参数转换成功

- Given：控制器方法参数为 `int`、`long`、`boolean`
- When：请求参数值为合法字符串
- Then：`SimpleTypeConverter` 成功完成转换
- Then：控制器接收到正确类型值

### TC-1.5 WebRequest/WebResponse 参数解析成功

- Given：控制器方法参数包含 `WebRequest` 和 `WebResponse`
- When：请求进入 Dispatcher
- Then：对应默认参数解析器成功注入当前请求和响应对象

### TC-1.6 String 返回值处理成功

- Given：控制器方法返回 `String`
- When：方法正常返回 `"ok"`
- Then：`StringReturnValueHandler` 将 `"ok"` 写入响应体
- Then：请求正常结束

### TC-1.7 void 返回值处理成功

- Given：控制器方法返回 `void`
- When：方法正常完成
- Then：`VoidReturnValueHandler` 正常结束处理链
- Then：不会产生“不支持的返回值类型”错误

### TC-1.8 自定义高优先级参数解析器覆盖默认解析器

- Given：自定义 `HandlerMethodArgumentResolver` Bean 与默认 `RequestParamArgumentResolver` 同时支持同一参数
- When：执行请求
- Then：排序靠前的自定义解析器先命中
- Then：默认解析器不再执行

### TC-1.9 自定义高优先级返回值处理器覆盖默认处理器

- Given：自定义 `HandlerMethodReturnValueHandler` Bean 与默认 `StringReturnValueHandler` 同时支持 `String`
- When：控制器返回 `String`
- Then：高优先级自定义处理器先执行
- Then：默认处理器不再执行

## 3. 失败路径用例

### TC-2.1 不支持的参数类型直接失败

- Given：控制器方法参数类型不被任何 `HandlerMethodArgumentResolver` 支持
- When：Dispatcher 执行参数解析
- Then：直接失败
- Then：异常包含控制器类、方法名、参数索引、参数类型

### TC-2.2 缺少必填参数返回 400

- Given：控制器方法参数为 `@RequestParam("id") long id`
- When：请求缺少 `id`
- Then：返回 400
- Then：错误信息包含参数名 `id`

### TC-2.3 参数类型转换失败返回 400

- Given：控制器方法参数为 `@RequestParam("id") long id`
- When：请求传入 `id=abc`
- Then：返回 400
- Then：错误信息包含参数名 `id`
- Then：错误信息包含目标类型 `long`

### TC-2.4 不支持的返回值类型直接失败

- Given：控制器方法返回值类型不被任何 `HandlerMethodReturnValueHandler` 支持
- When：控制器方法返回该类型
- Then：直接失败
- Then：异常包含控制器类、方法名、返回值类型

### TC-2.5 response 已提交时不重复写响应

- Given：控制器方法或前置处理已经将 `response.committed` 标记为 `true`
- When：返回值处理器继续执行
- Then：不会再次写出响应体
- Then：不会抛出重复提交错误

## 4. 集成边界用例

### TC-3.1 Resolver 与 Handler 都来自 mini-spring 容器

- Given：默认和自定义解析器/处理器都注册为 Bean
- When：执行 MVC 初始化
- Then：这些组件全部从 `mini-spring` 容器收集
- Then：MVC 请求期不再动态扫描容器

### TC-3.2 自定义 Resolver / Handler 允许被 AOP 代理

- Given：某个自定义参数解析器或返回值处理器被 `mini-spring` AOP 代理
- When：MVC 初始化时收集组件
- Then：该组件仍能参与排序和执行

### TC-3.3 view=DISABLED 语义保持不变

- Given：控制器方法返回 `String`
- When：请求完成
- Then：该值仍作为响应体写出
- Then：不会尝试视图解析

## 5. 验收结论标准

本阶段通过标准：

- 正常路径用例全部通过
- 失败路径用例全部按预期失败或返回 400
- 集成边界用例全部通过
- 未出现跨阶段能力：
  - 无 `ViewResolver`
  - 无 `ModelAndView`
  - 无拦截器链
  - 无异常责任链
  - 无 JSON body 解析
