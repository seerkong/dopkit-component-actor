package com.dopkit.actor.command;

import com.dopkit.actor.example.Result;

/**
 * 用户命令处理器接口
 * 所有命令处理器都实现此接口
 *
 * @author kongweixian
 */
public interface UserCommandHandler {

    /**
     * 处理命令
     *
     * @param input 输入参数
     * @return 处理结果
     */
    Result<?> handle(Object input);
}
