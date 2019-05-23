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

    public CompositePrimaryKey GetPrimaryKey(String tableName) {
        CompositePrimaryKey pk = new CompositePrimaryKey();
        try {
            try (ResultSet primaryKeys = _metaData.getPrimaryKeys(null, null, tableName)) {
                while (primaryKeys.next()) {
                    PrimaryKey tempPk = new PrimaryKey(tableName, primaryKeys.getString("COLUMN_NAME"));
                    pk.AddPrimaryKey(tempPk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return pk;
        }
    }

    // Grouping is not correct (person1, person2) are 2 different keys
    public List<CompositeForeignKey> GetForeignKeys(String tableName) {
        List<CompositeForeignKey> Fks = new ArrayList<CompositeForeignKey>();
        try {
            try (ResultSet foreignKeys = _metaData.getImportedKeys(null, null, tableName)) {
                while (foreignKeys.next()) {
                    boolean flag = false;
                    String st = foreignKeys.getString("FKTABLE_NAME");
                    String tt = foreignKeys.getString("PKTABLE_NAME");
                    String sa = foreignKeys.getString("FKCOLUMN_NAME");
                    String ta = foreignKeys.getString("PKCOLUMN_NAME");
                    Integer keySeq = Integer.parseInt(foreignKeys.getString("KEY_SEQ"));
                    ForeignKey tempFk = new ForeignKey(st, tt, sa, ta);

                    for (int i = 0; i < Fks.size() && !flag; i++) {
                        CompositeForeignKey currentFk = Fks.get(i);
                        if (keySeq > 1) {
                            currentFk.AddForeignKey(tempFk);
                            flag = true;
                        }
                    }

                    if (!flag) {
                        CompositeForeignKey cfk = new CompositeForeignKey();
                        cfk.AddForeignKey(tempFk);
                        Fks.add(cfk);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return Fks;
        }
    }

    public List<Column> GetColumns(String tableName) {
        List<Column> cols = new ArrayList<Column>();
        try {
            try (ResultSet columns = _metaData.getColumns(null, null, tableName, null)) {
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    cols.add(new Column(columnName));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return cols;
        }
    }

    public void UpdateValues(List<Table> tables) {
        tables.forEach(table -> {
            String sql = "SELECT * FROM ".concat(table.TableName);
            Statement stmt;
            try {
                stmt = _con.createStatement();
                ResultSet values = stmt.executeQuery(sql);
                while (values.next()) {
                    table.Columns.forEach(col -> {
                        try {
                            String curr = values.getString(col.Attribute);
                            col.Values.add(curr);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    table.setRids(TupleIdentifierGenerator.GenerateNextRId());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
