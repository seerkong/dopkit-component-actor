package com.dopkit.actor.command;

import com.dopkit.actor.ActorRoute;
import com.dopkit.actor.ActorRouteBuilder;
import com.dopkit.actor.example.ApiEndpointBase;
import com.dopkit.actor.example.Result;

/**
 * 用户命令API - 演示机制5 CommandTable模式
 * 介于程序入口层和class级别分发之间的机制
 *
 * 特点：
 * 1. 使用字符串命令作为输入
 * 2. 自动转换为枚举类型
 * 3. 从枚举表中查找对应的handler
 * 4. 支持兜底处理器
 *
 * @author kongweixian
 */
public class UserCommandApi extends ApiEndpointBase {

    @Override
    protected ActorRoute<Result<?>> createActorRoute() {
        return ActorRouteBuilder.<Result<?>>create()
                // 机制5: 注册CommandTable
                .registerCommandTable(
                        // 1. 字符串到枚举的转换器（支持大小写不敏感）
                        commandStr -> {
                            try {
                                return UserCommandType.valueOf(commandStr.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                return null; // 转换失败，触发兜底处理
                            }
                        },
                        // 2. 从枚举中提取handler的函数
                        UserCommandHandlerTable::getHandlerFunction,
                        // 3. 兜底处理器（当命令无法识别或handler未实现时）
                        (command, input) -> Result.fail(
                                "Command not supported or not implemented: " + command)
                )
                .build();
    }
}
