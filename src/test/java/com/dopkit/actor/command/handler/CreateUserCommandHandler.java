package com.dopkit.actor.command.handler;

import com.dopkit.actor.command.UserCommandHandler;
import com.dopkit.actor.example.Result;
import com.dopkit.example.User;
import com.dopkit.example.UserService;

import java.util.Map;

/**
 * 创建用户命令处理器
 *
 * @author kongweixian
 */
public class CreateUserCommandHandler implements UserCommandHandler {

    private final UserService userService = new UserService();

    @Override
    public Result<?> handle(Object input) {
        if (!(input instanceof Map)) {
            return Result.fail("Invalid input type for CREATE_USER command");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) input;
        String username = (String) params.get("username");
        String email = (String) params.get("email");
        Integer age = (Integer) params.get("age");

        if (username == null || email == null || age == null) {
            return Result.fail("Missing required parameters: username, email, age");
        }

        User user = userService.createUser(username, email, age);
        return Result.ok(user);
    }
}
