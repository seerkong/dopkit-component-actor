package com.dopkit.business;

import com.dopkit.example.ApiRequest;
import com.dopkit.example.ApiResponse;
import com.dopkit.example.ApiRuntime;
import com.dopkit.example.PathMatcher;
import com.dopkit.example.User;
import com.dopkit.example.UserService;
import com.dopkit.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户API路由表测试
 *
 * 特点：
 * 1. 使用静态路由表 UserApiAdapterRouter.routes
 * 2. 手动进行路由匹配和分发
 * 3. 演示函数式和OOP两种适配器的使用
 */
public class UserApiAdapterRouterTest {

    private ApiRuntime runtime;

    @BeforeEach
    public void setUp() {
        runtime = ApiRuntime.builder()
                .appId("test-app")
                .userId("admin")
                .userService(new UserService())
                .build();
    }

    /**
     * 测试函数式风格的用户搜索
     */
    @Test
    public void testFunctionalStyleSearch() {
        // 构造请求
        ApiRequest request = ApiRequest.builder()
                .path("/user/search")
                .method("GET")
                .queryParams(new HashMap<String, String>() {{
                    put("keyword", "alice");
                }})
                .build();

        // 分发请求
        ApiResponse response = dispatch(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getCode());
        List<User> users = (List<User>) response.getData();
        assertEquals(1, users.size());
        assertEquals("alice", users.get(0).getUsername());
    }

    /**
     * 测试函数式风格的用户创建
     */
    @Test
    public void testFunctionalStyleCreate() {
        // 构造请求
        ApiRequest request = ApiRequest.builder()
                .path("/user/create")
                .method("POST")
                .queryParams(new HashMap<String, String>() {{
                    put("username", "david");
                    put("email", "david@example.com");
                    put("age", "28");
                }})
                .build();

        // 分发请求
        ApiResponse response = dispatch(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getCode());
        User user = (User) response.getData();
        assertEquals("david", user.getUsername());
        assertEquals("david@example.com", user.getEmail());
        assertEquals(28, user.getAge());
    }

    /**
     * 测试OOP风格的获取用户
     */
    @Test
    public void testOOPStyleGetUser() {
        // 构造请求
        ApiRequest request = ApiRequest.builder()
                .path("/user/bob")
                .method("GET")
                .build();

        // 分发请求
        ApiResponse response = dispatch(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getCode());
        User user = (User) response.getData();
        assertEquals("bob", user.getUsername());
    }

    /**
     * 测试混合使用场景
     */
    @Test
    public void testMixedUsage() {
        // 1. 搜索所有用户
        ApiRequest searchRequest = ApiRequest.builder()
                .path("/user/search")
                .method("GET")
                .build();
        ApiResponse searchResponse = dispatch(searchRequest);
        assertEquals(200, searchResponse.getCode());
        List<User> allUsers = (List<User>) searchResponse.getData();
        assertEquals(3, allUsers.size()); // alice, bob, charlie

        // 2. 获取特定用户
        ApiRequest getRequest = ApiRequest.builder()
                .path("/user/charlie")
                .method("GET")
                .build();
        ApiResponse getResponse = dispatch(getRequest);
        assertEquals(200, getResponse.getCode());
        User charlie = (User) getResponse.getData();
        assertEquals("charlie", charlie.getUsername());

        // 3. 创建新用户
        ApiRequest createRequest = ApiRequest.builder()
                .path("/user/create")
                .method("POST")
                .queryParams(new HashMap<String, String>() {{
                    put("username", "eve");
                    put("email", "eve@example.com");
                    put("age", "22");
                }})
                .build();
        ApiResponse createResponse = dispatch(createRequest);
        assertEquals(200, createResponse.getCode());

        // 4. 验证新用户已创建
        ApiRequest verifyRequest = ApiRequest.builder()
                .path("/user/eve")
                .method("GET")
                .build();
        ApiResponse verifyResponse = dispatch(verifyRequest);
        assertEquals(200, verifyResponse.getCode());
        User eve = (User) verifyResponse.getData();
        assertEquals("eve", eve.getUsername());
    }

    /**
     * 测试路由不匹配的情况
     */
    @Test
    public void testRouteNotFound() {
        ApiRequest request = ApiRequest.builder()
                .path("/api/unknown")
                .method("GET")
                .build();

        ApiResponse response = dispatch(request);
        assertNull(response); // 没有匹配的路由，返回 null
    }

    /**
     * 分发请求到匹配的适配器
     */
    private ApiResponse dispatch(ApiRequest request) {
        String path = request.getPath();

        // 遍历路由表，按顺序匹配
        for (ApiAdapter adapter : UserApiAdapterRouter.routes) {
            String routePattern = adapter.getRoutePattern();

            // 使用 PathMatcher 进行路由匹配
            PathMatcher matcher = new PathMatcher(routePattern);
            Map<String, String> pathVars = matcher.match(path);

            if (pathVars != null) {
                // 匹配成功，调用适配器处理
                return adapter.dispatch(runtime, request, pathVars);
            }
        }

        // 没有匹配的路由
        return null;
    }
}
