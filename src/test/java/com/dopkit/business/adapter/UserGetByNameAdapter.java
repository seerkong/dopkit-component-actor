package com.dopkit.business.adapter;

import com.dopkit.business.OOPStyleApiAdapter;
import com.dopkit.example.ApiRequest;
import com.dopkit.example.ApiRuntime;
import com.dopkit.example.User;
import lombok.Data;

import java.util.Map;

/**
 * 根据用户名获取用户适配器（OOP风格）
 *
 * 编写方式二：实现业务封装后的 OOPStyleApiAdapter 接口
 * 而不是包含全部adapter签名的 StdOOPStyleAdapter
 * 无需写过多的泛型和接口函数实现，降低封装层adapter的复杂性
 *
 * 当业务封装不复杂时，每个组件封装只需要实现 makeInnerInput 和 runCoreLogic
 * 至于其他adapter，可以复用公共的（底层的StdRunComponentLogic，或者业务封装层公共的ApiDefaultAdapter）
 */
public class UserGetByNameAdapter implements OOPStyleApiAdapter<UserGetByNameAdapter.GetUserRequest, User> {

    /**
     * 获取用户请求
     */
    @Data
    public static class GetUserRequest {
        private final String username;
    }

    @Override
    public String getRoutePattern() {
        return "/user/{username}";
    }

    @Override
    public GetUserRequest makeInnerInput(ApiRuntime runtime, ApiRequest request, Map<String, String> pathVariables) {
        String username = pathVariables.get("username");
        return new GetUserRequest(username);
    }

    @Override
    public User runCoreLogic(ApiRuntime runtime, GetUserRequest input) {
        return runtime.getUserService().getUserByUsername(input.getUsername());
    }
}
