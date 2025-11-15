package com.dopkit.business;

import com.dopkit.business.adapter.UserCreateAdapter;
import com.dopkit.business.adapter.UserGetByNameAdapter;
import com.dopkit.business.adapter.UserSearchAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户API路由表
 *
 * 特点：
 * 1. 使用静态 List 声明所有路由
 * 2. 支持函数式风格（通过 FuncStyleApiAdapter 包装静态字段）
 * 3. 支持 OOP 风格（直接 new 实现类）
 * 4. 注意路由顺序：通配路由（/user/{username}）必须放在最后，避免误匹配
 */
public class UserApiAdapterRouter {

    public static List<ApiAdapter> routes = new ArrayList<>();

    static {
        // 1. 函数式注册：搜索用户
        routes.add(new FuncStyleApiAdapter<>(
                UserSearchAdapter.RoutePattern,
                UserSearchAdapter.InnerInputAdapter,
                UserSearchAdapter.CoreLogicAdapter
        ));

        // 2. 函数式注册：创建用户
        routes.add(new FuncStyleApiAdapter<>(
                UserCreateAdapter.RoutePattern,
                UserCreateAdapter.InnerInputAdapter,
                UserCreateAdapter.CoreLogicAdapter
        ));

        // 3. OOP注册：获取用户
        // 为了避免 /user/{username} 路由和上面两个user的路由冲突，【必须】将这个接口放到后面
        routes.add(new UserGetByNameAdapter());
    }
}
