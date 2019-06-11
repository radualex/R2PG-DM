package com.java.r2pgdm;

import java.util.List;


//select (ctid::text::point)[1]::bigint AS rId,* from person;

public class App {

    // Get urls from args. Export sql somehow. Add Neo4J.
    public static void main(String[] args) {
        String db = "world";
        String url = "jdbc:postgresql://localhost/".concat(db).concat("?user=radu&password=root");
        String urlGraph = "jdbc:postgresql://localhost/graph?user=radu&password=root";
        Psql psql = new Psql(url);
        new PsqlGraph(urlGraph);

        // Create node + props
        List<String> tables = psql.GetTableName();
        tables.forEach(t -> {
            psql.CreateNodesAndProperties(t);
        });

        // Create edges
        tables.forEach(t -> {
            List<CompositeForeignKey> fks = psql.GetForeignKeys(t);
            fks.forEach(fk -> {
                psql.CreateEdges(fk);
            });
        });

        System.out.println("Mapping - Done.");
        PsqlGraph.Statistics();

    }
}
