package com.dopkit.actor.command.handler;

import com.dopkit.actor.command.UserCommandHandler;
import com.dopkit.actor.example.Result;
import com.dopkit.example.User;
import com.dopkit.example.UserService;

import java.util.List;

/**
 * 列出所有用户命令处理器
 *
 * @author kongweixian
 */
public class ListAllUsersCommandHandler implements UserCommandHandler {

    private final UserService userService = new UserService();

    @Override
    public Result<?> handle(Object input) {
        // 获取所有用户（搜索空字符串）
        List<User> users = userService.searchUsers("");
        return Result.ok(users);
    }
}
