package com.tjeoun.newssearch.util;

import com.tjeoun.newssearch.enums.AdminLogEnum;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminLogUtils {
    private static final Pattern MODIFY_ARTICLE_PATTERN = Pattern.compile("^/admin/boards/edit/(\\d+)$");
    private static final Pattern DELETE_ARTICLE_PATTERN = Pattern.compile("^/admin/boards/delete/(\\d+)$");
    private static final Pattern MODIFY_NEWS_PATTERN = Pattern.compile("^/admin/news/edit/(\\d+)$");
    private static final Pattern DELETE_NEWS_PATTERN = Pattern.compile("^/admin/news/delete/(\\d+)$");
    private static final Pattern SUSPEND_MEMBER_PATTERN = Pattern.compile("^/admin/members/delete/(\\d+)$");
    private static final Pattern MODIFY_MEMBER_PATTERN = Pattern.compile("^/admin/members/edit/(\\d+)$");

    public static Map<String, Object> parseUriToAdminLogAction(String uri) {
        Matcher m;
        m = MODIFY_ARTICLE_PATTERN.matcher(uri);
        if (m.matches()) {
            return Map.of("enum", AdminLogEnum.MODIFY_ARTICLE, "targetId", Long.parseLong(m.group(1)));
        }

        m = DELETE_ARTICLE_PATTERN.matcher(uri);
        if (m.matches()) {
            return Map.of("enum", AdminLogEnum.DELETE_ARTICLE, "targetId", Long.parseLong(m.group(1)));
        }

        m = MODIFY_NEWS_PATTERN.matcher(uri);
        if (m.matches()) {
            return Map.of("enum", AdminLogEnum.MODIFY_NEWS, "targetId", Long.parseLong(m.group(1)));
        }

        m = DELETE_NEWS_PATTERN.matcher(uri);
        if (m.matches()) {
            return Map.of("enum", AdminLogEnum.DELETE_NEWS, "targetId", Long.parseLong(m.group(1)));
        }

        m = SUSPEND_MEMBER_PATTERN.matcher(uri);
        if (m.matches()) {
            return Map.of("enum", AdminLogEnum.SUSPEND_MEMBER, "targetId", m.group(1));
        }

        m = MODIFY_MEMBER_PATTERN.matcher(uri);
        if (m.matches()) {
            return Map.of("enum", AdminLogEnum.MODIFY_MEMBER, "targetId", m.group(1));
        }
        return null;
    }
}

