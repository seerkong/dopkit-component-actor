package com.dopkit.dispatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Ant-style path matcher supporting '?', '*', and '**'.
 */
public class AntPathMatcher {
    private final String pathSeparator;

    public AntPathMatcher() {
        this("/");
    }

    public AntPathMatcher(String pathSeparator) {
        this.pathSeparator = pathSeparator == null || pathSeparator.isEmpty() ? "/" : pathSeparator;
    }

    public boolean match(String pattern, String path) {
        return doMatch(pattern, path, null);
    }

    public PathMatchResult matchAndExtract(String pattern, String path) {
        Map<String, String> variables = new HashMap<>();
        if (doMatch(pattern, path, variables)) {
            return new PathMatchResult(pattern, path, variables);
        }
        return null;
    }

    private boolean doMatch(String pattern, String path, Map<String, String> variables) {
        String[] pattDirs = tokenize(pattern);
        String[] pathDirs = tokenize(path);

        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        // Match all elements up to the first **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String patDir = pattDirs[pattIdxStart];
            if ("**".equals(patDir)) {
                break;
            }
            if (!matchStrings(patDir, pathDirs[pathIdxStart], variables)) {
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }

        if (pathIdxStart > pathIdxEnd) {
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!"**".equals(pattDirs[i])) {
                    return false;
                }
            }
            return true;
        }

        if (pattIdxStart > pattIdxEnd) {
            return false;
        }

        // Match all elements up to the last **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String patDir = pattDirs[pattIdxEnd];
            if ("**".equals(patDir)) {
                break;
            }
            if (!matchStrings(patDir, pathDirs[pathIdxEnd], variables)) {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }

        if (pathIdxStart > pathIdxEnd) {
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!"**".equals(pattDirs[i])) {
                    return false;
                }
            }
            return true;
        }

        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
                if ("**".equals(pattDirs[i])) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                pattIdxStart++;
                continue;
            }
            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            outer:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = pattDirs[pattIdxStart + j + 1];
                    String subStr = pathDirs[pathIdxStart + i + j];
                    if (!matchStrings(subPat, subStr, variables)) {
                        continue outer;
                    }
                }
                foundIdx = pathIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
            if (!"**".equals(pattDirs[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean matchStrings(String pattern, String str, Map<String, String> variables) {
        if (pattern.startsWith("{") && pattern.endsWith("}")) {
            if (variables != null) {
                String variableName = pattern.substring(1, pattern.length() - 1);
                variables.put(variableName, str);
            }
            return true;
        }
        int patIdx = 0;
        int strIdx = 0;
        int patLen = pattern.length();
        int strLen = str.length();
        int starIdx = -1;
        int strTmpIdx = -1;

        while (strIdx < strLen) {
            if (patIdx < patLen && (pattern.charAt(patIdx) == '?' || pattern.charAt(patIdx) == str.charAt(strIdx))) {
                patIdx++;
                strIdx++;
            } else if (patIdx < patLen && pattern.charAt(patIdx) == '*') {
                starIdx = patIdx++;
                strTmpIdx = strIdx;
            } else if (starIdx != -1) {
                patIdx = starIdx + 1;
                strIdx = ++strTmpIdx;
            } else {
                return false;
            }
        }

        while (patIdx < patLen && pattern.charAt(patIdx) == '*') {
            patIdx++;
        }

        return patIdx == patLen;
    }

    private String[] tokenize(String path) {
        if (path == null || path.isEmpty()) {
            return new String[0];
        }
        String trimmed = path;
        if (trimmed.startsWith(pathSeparator)) {
            trimmed = trimmed.substring(pathSeparator.length());
        }
        if (trimmed.endsWith(pathSeparator)) {
            trimmed = trimmed.substring(0, trimmed.length() - pathSeparator.length());
        }
        if (trimmed.isEmpty()) {
            return new String[]{""};
        }
        List<String> result = new ArrayList<>();
        int sepLen = pathSeparator.length();
        int index;
        int start = 0;
        while ((index = trimmed.indexOf(pathSeparator, start)) != -1) {
            result.add(trimmed.substring(start, index));
            start = index + sepLen;
        }
        result.add(trimmed.substring(start));
        return result.toArray(new String[0]);
    }

}
