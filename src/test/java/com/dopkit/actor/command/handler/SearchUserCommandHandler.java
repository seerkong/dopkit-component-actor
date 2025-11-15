package com.dopkit.actor.command.handler;

import com.dopkit.actor.command.UserCommandHandler;
import com.dopkit.actor.example.Result;
import com.dopkit.example.User;
import com.dopkit.example.UserService;

import java.util.List;
import java.util.Map;

/**
 * 搜索用户命令处理器
 *
 * @author kongweixian
 */
public class SearchUserCommandHandler implements UserCommandHandler {

    private final UserService userService = new UserService();

    @Override
    public Result<?> handle(Object input) {
        if (!(input instanceof Map)) {
            return Result.fail("Invalid input type for SEARCH_USER command");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) input;
        String keyword = (String) params.get("keyword");

        if (keyword == null) {
            return Result.fail("Missing 'keyword' parameter");
        }

        List<User> users = userService.searchUsers(keyword);
        return Result.ok(users);
    }
}
