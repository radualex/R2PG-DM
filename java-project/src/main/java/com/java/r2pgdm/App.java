package com.java.r2pgdm;

import java.util.List;

public class App {
    public static void main(String[] args) {
        // System.out.println("Hello World!");
        String url = "jdbc:postgresql://localhost/r2pgdm?user=radu&password=root";
        Psql psql = new Psql(url);
        List<String> tables = psql.GetTableName();

    }
}
