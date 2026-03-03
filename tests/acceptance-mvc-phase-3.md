# MVC Phase 3 验收用例清单

## 1. 说明

本阶段验收以下范围：

- `HandlerInterceptor`
- `HandlerExecutionChain`
- `ExceptionResolver` 责任链
- `ModelAndView`
- `ViewResolver`
- `view=DISABLED` 与 `view=SIMPLE_VIEW_RESOLVER` 的返回值语义切换

不验收：

- `@ExceptionHandler`
- JSON body
- 路径变量
- 异步请求处理
- 模板引擎细节

## 2. 正常路径用例

### TC-1.1 多个拦截器按顺序执行 preHandle

- Given：存在两个有序拦截器 `InterceptorA(order=0)`、`InterceptorB(order=1)`
- When：请求进入 Dispatcher
- Then：先执行 `InterceptorA.preHandle`
- Then：再执行 `InterceptorB.preHandle`

### TC-1.2 postHandle 与 afterCompletion 按逆序执行

- Given：两个拦截器都返回 `preHandle=true`
- When：Handler 正常执行完成
- Then：`postHandle` 执行顺序为 `B -> A`
- Then：`afterCompletion` 执行顺序为 `B -> A`

### TC-1.3 preHandle 短路时不执行 Handler

- Given：第一个拦截器 `preHandle=true`，第二个拦截器 `preHandle=false`
- When：请求进入 Dispatcher
- Then：Handler 不会执行
- Then：后续拦截器不再执行 `preHandle`

### TC-1.4 preHandle 短路时只回调已执行拦截器 afterCompletion

- Given：第二个拦截器返回 `preHandle=false`
- When：请求被短路
- Then：只对已通过 `preHandle` 的拦截器执行 `afterCompletion`
- Then：未进入链的拦截器不会收到 `afterCompletion`

### TC-1.5 自定义异常解析器优先于默认异常解析器

- Given：存在高优先级自定义 `ExceptionResolver`
- And：Handler 抛出业务异常
- When：请求进入 Dispatcher
- Then：自定义 `ExceptionResolver` 先命中并处理异常
- Then：默认异常解析器不再执行

### TC-1.6 默认异常解析器在无自定义命中时兜底

- Given：没有任何自定义解析器支持当前异常
- When：Handler 抛出异常
- Then：默认异常解析器返回 500

### TC-1.7 SIMPLE_VIEW_RESOLVER 模式下 String 视图名可解析并渲染

- Given：`view=SIMPLE_VIEW_RESOLVER`
- And：Controller 返回 `"user-detail"`
- When：请求完成处理
- Then：`ViewResolver` 成功解析 `user-detail`
- Then：视图成功渲染输出

### TC-1.8 SIMPLE_VIEW_RESOLVER 模式下 ModelAndView 可渲染

- Given：Controller 返回 `ModelAndView("user-detail")`
- When：请求完成处理
- Then：模型数据被传递给视图
- Then：视图成功渲染输出

### TC-1.9 DISABLED 模式下 String 仍作为响应体

- Given：`view=DISABLED`
- And：Controller 返回 `"plain-text"`
- When：请求完成处理
- Then：响应体内容为 `plain-text`
- Then：不会触发 `ViewResolver`

## 3. 失败路径用例

### TC-2.1 响应已提交后发生异常时不再重复写错误结果

- Given：Handler 已经写出响应并将 `response.committed` 置为 `true`
- When：后续流程抛出异常
- Then：异常解析链不再尝试写新的响应体
- Then：仍会执行 `afterCompletion`

### TC-2.2 无可用 ViewResolver 时视图渲染失败

- Given：`view=SIMPLE_VIEW_RESOLVER`
- And：Controller 返回逻辑视图名
- And：没有任何 `ViewResolver` 支持该视图名
- When：请求完成处理
- Then：直接失败
- Then：错误信息包含视图名

### TC-2.3 多个 ExceptionResolver 都支持异常时仅首个命中生效

- Given：两个异常解析器都支持同一个异常类型
- When：Handler 抛出该异常
- Then：只执行排序后的首个解析器
- Then：后续解析器不再执行

### TC-2.4 视图渲染过程中抛异常时仍执行 afterCompletion

- Given：`View.render` 内部抛出异常
- When：Dispatcher 尝试渲染视图
- Then：异常能够进入兜底处理
- Then：`afterCompletion` 仍然执行

## 4. 集成边界用例

### TC-3.1 拦截器、异常处理器、视图解析器都来自 mini-spring 容器

- Given：这些组件都注册为 Bean
- When：Dispatcher 初始化
- Then：全部从 `mini-spring` 容器收集
- Then：按顺序排序后冻结

### TC-3.2 自定义组件允许被 AOP 代理

- Given：某个 `HandlerInterceptor` 或 `ExceptionResolver` 被 `mini-spring` AOP 代理
- When：Dispatcher 初始化并处理请求
- Then：该组件仍能参与链路执行

### TC-3.3 Controller 被代理时拦截器链和异常链仍能正常工作

- Given：Controller Bean 为代理对象
- When：请求进入 Dispatcher
- Then：映射、拦截、异常处理都正常

## 5. 验收结论标准

本阶段通过标准：

- 拦截器顺序与短路用例全部通过
- 异常解析链用例全部通过
- 视图开启与关闭两种模式都通过
- 集成边界用例全部通过
- 未引入跨阶段能力：
  - 无 `@ExceptionHandler`
  - 无 JSON body
  - 无异步处理
