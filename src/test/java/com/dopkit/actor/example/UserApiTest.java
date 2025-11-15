package com.dopkit.actor.example;

import com.dopkit.example.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserApi测试 - 演示4种内置分发机制
 *
 * @author kongweixian
 */
class UserApiTest {

    private UserApi userApi;

    @BeforeEach
    void setUp() {
        userApi = new UserApi();
    }

    /**
     * 测试机制1: By Class类型分发
     * 直接传入请求对象，根据Class类型自动路由到对应handler
     */
    @Test
    void testDispatchByClass_Search() {
        // 搜索用户
        UserApi.SearchUserRequest searchRequest = UserApi.SearchUserRequest.builder()
                .keyword("alice")
                .build();

        Result<List<User>> result = userApi.callTyped(searchRequest);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty());
        assertTrue(result.getData().get(0).getUsername().contains("alice"));
    }

    @Test
    void testDispatchByClass_GetUser() {
        // 获取用户
        UserApi.GetUserRequest getRequest = UserApi.GetUserRequest.builder()
                .username("alice")
                .build();

        Result<User> result = userApi.callTyped(getRequest);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("alice", result.getData().getUsername());
    }

    @Test
    void testDispatchByClass_CreateUser() {
        // 创建用户
        UserApi.CreateUserRequest createRequest = UserApi.CreateUserRequest.builder()
                .username("dave")
                .email("dave@example.com")
                .age(35)
                .build();

        Result<User> result = userApi.callTyped(createRequest);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("dave", result.getData().getUsername());
        assertEquals("dave@example.com", result.getData().getEmail());
    }

    /**
     * 测试机制2: By RouteKey字符串分发
     * 使用字符串key进行路由，一个handler可以注册多个key
     */
    @Test
    void testDispatchByRouteKey_Search() {
        UserApi.SearchUserRequest request = UserApi.SearchUserRequest.builder()
                .keyword("bob")
                .build();

        // 使用不同的routeKey访问同一个handler
        Result<List<User>> result1 = userApi.callByRouteKeyTyped("search", request);
        Result<List<User>> result2 = userApi.callByRouteKeyTyped("searchByKeyword", request);

        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertFalse(result1.getData().isEmpty());
        assertFalse(result2.getData().isEmpty());
    }

    @Test
    void testDispatchByRouteKey_GetUser() {
        UserApi.GetUserRequest request = UserApi.GetUserRequest.builder()
                .username("bob")
                .build();

        // 使用不同的routeKey访问同一个handler
        Result<User> result1 = userApi.callByRouteKeyTyped("getUserByUsername", request);
        Result<User> result2 = userApi.callByRouteKeyTyped("user.get", request);

        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertEquals("bob", result1.getData().getUsername());
        assertEquals("bob", result2.getData().getUsername());
    }

    @Test
    void testDispatchByRouteKey_CreateUser() {
        UserApi.CreateUserRequest request = UserApi.CreateUserRequest.builder()
                .username("eve")
                .email("eve@example.com")
                .age(28)
                .build();

        // 使用不同的routeKey访问同一个handler
        Result<User> result1 = userApi.callByRouteKeyTyped("createUser", request);

        assertTrue(result1.isSuccess());
        assertEquals("eve", result1.getData().getUsername());

        UserApi.CreateUserRequest request2 = UserApi.CreateUserRequest.builder()
                .username("frank")
                .email("frank@example.com")
                .age(40)
                .build();

        Result<User> result2 = userApi.callByRouteKeyTyped("user.create", request2);

        assertTrue(result2.isSuccess());
        assertEquals("frank", result2.getData().getUsername());
    }

    /**
     * 测试机制3: By 枚举类型分发
     * 使用类型安全的枚举进行路由
     */
    @Test
    void testDispatchByEnum_Search() {
        UserApi.SearchUserRequest request = UserApi.SearchUserRequest.builder()
                .keyword("charlie")
                .build();

        Result<List<User>> result = userApi.callByEnumTyped(UserApi.UserApiKey.SEARCH, request);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void testDispatchByEnum_GetUser() {
        UserApi.GetUserRequest request = UserApi.GetUserRequest.builder()
                .username("alice")
                .build();

        Result<User> result = userApi.callByEnumTyped(
                UserApi.UserApiKey.GET_BY_USERNAME, request);

        assertTrue(result.isSuccess());
        assertEquals("alice", result.getData().getUsername());
    }

    @Test
    void testDispatchByEnum_CreateUser() {
        UserApi.CreateUserRequest request = UserApi.CreateUserRequest.builder()
                .username("grace")
                .email("grace@example.com")
                .age(32)
                .build();

        Result<User> result = userApi.callByEnumTyped(UserApi.UserApiKey.CREATE, request);

        assertTrue(result.isSuccess());
        assertEquals("grace", result.getData().getUsername());
    }

    /**
     * 测试机制4: By RouteKey with Enum fallback
     * 使用字符串key，自动尝试转换为枚举
     * 这是最强大的机制，支持字符串到枚举的自动转换
     */
    @Test
    void testDispatchByRouteKeyWithEnumConversion() {
        UserApi.SearchUserRequest searchRequest = UserApi.SearchUserRequest.builder()
                .keyword("test")
                .build();

        // 使用大写枚举名作为routeKey，会自动转换为枚举
        Result<List<User>> result1 = userApi.callByRouteKeyTyped("SEARCH", searchRequest);
        assertTrue(result1.isSuccess());

        // 使用小写，转换器会自动转为大写
        Result<List<User>> result2 = userApi.callByRouteKeyTyped("search", searchRequest);
        assertTrue(result2.isSuccess());

        UserApi.GetUserRequest getRequest = UserApi.GetUserRequest.builder()
                .username("alice")
                .build();

        Result<User> result3 = userApi.callByRouteKeyTyped("GET_BY_USERNAME", getRequest);
        assertTrue(result3.isSuccess());

        Result<User> result4 = userApi.callByRouteKeyTyped("get_by_username", getRequest);
        assertTrue(result4.isSuccess());

        UserApi.CreateUserRequest createRequest = UserApi.CreateUserRequest.builder()
                .username("henry")
                .email("henry@example.com")
                .age(45)
                .build();

        Result<User> result5 = userApi.callByRouteKeyTyped("CREATE", createRequest);
        assertTrue(result5.isSuccess());

        Result<User> result6 = userApi.callByRouteKeyTyped("create", createRequest);
        assertTrue(result6.isSuccess());
    }

    /**
     * 测试默认处理器 - 未注册的输入类型
     */
    @Test
    void testDefaultInputHandler_UnsupportedType() {
        String unsupportedInput = "unsupported input";

        Result<?> result = userApi.callTyped(unsupportedInput);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Unsupported input type"));
        assertTrue(result.getMessage().contains("String"));
    }

    /**
     * 测试默认Key处理器 - 未注册的routeKey
     */
    @Test
    void testDefaultKeyHandler_UnsupportedKey() {
        UserApi.SearchUserRequest request = UserApi.SearchUserRequest.builder()
                .keyword("test")
                .build();

        Result<?> result = userApi.callByRouteKeyTyped("unknownKey", request);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Unsupported routeKey"));
        assertTrue(result.getMessage().contains("unknownKey"));
    }

    /**
     * 测试用户不存在的情况
     */
    @Test
    void testGetUser_NotFound() {
        UserApi.GetUserRequest request = UserApi.GetUserRequest.builder()
                .username("nonexistent")
                .build();

        Result<User> result = userApi.callTyped(request);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("User not found"));
    }

    /**
     * 综合测试：演示同一个handler可以通过多种方式访问
     */
    @Test
    void testMultipleDispatchMechanisms_SameHandler() {
        String keyword = "test";

        // 准备相同的请求
        UserApi.SearchUserRequest request1 = UserApi.SearchUserRequest.builder()
                .keyword(keyword)
                .build();
        UserApi.SearchUserRequest request2 = UserApi.SearchUserRequest.builder()
                .keyword(keyword)
                .build();
        UserApi.SearchUserRequest request3 = UserApi.SearchUserRequest.builder()
                .keyword(keyword)
                .build();
        UserApi.SearchUserRequest request4 = UserApi.SearchUserRequest.builder()
                .keyword(keyword)
                .build();

        // 机制1: By Class
        Result<List<User>> result1 = userApi.callTyped(request1);

        // 机制2: By RouteKey
        Result<List<User>> result2 = userApi.callByRouteKeyTyped("search", request2);

        // 机制3: By Enum
        Result<List<User>> result3 = userApi.callByEnumTyped(UserApi.UserApiKey.SEARCH, request3);

        // 机制4: By RouteKey with Enum conversion
        Result<List<User>> result4 = userApi.callByRouteKeyTyped("SEARCH", request4);

        // 所有方式都应该成功
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());
        assertTrue(result4.isSuccess());

        // 结果应该一致
        assertEquals(result1.getData().size(), result2.getData().size());
        assertEquals(result2.getData().size(), result3.getData().size());
        assertEquals(result3.getData().size(), result4.getData().size());
    }
}
