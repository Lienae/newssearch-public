package com.tjeoun.newssearch.enums;

public enum NewsCategory {
    POLITICS("정치"),
    SOCIAL("사회"),
    SPORTS("스포츠"),
    CULTURE("문화"),
    ECONOMY("경제");

    private final String displayName;

    NewsCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
