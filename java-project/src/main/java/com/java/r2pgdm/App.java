package com.java.r2pgdm;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class App {
    @Getter
    @Setter
    private static List<Table> _tables;

    @Getter
    @Setter
    private static Mapping _mapping;

    //Get urls from args. Export sql somehow. Add Neo4J.
    public static void main(String[] args) {
        _tables = new ArrayList<Table>();
        String url = "jdbc:postgresql://localhost/r2pgdm?user=radu&password=root";
        String urlGraph = "jdbc:postgresql://localhost/graph?user=radu&password=root";
        Psql psql = new Psql(url);
        List<String> tables = psql.GetTableName();
        tables.forEach(t -> {
            CompositePrimaryKey pk = psql.GetPrimaryKey(t);
            List<CompositeForeignKey> fks = psql.GetForeignKeys(t);
            List<Column> cols = psql.GetColumns(t);
            Table table = new Table(t, pk, fks, cols);
            _tables.add(table);
        });

        psql.UpdateValues(_tables);

        _mapping = new Mapping(_tables);
        PsqlGraph psqlGraph = new PsqlGraph(urlGraph, _mapping);

    }

    public static Table FindTable(String tName) {
        return _tables.stream().filter(t -> t.TableName.equals(tName)).findFirst().get();
    }
}
