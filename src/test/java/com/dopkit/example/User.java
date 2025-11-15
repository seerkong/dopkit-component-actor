package com.dopkit.example;

import lombok.Builder;
import lombok.Data;

/**
 * 示例用户实体
 */
@Data
@Builder
public class User {
    private String id;
    private String username;
    private String email;
    private int age;
}
