package com.dopkit.example;

import com.dopkit.router.ComponentHandler;
import com.dopkit.router.GenericRouter;
import com.dopkit.router.RouteMatcher;
import com.dopkit.router.RouteRegistration;
import com.dopkit.component.StdOOPStyleAdapter;
import com.dopkit.component.StdRunComponentLogic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 演示更高级的OOP风格实现
 * 类似于原来的 OOPStyleInternalRpcAdapter
 */
public class AdvancedOOPStyleTest {

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

    @Test
    public void testAdvancedOOPStyle() {
        // 使用完整的OOP风格适配器
        router.register(new SearchUserAdapter());
        router.register(new GetUserAdapter());

        // 测试搜索
        ApiRequest searchRequest = ApiRequest.builder()
                .path("/api/user/search")
                .method("GET")
                .queryParams(new HashMap<String, String>() {{
                    put("keyword", "bob");
                }})
                .build();
        ApiResponse searchResponse = router.dispatch(runtime, searchRequest);
        assertNotNull(searchResponse);
        assertEquals(200, searchResponse.getCode());

        // 测试获取
        ApiRequest getRequest = ApiRequest.builder()
                .path("/api/user/charlie")
                .method("GET")
                .build();
        ApiResponse getResponse = router.dispatch(runtime, getRequest);
        assertNotNull(getResponse);
        assertEquals(200, getResponse.getCode());
        User user = (User) getResponse.getData();
        assertEquals("charlie", user.getUsername());
    }

    /**
     * 抽象基类：实现了StdOOPStyleAdapter的完整OOP风格适配器
     * 这个基类简化了路由注册的样板代码
     */
    static abstract class BaseApiAdapter<TInnerInput, TInnerOutput>
            extends RouteRegistration<ApiRuntime, ApiRequest, Map<String, String>, ApiResponse>
            implements StdOOPStyleAdapter<ApiRuntime, ApiRequest, Object, Object, ApiResponse,
            ApiRuntime, TInnerInput, Object, TInnerOutput> {

        private final PathMatcher pathMatcher;

        public BaseApiAdapter(String pathPattern) {
            super(
                    createMatcher(pathPattern),
                    createHandler()
            );
            this.pathMatcher = new PathMatcher(pathPattern);
        }

        private static RouteMatcher<ApiRequest, Map<String, String>> createMatcher(String pathPattern) {
            PathMatcher matcher = new PathMatcher(pathPattern);
            return request -> matcher.match(request.getPath());
        }

        private static <TInnerInput, TInnerOutput> ComponentHandler<ApiRuntime, ApiRequest, Map<String, String>, ApiResponse> createHandler() {
            return (runtime, request, matchResult) -> {
                // 这里需要访问外部实例，所以在子类构造时通过super传入
                throw new UnsupportedOperationException("Should be overridden in constructor");
            };
        }

        @Override
        public Object stdMakeOuterComputed(ApiRuntime outerRuntime, ApiRequest outerInput, Object outerConfig) {
            return null;
        }

        @Override
        public ApiRuntime stdMakeInnerRuntime(ApiRuntime outerRuntime, ApiRequest outerInput, Object outerConfig, Object outerDerived) {
            return outerRuntime;
        }

        @Override
        public Object stdMakeInnerConfig(ApiRuntime outerRuntime, ApiRequest outerInput, Object outerConfig, Object outerDerived) {
            return null;
        }

        @Override
        public ApiResponse stdMakeOuterOutput(ApiRuntime outerRuntime, ApiRequest outerInput, Object outerConfig, Object outerDerived, TInnerOutput innerOutput) {
            return convertToResponse(innerOutput);
        }

        /**
         * 子类需要实现的方法：从请求中提取内部输入
         */
        @Override
        public abstract TInnerInput stdMakeInnerInput(
                ApiRuntime outerRuntime,
                ApiRequest outerInput,
                Object outerConfig,
                Object outerDerived);

        /**
         * 子类需要实现的方法：核心业务逻辑
         */
        @Override
        public abstract TInnerOutput stdCoreLogic(
                ApiRuntime runtime,
                TInnerInput input,
                Object config);

        /**
         * 子类需要实现的方法：将业务输出转换为API响应
         */
        protected abstract ApiResponse convertToResponse(TInnerOutput output);
    }

    /**
     * 搜索用户适配器（完整OOP风格）
     */
    static class SearchUserAdapter extends RouteRegistration<ApiRuntime, ApiRequest, Map<String, String>, ApiResponse> {
        public SearchUserAdapter() {
            super(
                    request -> new PathMatcher("/api/user/search").match(request.getPath()),
                    (runtime, request, matchResult) -> {
                        // 提取查询参数
                        String keyword = request.getQueryParams() != null
                                ? request.getQueryParams().get("keyword")
                                : null;
                        // 调用业务逻辑
                        List<User> users = runtime.getUserService().searchUsers(keyword);
                        // 返回响应
                        return ApiResponse.success(users);
                    }
            );
        }
    }

    /**
     * 获取用户适配器（使用标准组件封装逻辑）
     */
    static class GetUserAdapter extends RouteRegistration<ApiRuntime, ApiRequest, Map<String, String>, ApiResponse> {
        public GetUserAdapter() {
            super(
                    request -> new PathMatcher("/api/user/{username}").match(request.getPath()),
                    (runtime, request, matchResult) -> StdRunComponentLogic.runByFuncStyleAdapter(
                            runtime, request, null,
                            StdRunComponentLogic::stdMakeNullOuterComputed,
                            StdRunComponentLogic::stdMakeIdentityInnerRuntime,
                            (rt, req, config, computed) -> matchResult.get("username"),
                            StdRunComponentLogic::stdMakeIdentityInnerConfig,
                            (ApiRuntime rt, String username, Object config) -> rt.getUserService().getUserByUsername(username),
                            (ApiRuntime rt, ApiRequest req, Object config, Object computed, User user) -> {
                                if (user == null) {
                                    return ApiResponse.error(404, "User not found");
                                }
                                return ApiResponse.success(user);
                            }
                    )
            );
        }
    }
}
