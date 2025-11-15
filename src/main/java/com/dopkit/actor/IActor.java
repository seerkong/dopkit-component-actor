package com.dopkit.actor;

/**
 * DOP Actor 接口
 * 提供基于类型和Key的消息分发机制
 *
 * 类似 Akka Actor，但更简化和类型安全
 *
 * @author kongweixian
 */
public interface IActor<TResult> {

    /**
     * 根据输入类型调用对应的handler
     * @param input 输入参数
     * @param <TOutput> 期望的输出类型
     * @return 处理结果
     */
    <TOutput> TResult call(Object input);

    /**
     * 根据routeKey调用对应的handler
     * @param routeKey 路由Key
     * @param input 输入参数
     * @param <TOutput> 期望的输出类型
     * @return 处理结果
     */
    <TOutput> TResult callByRouteKey(String routeKey, Object input);

    /**
     * 根据枚举调用对应的handler
     * @param routeEnum 路由枚举
     * @param input 输入参数
     * @param <TOutput> 期望的输出类型
     * @param <E> 枚举类型
     * @return 处理结果
     */
    <TOutput, E extends Enum<E>> TResult callByEnum(E routeEnum, Object input);

    /**
     * 分发机制5: By Command字符串分发（CommandTable模式）
     * 介于程序入口层和class级别分发之间的机制
     *
     * 工作流程：
     * 1. 输入字符串command
     * 2. 使用注册的converter将字符串转换为枚举
     * 3. 使用注册的handlerExtractor从枚举中提取handler
     * 4. 执行handler处理input
     * 5. 如果转换失败或没有找到handler，执行兜底handler
     *
     * @param command 命令字符串
     * @param input 输入参数
     * @param <TOutput> 期望的输出类型
     * @return 处理结果
     */
    <TOutput> TResult callByCommand(String command, Object input);
}
