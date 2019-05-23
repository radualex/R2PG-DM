package com.java.r2pgdm;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Psql {
    private static final String[] TYPES = new String[] { "TABLE" };
    Connection _con;
    DatabaseMetaData _metaData;

    public Psql(String url) {
        Connect(url);
        GetMetaData();
    }

    private void Connect(String url) {
        try {
            _con = DriverManager.getConnection(url);
            System.out.println("Connetion established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void GetMetaData() {
        try {
            _metaData = _con.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> GetTableName() {
        List<String> tables = new ArrayList<String>();
        try {
            ResultSet rs = _metaData.getTables(null, null, "%", TYPES);
            while (rs.next()) {
                tables.add(rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return tables;
        }
    }
}
