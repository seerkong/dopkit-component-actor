package com.dopkit.actor.command;

import com.dopkit.actor.example.Result;
import com.dopkit.example.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserCommandApi测试 - 演示机制5 CommandTable模式
 *
 * @author kongweixian
 */
class UserCommandApiTest {

    private UserCommandApi commandApi;

    @BeforeEach
    void setUp() {
        commandApi = new UserCommandApi();
    }

    /**
     * 测试机制5: 搜索用户命令
     */
    @Test
    void testCommandTable_SearchUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", "alice");

        // 使用大写命令
        Result<List<User>> result1 = commandApi.callByCommandTyped("SEARCH_USER", params);
        assertTrue(result1.isSuccess());
        assertNotNull(result1.getData());
        assertFalse(result1.getData().isEmpty());

        // 使用小写命令（转换器会自动转为大写）
        Result<List<User>> result2 = commandApi.callByCommandTyped("search_user", params);
        assertTrue(result2.isSuccess());
        assertNotNull(result2.getData());
    }

    /**
     * 测试机制5: 获取用户详情命令
     */
    @Test
    void testCommandTable_GetUserDetail() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "alice");

        Result<User> result = commandApi.callByCommandTyped("GET_USER_DETAIL", params);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("alice", result.getData().getUsername());
    }

    /**
     * 测试机制5: 创建用户命令
     */
    @Test
    void testCommandTable_CreateUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "newuser");
        params.put("email", "newuser@example.com");
        params.put("age", 25);

        Result<User> result = commandApi.callByCommandTyped("CREATE_USER", params);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("newuser", result.getData().getUsername());
        assertEquals("newuser@example.com", result.getData().getEmail());
    }

    /**
     * 测试机制5: 列出所有用户命令
     */
    @Test
    void testCommandTable_ListAllUsers() {
        Result<List<User>> result = commandApi.callByCommandTyped("LIST_ALL_USERS", null);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty());
    }

    /**
     * 测试机制5: 大小写不敏感
     */
    @Test
    void testCommandTable_CaseInsensitive() {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", "bob");

        // 测试各种大小写组合
        Result<List<User>> result1 = commandApi.callByCommandTyped("SEARCH_USER", params);
        Result<List<User>> result2 = commandApi.callByCommandTyped("search_user", params);
        Result<List<User>> result3 = commandApi.callByCommandTyped("Search_User", params);

        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());
    }

    /**
     * 测试机制5: 未实现的命令（触发兜底处理器）
     * UPDATE_USER 和 DELETE_USER 在枚举中存在，但未在HandlerTable中实现
     */
    @Test
    void testCommandTable_UnimplementedCommand() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "test");

        // UPDATE_USER 命令存在但未实现handler
        Result<?> result = commandApi.callByCommandTyped("UPDATE_USER", params);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not supported or not implemented"));
        assertTrue(result.getMessage().contains("UPDATE_USER"));
    }

    /**
     * 测试机制5: 无法识别的命令（触发兜底处理器）
     */
    @Test
    void testCommandTable_UnknownCommand() {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", "test");

        // 完全不存在的命令
        Result<?> result = commandApi.callByCommandTyped("UNKNOWN_COMMAND", params);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not supported or not implemented"));
        assertTrue(result.getMessage().contains("UNKNOWN_COMMAND"));
    }

    /**
     * 测试机制5: 缺少必需参数
     */
    @Test
    void testCommandTable_MissingParameter() {
        Map<String, Object> params = new HashMap<>();
        // 缺少 keyword 参数

        Result<?> result = commandApi.callByCommandTyped("SEARCH_USER", params);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("keyword"));
    }

    /**
     * 测试机制5: 无效的输入类型
     */
    @Test
    void testCommandTable_InvalidInputType() {
        // 传入字符串而不是Map
        String invalidInput = "invalid input";

        Result<?> result = commandApi.callByCommandTyped("SEARCH_USER", invalidInput);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid input type"));
    }

    /**
     * 测试机制5: 用户不存在
     */
    @Test
    void testCommandTable_UserNotFound() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "nonexistent");

        Result<?> result = commandApi.callByCommandTyped("GET_USER_DETAIL", params);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("User not found"));
    }

    /**
     * 综合测试：演示完整的命令调用流程
     * 注意：每个handler有独立的UserService实例，所以无法跨handler共享数据
     */
    @Test
    void testCommandTable_CompleteWorkflow() {
        // 1. 创建新用户
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("username", "testuser");
        createParams.put("email", "testuser@example.com");
        createParams.put("age", 30);

        Result<User> createResult = commandApi.callByCommandTyped("create_user", createParams);
        assertTrue(createResult.isSuccess());
        assertEquals("testuser", createResult.getData().getUsername());
        assertEquals("testuser@example.com", createResult.getData().getEmail());

        // 2. 搜索现有用户（使用预置数据）
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("keyword", "alice");

        Result<List<User>> searchResult = commandApi.callByCommandTyped("search_user", searchParams);
        assertTrue(searchResult.isSuccess());
        assertFalse(searchResult.getData().isEmpty());
        assertTrue(searchResult.getData().stream()
                .anyMatch(u -> u.getUsername().equals("alice")));

        // 3. 获取用户详情（使用预置数据）
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("username", "alice");

        Result<User> getResult = commandApi.callByCommandTyped("get_user_detail", getParams);
        assertTrue(getResult.isSuccess());
        assertEquals("alice", getResult.getData().getUsername());
        assertEquals("alice@example.com", getResult.getData().getEmail());

        // 4. 列出所有用户
        Result<List<User>> listResult = commandApi.callByCommandTyped("list_all_users", null);
        assertTrue(listResult.isSuccess());
        assertFalse(listResult.getData().isEmpty());
        // 应该包含预置的用户
        assertTrue(listResult.getData().stream()
                .anyMatch(u -> u.getUsername().equals("alice")));
    }

    /**
     * 对比测试：机制5 vs 其他机制
     * 展示CommandTable模式更适合程序入口层
     */
    @Test
    void testCommandTable_ComparedToOtherMechanisms() {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", "alice");

        // 机制5: 使用字符串命令（最适合入口层，简单直接）
        Result<List<User>> result = commandApi.callByCommandTyped("SEARCH_USER", params);
        assertTrue(result.isSuccess());

        // 优势：
        // 1. 输入是简单的字符串，易于从HTTP请求、消息队列等获取
        // 2. 支持大小写不敏感
        // 3. 集中式的handler管理（UserCommandHandlerTable）
        // 4. 强大的兜底机制
        // 5. 适合作为系统的入口分发层
    }
}
