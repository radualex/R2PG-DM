package com.java.r2pgdm;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.opencsv.CSVWriter;

public class Export {
    public static void GenerateCSVs() {
        GenerateCSV(PsqlGraph.GetNodeData(), "nodes.csv", new String[] { "id", "label" });
        GenerateCSV(PsqlGraph.GetPropertyData(), "properties.csv", new String[] { "id", "key", "value" });
        GenerateCSV(PsqlGraph.GetEdgeData(), "edges.csv", new String[] { "id", "srcid", "tgtid", "label" });
    }

    private static void GenerateCSV(ResultSet res, String csv, String[] headers) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csv), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            writer.writeNext(headers);
            writer.writeAll(res, false);
            writer.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}