package com.dopkit.example;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简单的路径匹配器，支持路径变量提取
 */
public class PathMatcher {
    private final Pattern pattern;
    private final String[] variableNames;

    public PathMatcher(String pathPattern) {
        // 将路径模式转换为正则表达式
        // 例如: "/user/{username}" -> "/user/([^/]+)"
        String[] parts = pathPattern.split("/");
        StringBuilder regexBuilder = new StringBuilder();
        java.util.List<String> vars = new java.util.ArrayList<>();

        for (String part : parts) {
            if (!part.isEmpty()) {
                regexBuilder.append("/");
                if (part.startsWith("{") && part.endsWith("}")) {
                    // 路径变量
                    vars.add(part.substring(1, part.length() - 1));
                    regexBuilder.append("([^/]+)");
                } else {
                    // 字面量
                    regexBuilder.append(Pattern.quote(part));
                }
            }
        }
        if (regexBuilder.length() == 0) {
            regexBuilder.append("/");
        }

        this.pattern = Pattern.compile(regexBuilder.toString());
        this.variableNames = vars.toArray(new String[0]);
    }

    /**
     * 匹配路径并提取路径变量
     * @param path 实际路径
     * @return 路径变量映射，如果不匹配则返回null
     */
    public Map<String, String> match(String path) {
        Matcher matcher = pattern.matcher(path);
        if (!matcher.matches()) {
            return null;
        }

        Map<String, String> variables = new HashMap<>();
        for (int i = 0; i < variableNames.length; i++) {
            variables.put(variableNames[i], matcher.group(i + 1));
        }
        return variables;
    }
}
