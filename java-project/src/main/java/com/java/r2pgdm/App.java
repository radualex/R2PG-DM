package com.java.r2pgdm;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class App {
    @Getter
    @Setter
    private static List<Table> Tables;

    public static void main(String[] args) {
        // System.out.println("Hello World!");
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

        System.out.println("1");

        // fks.get(1).ForeignKeys.forEach(fk -> System.out.println(fk.TargetTable + "
        // --- " + fk.SourceTable));
        // fks.forEach(p -> p.ForeignKeys.forEach(fk ->
        // System.out.println(fk.TargetTable + " --- " + fk.SourceTable)));

    }
}
