package com.java.r2pgdm;

import lombok.Getter;
import lombok.Setter;

public class Config {
    @Getter
    @Setter
    public String Driver;
    @Getter
    @Setter
    public String Host;
    @Getter
    @Setter
    public String Database;
    @Getter
    @Setter
    public String User;
    @Getter
    @Setter
    public String Password;

    public Config(String driver, String host, String db, String user, String pass) {
        this.Driver = driver;
        this.Host = host;
        this.Database = db;
        this.User = user;
        this.Password = pass;
    }
}