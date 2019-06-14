package com.java.r2pgdm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

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
                    .concat(input.Database).concat("?user=").concat(input.User).concat("&password=")
                    .concat(input.Password);
            String urlOutput = "jdbc:".concat(output.Driver).concat("://").concat(output.Host).concat("/")
                    .concat(output.Database).concat("?user=").concat(output.User).concat("&password=")
                    .concat(output.Password);
            Psql psql = new Psql(urlInput);
            new PsqlGraph(urlOutput);

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
