package com.dopkit.actor.command.handler;

import com.dopkit.actor.command.UserCommandHandler;
import com.dopkit.actor.example.Result;
import com.dopkit.example.User;
import com.dopkit.example.UserService;

import java.util.Map;

/**
 * 获取用户详情命令处理器
 *
 * @author kongweixian
 */
public class GetUserDetailCommandHandler implements UserCommandHandler {

    private final UserService userService = new UserService();

    @Override
    public Result<?> handle(Object input) {
        if (!(input instanceof Map)) {
            return Result.fail("Invalid input type for GET_USER_DETAIL command");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) input;
        String username = (String) params.get("username");

        if (username == null) {
            return Result.fail("Missing 'username' parameter");
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.fail("User not found: " + username);
        }

        return Result.ok(user);
    }
}
