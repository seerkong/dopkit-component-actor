package com.dopkit.actor.command;

import com.dopkit.actor.command.handler.CreateUserCommandHandler;
import com.dopkit.actor.command.handler.GetUserDetailCommandHandler;
import com.dopkit.actor.command.handler.ListAllUsersCommandHandler;
import com.dopkit.actor.command.handler.SearchUserCommandHandler;
import com.dopkit.actor.example.Result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

/**
 * 用户命令处理器表
 * 枚举本身包含命令类型和对应的handler实例
 *
 * @author kongweixian
 */
@AllArgsConstructor
@Getter
public enum UserCommandHandlerTable {
    /**
     * 搜索用户命令
     */
    SEARCH_USER(UserCommandType.SEARCH_USER, new SearchUserCommandHandler()),

    /**
     * 获取用户详情命令
     */
    GET_USER_DETAIL(UserCommandType.GET_USER_DETAIL, new GetUserDetailCommandHandler()),

    /**
     * 创建用户命令
     */
    CREATE_USER(UserCommandType.CREATE_USER, new CreateUserCommandHandler()),

    /**
     * 列出所有用户命令
     */
    LIST_ALL_USERS(UserCommandType.LIST_ALL_USERS, new ListAllUsersCommandHandler()),

    // 注意：UPDATE_USER 和 DELETE_USER 未实现，用于测试兜底处理器
    ;

    private final UserCommandType type;
    private final UserCommandHandler handler;

    /**
     * 检查是否包含指定类型的handler
     */
    public static boolean containsHandlerOfType(UserCommandType cmdType) {
        for (UserCommandHandlerTable item : UserCommandHandlerTable.values()) {
            if (item.type == cmdType) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据命令类型获取对应的handler
     * 如果未找到，返回null（由Actor的兜底处理器处理）
     */
    public static UserCommandHandler getHandler(UserCommandType cmdType) {
        for (UserCommandHandlerTable item : UserCommandHandlerTable.values()) {
            if (item.type == cmdType) {
                return item.getHandler();
            }
        }
        return null; // 未找到handler，触发兜底逻辑
    }

    /**
     * 获取handler包装为Function，用于Actor框架
     */
    public static Function<Object, Result<?>> getHandlerFunction(
            UserCommandType cmdType) {
        UserCommandHandler handler = getHandler(cmdType);
        if (handler == null) {
            return null;
        }
        return handler::handle;
    }
}
