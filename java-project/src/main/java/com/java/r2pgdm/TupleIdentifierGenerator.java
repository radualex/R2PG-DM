package com.java.r2pgdm;

public class TupleIdentifierGenerator {

    private static Integer GlobalRId = 1;

    public static Integer GenerateNextRId() {
        return GlobalRId++;
    }
}