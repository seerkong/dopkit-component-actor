package com.dopkit.actor.command;

/**
 * 用户命令类型枚举
 * 定义所有支持的用户操作命令
 *
 * @author kongweixian
 */
public enum UserCommandType {
    /**
     * 搜索用户命令
     */
    SEARCH_USER,

    /**
     * 获取用户详情命令
     */
    GET_USER_DETAIL,

    /**
     * 创建用户命令
     */
    CREATE_USER,

    /**
     * 更新用户命令
     */
    UPDATE_USER,

    /**
     * 删除用户命令
     */
    DELETE_USER,

    /**
     * 列出所有用户命令
     */
    LIST_ALL_USERS
}
