package com.java.r2pgdm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.javatuples.Pair;

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

    public List<Pair<String, String>> getTuple(String label, String colName1, String colName2) {
        List<Pair<String, String>> map = new ArrayList<Pair<String, String>>();
        List<Integer> matchingIndices = new ArrayList<>();
        Column id = this.FindColumn(colName1);
        Column lbl = this.FindColumn(colName2);

        for (int i = 0; i < lbl.Values.size(); i++) {
            String element = lbl.Values.get(i);

            if (element.equals(label)) {
                matchingIndices.add(i);
            }
        }

        for (int i = 0; i < matchingIndices.size(); i++) {
            Pair<String, String> pair = new Pair<String, String>(id.Values.get(matchingIndices.get(i)), label);
            map.add(pair);
        }

        return map;
    }
}