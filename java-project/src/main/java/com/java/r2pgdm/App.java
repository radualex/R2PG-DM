package com.java.r2pgdm;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class App {
    @Getter
    @Setter
    private static List<Table> Tables;

    @Getter
    @Setter
    private static Mapping Mapping;

    public static void main(String[] args) {
        Tables = new ArrayList<Table>();
        String url = "jdbc:postgresql://localhost/r2pgdm?user=radu&password=root";
        Psql psql = new Psql(url);
        List<String> tables = psql.GetTableName();
        tables.forEach(t -> {
            CompositePrimaryKey pk = psql.GetPrimaryKey(t);
            List<CompositeForeignKey> fks = psql.GetForeignKeys(t);
            List<Column> cols = psql.GetColumns(t);
            Table table = new Table(t, pk, fks, cols);
            Tables.add(table);
        });

        psql.UpdateValues(Tables);

        Mapping = new Mapping(Tables);

        System.out.println();

    }

    public static Table FindTable(String tName) {
        return Tables.stream().filter(t -> t.TableName.equals(tName)).findFirst().get();
    }
}
