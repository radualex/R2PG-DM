package com.java.r2pgdm;

import lombok.Getter;
import lombok.Setter;

public class Property {
    @Getter
    @Setter
    public String Id;
    @Getter
    @Setter
    public String Key;
    @Getter
    @Setter
    public String Value;

    public Property(String id, String key, String val) {
        this.Id = id;
        this.Key = key;
        this.Value = val;
    }
}