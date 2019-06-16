package com.java.r2pgdm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.ini4j.Profile.Section;

public class App {

    public static void main(String[] args) {
        try {
            Wini ini = new Wini(new File("config.ini"));
            Config input = GetConfiguration(ini.get("input"));
            Config output = GetConfiguration(ini.get("output"));
            String urlInput = "jdbc:".concat(input.Driver).concat("://").concat(input.Host).concat("/")
                    .concat(input.Database);
            String urlOutput = "jdbc:".concat(output.Driver).concat("://").concat(output.Host).concat("/")
                    .concat(output.Database);

            Psql psql = new Psql(urlInput, input.User, input.Password);
            new PsqlGraph(urlOutput, output.User, output.Password);

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

            Export.GenerateCSVs();

        } catch (InvalidFileFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Config GetConfiguration(Section section) {
        return new Config(section.get("driver"), section.get("host"), section.get("database"), section.get("user"),
                section.get("password"));
    }
}
