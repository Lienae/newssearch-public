package com.tjeoun.newssearch.enums;

public enum NewsMediaCompany {
    DONGA("동아일보"),
    YTN("YTN"),
    HANI("한겨레"),
    KHAN("경향신문"),
    JOONGANG("중앙일보");

    private final String displayName;

    NewsMediaCompany(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
