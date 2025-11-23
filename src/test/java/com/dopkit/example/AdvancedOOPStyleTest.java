package com.dopkit.example;

import com.dopkit.dispatch.PathMatchResult;
import com.dopkit.router.GenericPathRouter;
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

    private GenericPathRouter<ApiRuntime, ApiRequest, ApiResponse> router;
    private ApiRuntime runtime;

    @BeforeEach
    public void setUp() {
        router = new GenericPathRouter<>(ApiRequest::getPath);
        runtime = ApiRuntime.builder()
                .appId("test-app")
                .userId("admin")
                .userService(new UserService())
                .build();
    }

    @Test
    public void testAdvancedOOPStyle() {
        // 使用完整的OOP风格适配器
        new SearchUserAdapter().register(router);
        new GetUserAdapter().register(router);

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

    static class SearchUserAdapter {
        void register(GenericPathRouter<ApiRuntime, ApiRequest, ApiResponse> router) {
            router.register("/api/user/search",
                    (runtime, request, matchResult) -> {
                        String keyword = request.getQueryParams() != null
                                ? request.getQueryParams().get("keyword")
                                : null;
                        List<User> users = runtime.getUserService().searchUsers(keyword);
                        return ApiResponse.success(users);
                    });
        }
    }

    /**
     * 获取用户适配器（使用标准组件封装逻辑）
     */
    static class GetUserAdapter {
        void register(GenericPathRouter<ApiRuntime, ApiRequest, ApiResponse> router) {
            router.register("/api/user/{username}",
                    (runtime, request, matchResult) -> StdRunComponentLogic.runByFuncStyleAdapter(
                            runtime, request, null,
                            StdRunComponentLogic::stdMakeNullOuterComputed,
                            StdRunComponentLogic::stdMakeIdentityInnerRuntime,
                            (rt, req, config, computed) -> matchResult.getVariables().get("username"),
                            StdRunComponentLogic::stdMakeIdentityInnerConfig,
                            (ApiRuntime rt, String username, Object config) -> rt.getUserService().getUserByUsername(username),
                            (ApiRuntime rt, ApiRequest req, Object config, Object computed, User user) -> {
                                if (user == null) {
                                    return ApiResponse.error(404, "User not found");
                                }
                                return ApiResponse.success(user);
                            }
                    ));
        }
    }
}
