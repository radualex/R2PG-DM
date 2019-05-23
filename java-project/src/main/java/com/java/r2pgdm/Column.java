package com.java.r2pgdm;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Column {
    @Getter
    @Setter
    public String Attribute;
    @Getter
    @Setter
    public List<String> Values;

    public Column(String att) {
        this.Attribute = att;
        this.Values = new ArrayList<String>();
    }

    public void UpdateValues(String value) {
        this.Values.add(value);
    }
}