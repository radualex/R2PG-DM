package com.java.r2pgdm;

import lombok.Getter;
import lombok.Setter;

public class Config {
    @Getter
    @Setter
    public String Driver;
    @Getter
    @Setter
    public String Database;
    @Getter
    @Setter
    public String ConnectionString;

    public Config(String connString, String driver, String db) {
        this.Driver = driver;
        this.Database = db;
        this.ConnectionString = connString;
    }

    public Config(String connString) {
        this.Driver = null;
        this.Database = null;
        this.ConnectionString = connString;
    }
}