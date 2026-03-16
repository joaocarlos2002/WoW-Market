package com.wowmarket.wowmarket.domain;

public enum BlizzardRegion {

    US("us"),
    EU("eu"),
    KR("kr"),
    TW("tw"),
    CN("cn");

    private final String value;

    BlizzardRegion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}