package com.java.r2pgdm;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.opencsv.CSVWriter;

public class Export {
    public static void GenerateCSVs() {
        GenerateCSV(OutputConnection.GetNodeData(), "nodes.csv", new String[] { "nodeid", "label" });
        GenerateCSV(OutputConnection.GetPropertyData(), "properties.csv", new String[] { "propid", "key", "value" });
        GenerateCSV(OutputConnection.GetEdgeData(), "edges.csv", new String[] { "edgeid", "srcid", "tgtid", "label" });
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
