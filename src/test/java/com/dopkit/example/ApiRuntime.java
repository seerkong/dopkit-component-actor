package com.dopkit.example;

import lombok.Builder;
import lombok.Data;

/**
 * 示例API运行时上下文
 */
@Data
@Builder
public class ApiRuntime {
    private String appId;
    private String userId;
    private UserService userService;
}
