package com.ludistudy.smsroutingservice.model;

public enum Carrier {
    TELSTRA("Telstra"),
    OPTUS("Optus"),
    SPARK("Spark"),
    GLOBAL("Global");

    private final String displayName;

    Carrier(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
