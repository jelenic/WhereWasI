package com.example.jakov.wherewasi;

public class ActiveLog {
    private static ActiveLog instance;
    private String val="Default log";

    public static ActiveLog getInstance() {
        if (instance == null)
            instance = new ActiveLog();
        return instance;
    }

    private ActiveLog() {
    }

    public String getValue() {
        return val;
    }

    public void setValue(String value) {
        this.val = value;
    }
}

