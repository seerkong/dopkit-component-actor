package com.dopkit.example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 示例用户服务（模拟业务逻辑）
 */
public class UserService {
    private final Map<String, User> users = new HashMap<>();

    public UserService() {
        // 初始化一些测试数据
        users.put("alice", User.builder()
                .id("1")
                .username("alice")
                .email("alice@example.com")
                .age(25)
                .build());
        users.put("bob", User.builder()
                .id("2")
                .username("bob")
                .email("bob@example.com")
                .age(30)
                .build());
        users.put("charlie", User.builder()
                .id("3")
                .username("charlie")
                .email("charlie@example.com")
                .age(35)
                .build());
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new ArrayList<>(users.values());
        }
        return users.values().stream()
                .filter(user -> user.getUsername().contains(keyword) ||
                        user.getEmail().contains(keyword))
                .collect(Collectors.toList());
    }

    public User createUser(String username, String email, int age) {
        String id = String.valueOf(users.size() + 1);
        User user = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .age(age)
                .build();
        users.put(username, user);
        return user;
    }
}
