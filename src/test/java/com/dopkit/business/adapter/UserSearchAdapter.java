package com.dopkit.business.adapter;

import com.dopkit.business.ApiInnerLogicAdapter;
import com.dopkit.business.ApiInputAdapter;
import com.dopkit.example.User;

import java.util.List;

/**
 * 用户搜索适配器（函数式风格）
 * 使用静态字段定义路由模式、输入适配器和核心逻辑
 *
 * 编写方式一：函数式组件封装adapter
 * 通过静态字段定义适配器逻辑，然后在路由表中通过 new FuncStyleApiAdapter 包装
 */
public class UserSearchAdapter {

    public static final String RoutePattern = "/user/search";

    /**
     * 输入适配器：从请求中提取搜索关键词
     */
    public static ApiInputAdapter<String> InnerInputAdapter = (runtime, request) -> {
        if (request.getQueryParams() != null) {
            return request.getQueryParams().get("keyword");
        }
        return null;
    };

    /**
     * 核心逻辑适配器：调用 UserService 执行搜索
     */
    public static ApiInnerLogicAdapter<String, List<User>> CoreLogicAdapter = (runtime, keyword) -> {
        return runtime.getUserService().searchUsers(keyword);
    };
}
