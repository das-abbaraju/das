package com.picsauditing.util;


public class SelectOption {
    private String key;
    private String value;

    public SelectOption(Integer key, String value) {
        this.key = key.toString();
        this.value = value;
    }

    public SelectOption(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
