package com.java.r2pgdm;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CompositePrimaryKey {
    @Getter
    @Setter
    public List<PrimaryKey> PrimaryKeys;

    public CompositePrimaryKey() {
        this.PrimaryKeys = new ArrayList<PrimaryKey>();
    }

    public void AddPrimaryKey(PrimaryKey pk) {
        this.PrimaryKeys.add(pk);
    }
}