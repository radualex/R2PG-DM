package com.java.r2pgdm;

import lombok.Getter;
import lombok.Setter;

public class PrimaryKey {
    @Getter
    @Setter
    public String TableName;
    @Getter
    @Setter
    public String AttributeName;

    public PrimaryKey(String tn, String an) {
        this.TableName = tn;
        this.AttributeName = an;
    }
}