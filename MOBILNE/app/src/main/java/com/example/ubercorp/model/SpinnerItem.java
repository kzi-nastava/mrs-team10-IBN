package com.example.ubercorp.model;

public class SpinnerItem {
    private String value;
    private String displayName;
    public SpinnerItem(String displayName, String value) {
        this.displayName = displayName;
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    @Override
    public String toString(){
        return displayName;
    }
}
