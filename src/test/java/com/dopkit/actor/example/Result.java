package com.dopkit.actor.example;

import lombok.Data;

/**
 * 通用结果类
 * 简化版的Result，用于演示Actor模式
 *
 * @param <T> 数据类型
 * @author kongweixian
 */
@Data
public class Result<T> {
    private boolean success;
    private T data;
    private String message;
    private String errorCode;

    private Result(boolean success, T data, String message, String errorCode) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
    }

    public static <T> Result<T> ok() {
        return new Result<>(true, null, "success", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(true, data, "success", null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(false, null, message, "ERROR");
    }

    public static <T> Result<T> fail(String errorCode, String message) {
        return new Result<>(false, null, message, errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFail() {
        return !success;
    }
}
