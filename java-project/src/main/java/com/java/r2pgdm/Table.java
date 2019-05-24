package com.java.r2pgdm;

import java.util.ArrayList;
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

    private List<Integer> Rids;
    @Getter
    @Setter
    public Integer NrTuples;

    public Table(String tableName) {
        this.TableName = tableName;
        this.Columns = new ArrayList<Column>();
    }

    public Table(String tableName, CompositePrimaryKey pks, List<CompositeForeignKey> fks) {
        this.TableName = tableName;
        this.PrimaryKey = pks;
        this.ForeignKeys = fks;
        this.Columns = null;
        this.Rids = new ArrayList<Integer>();
    }

    public Table(String tableName, CompositePrimaryKey pks, List<CompositeForeignKey> fks, List<Column> cols) {
        this.TableName = tableName;
        this.PrimaryKey = pks;
        this.ForeignKeys = fks;
        this.Columns = cols;
        this.Rids = new ArrayList<Integer>();
    }

    public Column FindColumn(String att) {
        return this.Columns.stream().filter(c -> c.Attribute.equals(att)).findFirst().get();
    }

    public List<Integer> getRids() {
        return this.Rids;
    }

    public void setRids(Integer i) {
        this.Rids.add(i);
        this.NrTuples = this.Rids.size();
    }

    public Integer GetIdFromProperty(Property p) {
        Column col = Columns.stream().filter(c -> c.Attribute.equals(p.Key)).findFirst().get();
        Integer index = col.Values.indexOf(p.Value);
        return Rids.get(index);
    }

}