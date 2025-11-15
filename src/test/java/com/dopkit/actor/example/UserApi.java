package com.dopkit.actor.example;

import com.dopkit.actor.ActorRoute;
import com.dopkit.actor.ActorRouteBuilder;
import com.dopkit.example.User;
import com.dopkit.example.UserService;
import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户API - Actor模式示例
 * 演示继承ApiEndpointBase，使用Actor模式进行消息分发
 *
 * @author kongweixian
 */
public class UserApi extends ApiEndpointBase {

    private final UserService userService = new UserService();

    /**
     * 用户API的路由枚举
     */
    public enum UserApiKey {
        SEARCH,
        GET_BY_USERNAME,
        CREATE
    }

    /**
     * 搜索用户请求
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchUserRequest {
        private String keyword;
    }

    /**
     * 获取用户请求
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetUserRequest {
        private String username;
    }

    /**
     * 创建用户请求
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        private String username;
        private String email;
        private int age;
    }

    @Override
    protected ActorRoute<Result<?>> createActorRoute() {
        return ActorRouteBuilder.<Result<?>>create()
                // 搜索用户: 支持Class、Key、Enum三种分发方式
                .match(SearchUserRequest.class,
                        Sets.newHashSet("search", "searchByKeyword"),
                        Sets.newHashSet(UserApiKey.SEARCH),
                        this::search)
                // 获取用户: 支持Class、Key、Enum三种分发方式
                .match(GetUserRequest.class,
                        Sets.newHashSet("getUserByUsername", "user.get"),
                        Sets.newHashSet(UserApiKey.GET_BY_USERNAME),
                        this::getUserByUsername)
                // 创建用户: 支持Class、Key、Enum三种分发方式
                .match(CreateUserRequest.class,
                        Sets.newHashSet("createUser", "user.create"),
                        Sets.newHashSet(UserApiKey.CREATE),
                        this::createUser)
                // 注册枚举转换器（机制4：支持字符串到枚举的自动转换）
                .registerEnumConverter(UserApiKey.class, key -> {
                    try {
                        return UserApiKey.valueOf(key.toUpperCase());
                    } catch (Exception e) {
                        return null;
                    }
                })
                // 默认处理器
                .matchAny(input -> Result.fail("Unsupported input type: "
                        + (input == null ? "null" : input.getClass().getName())))
                .matchAnyKey((key, input) -> Result.fail("Unsupported routeKey: " + key))
                .build();
    }

    /**
     * 搜索用户
     */
    public Result<List<User>> search(SearchUserRequest request) {
        String keyword = request.getKeyword();
        List<User> users = userService.searchUsers(keyword);
        return Result.ok(users);
    }

    /**
     * 根据用户名获取用户
     */
    public Result<User> getUserByUsername(GetUserRequest request) {
        String username = request.getUsername();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.fail("User not found: " + username);
        }
        return Result.ok(user);
    }

    /**
     * 创建用户
     */
    public Result<User> createUser(CreateUserRequest request) {
        User user = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getAge()
        );
        return Result.ok(user);
    }
}
