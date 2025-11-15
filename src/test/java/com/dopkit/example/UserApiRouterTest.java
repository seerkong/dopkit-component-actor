package com.dopkit.example;

import com.dopkit.router.GenericRouter;
import com.dopkit.router.RouteRegistration;
import com.dopkit.component.StdRunComponentLogic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 演示函数式和OOP两种注册方式的测试用例
 */
public class UserApiRouterTest {

    private GenericRouter<ApiRuntime, ApiRequest, Map<String, String>, ApiResponse> router;
    private ApiRuntime runtime;

    @BeforeEach
    public void setUp() {
        router = new GenericRouter<>();
        runtime = ApiRuntime.builder()
                .appId("test-app")
                .userId("admin")
                .userService(new UserService())
                .build();
    }

    /**
     * 测试函数式注册方式
     * 类似于原来的 FuncStyleInternalRpcAdapter
     */
    @Test
    public void testFunctionalStyleRegistration() {
        // 注册: GET /user/search?keyword=xxx
        router.register(
                // 路由匹配器
                request -> {
                    PathMatcher matcher = new PathMatcher("/user/search");
                    return matcher.match(request.getPath());
                },
                // 处理器 - 使用标准组件封装逻辑
                (rt, req, matchResult) -> {
                    return StdRunComponentLogic.runByFuncStyleAdapter(
                            rt, req, null,
                            // 不需要额外计算
                            StdRunComponentLogic::stdMakeNullOuterComputed,
                            // Runtime直接透传
                            StdRunComponentLogic::stdMakeIdentityInnerRuntime,
                            // 输入转换: 提取查询参数
                            (runtime, request, config, computed) -> {
                                String keyword = request.getQueryParams() != null
                                        ? request.getQueryParams().get("keyword")
                                        : null;
                                return keyword;
                            },
                            // Config直接透传
                            StdRunComponentLogic::stdMakeIdentityInnerConfig,
                            // 核心逻辑: 调用UserService
                            (ApiRuntime runtime, String keyword, Object config) -> {
                                return runtime.getUserService().searchUsers(keyword);
                            },
                            // 输出转换: 转换为ApiResponse
                            (ApiRuntime runtime, ApiRequest request, Object config, Object computed, List<User> users) -> {
                                return ApiResponse.success(users);
                            }
                    );
                }
        );

        // 测试搜索用户
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("keyword", "alice");
        ApiRequest request = ApiRequest.builder()
                .path("/user/search")
                .method("GET")
                .queryParams(queryParams)
                .build();

        ApiResponse response = router.dispatch(runtime, request);
        assertNotNull(response);
        assertEquals(200, response.getCode());
        List<User> users = (List<User>) response.getData();
        assertEquals(1, users.size());
        assertEquals("alice", users.get(0).getUsername());
    }

    /**
     * 测试OOP风格注册方式
     * 类似于原来的 OOPStyleInternalRpcAdapter
     */
    @Test
    public void testOOPStyleRegistration() {
        // 使用OOP风格的路由注册
        router.register(new GetUserByUsernameRoute());

        // 测试获取用户
        ApiRequest request = ApiRequest.builder()
                .path("/user/bob")
                .method("GET")
                .build();

        ApiResponse response = router.dispatch(runtime, request);
        assertNotNull(response);
        assertEquals(200, response.getCode());
        User user = (User) response.getData();
        assertEquals("bob", user.getUsername());
    }

    /**
     * 测试混合注册方式：同时使用函数式和OOP风格
     */
    @Test
    public void testMixedRegistration() {
        // 1. 函数式注册: 搜索用户
        router.register(
                request -> new PathMatcher("/user/search").match(request.getPath()),
                (rt, req, matchResult) -> {
                    String keyword = req.getQueryParams() != null
                            ? req.getQueryParams().get("keyword")
                            : null;
                    List<User> users = rt.getUserService().searchUsers(keyword);
                    return ApiResponse.success(users);
                }
        );

        // 2. 函数式注册: 创建用户（需要在通配路由之前）
        router.register(
                request -> new PathMatcher("/user/create").match(request.getPath()),
                (rt, req, matchResult) -> {
                    Map<String, String> params = req.getQueryParams();
                    if (params == null) {
                        return ApiResponse.error(400, "Missing parameters");
                    }
                    String username = params.get("username");
                    String email = params.get("email");
                    int age = Integer.parseInt(params.getOrDefault("age", "0"));
                    User user = rt.getUserService().createUser(username, email, age);
                    return ApiResponse.success(user);
                }
        );

        // 3. OOP注册: 获取用户（通配路由，放在最后避免误匹配）
        router.register(new GetUserByUsernameRoute());

        // 测试搜索
        ApiRequest searchRequest = ApiRequest.builder()
                .path("/user/search")
                .method("GET")
                .queryParams(new HashMap<String, String>() {{
                    put("keyword", "");
                }})
                .build();
        ApiResponse searchResponse = router.dispatch(runtime, searchRequest);
        assertEquals(200, searchResponse.getCode());

        // 测试获取
        ApiRequest getRequest = ApiRequest.builder()
                .path("/user/alice")
                .method("GET")
                .build();
        ApiResponse getResponse = router.dispatch(runtime, getRequest);
        assertEquals(200, getResponse.getCode());

        // 测试创建
        ApiRequest createRequest = ApiRequest.builder()
                .path("/user/create")
                .method("POST")
                .queryParams(new HashMap<String, String>() {{
                    put("username", "david");
                    put("email", "david@example.com");
                    put("age", "28");
                }})
                .build();
        ApiResponse createResponse = router.dispatch(runtime, createRequest);
        assertEquals(200, createResponse.getCode());
        User createdUser = (User) createResponse.getData();
        assertEquals("david", createdUser.getUsername());
    }

    /**
     * OOP风格的路由注册示例
     * 继承自RouteRegistration，实现完整的路由逻辑
     */
    static class GetUserByUsernameRoute extends RouteRegistration<ApiRuntime, ApiRequest, Map<String, String>, ApiResponse> {
        private static final PathMatcher PATH_MATCHER = new PathMatcher("/user/{username}");

        public GetUserByUsernameRoute() {
            super(
                    // 匹配器
                    request -> PATH_MATCHER.match(request.getPath()),
                    // 处理器 - 使用标准组件封装逻辑
                    (runtime, request, matchResult) -> {
                        return StdRunComponentLogic.runByFuncStyleAdapter(
                                runtime, request, null,
                                StdRunComponentLogic::stdMakeNullOuterComputed,
                                StdRunComponentLogic::stdMakeIdentityInnerRuntime,
                                // 输入转换: 提取路径变量
                                (rt, req, config, computed) -> matchResult.get("username"),
                                StdRunComponentLogic::stdMakeIdentityInnerConfig,
                                // 核心逻辑
                                (ApiRuntime rt, String username, Object config) -> rt.getUserService().getUserByUsername(username),
                                // 输出转换
                                (ApiRuntime rt, ApiRequest req, Object config, Object computed, User user) -> {
                                    if (user == null) {
                                        return ApiResponse.error(404, "User not found");
                                    }
                                    return ApiResponse.success(user);
                                }
                        );
                    }
            );
        }
    }
}
