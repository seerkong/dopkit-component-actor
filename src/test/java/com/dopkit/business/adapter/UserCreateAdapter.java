package com.dopkit.business.adapter;

import com.dopkit.business.ApiInnerLogicAdapter;
import com.dopkit.business.ApiInputAdapter;
import com.dopkit.example.User;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 用户创建适配器（函数式风格）
 */
public class UserCreateAdapter {

    public static final String RoutePattern = "/user/create";

    /**
     * 创建用户请求
     */
    @Data
    @Builder
    public static class CreateUserRequest {
        private String username;
        private String email;
        private int age;
    }

    /**
     * 输入适配器：从查询参数中提取用户信息
     */
    public static ApiInputAdapter<CreateUserRequest> InnerInputAdapter = (runtime, request) -> {
        Map<String, String> params = request.getQueryParams();
        if (params == null) {
            throw new IllegalArgumentException("Missing parameters");
        }
        return CreateUserRequest.builder()
                .username(params.get("username"))
                .email(params.get("email"))
                .age(Integer.parseInt(params.getOrDefault("age", "0")))
                .build();
    };

    /**
     * 核心逻辑适配器：调用 UserService 创建用户
     */
    public static ApiInnerLogicAdapter<CreateUserRequest, User> CoreLogicAdapter = (runtime, request) -> {
        return runtime.getUserService().createUser(
                request.getUsername(),
                request.getEmail(),
                request.getAge()
        );
    };
}
