package com.java.r2pgdm;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Table {
    @Getter
    @Setter
    public String TableName;
    @Getter
    @Setter
    public CompositePrimaryKey PrimaryKey;
    @Getter
    @Setter
    public List<CompositeForeignKey> ForeignKeys;
    @Getter
    @Setter
    public List<Column> Columns;

    public Table(String tableName, CompositePrimaryKey pks, List<CompositeForeignKey> fks) {
        this.TableName = tableName;
        this.PrimaryKey = pks;
        this.ForeignKeys = fks;
        this.Columns = null;
    }

    public Table(String tableName, CompositePrimaryKey pks, List<CompositeForeignKey> fks, List<Column> cols) {
        this.TableName = tableName;
        this.PrimaryKey = pks;
        this.ForeignKeys = fks;
        this.Columns = cols;
    }
}