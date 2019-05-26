package com.java.r2pgdm;

import java.sql.*;
import java.text.MessageFormat;
import java.util.List;

public class PsqlGraph implements ISQL {
    private Connection _con;

    public PsqlGraph(String url, Mapping mapping) {
        Connect(url);
        CreateGraphSQL(mapping);
    }

    private void Connect(String url) {
        try {
            _con = DriverManager.getConnection(url);
            System.out.println("Connection established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void DropTablesIfExists() {
        try {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS node;");
            stmt.executeUpdate("DROP TABLE IF EXISTS edge;");
            stmt.executeUpdate("DROP TABLE IF EXISTS property;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreateNodeTable(List<Node> nodes) {
        try {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate("CREATE TABLE node(id INTEGER NOT NULL, label VARCHAR(100), PRIMARY KEY (id));");
            nodes.forEach(n -> {
                try {
                    Statement st = _con.createStatement();
                    String query = "INSERT INTO node VALUES(".concat(n.Id).concat(",'").concat(n.Label).concat("');");
                    st.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreateEdgeTable(List<Edge> edges) {
        try {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate(
                    "CREATE TABLE edge(id INTEGER NOT NULL, srcId VARCHAR(100), tgtId VARCHAR(100), label VARCHAR(100), PRIMARY KEY (id));");
            edges.forEach(edge -> {
                try {
                    Statement st = _con.createStatement();
                    Object[] args = { edge.Id, edge.SrcId, edge.TgtId, edge.Label };
                    String query = MessageFormat.format("INSERT INTO edge VALUES ({0}, ''{1}'', ''{2}'', ''{3}'');",
                            args);
                    st.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreatePropertyTable(List<Property> props) {
        try {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate(
                    "CREATE TABLE property(id INTEGER NOT NULL, key VARCHAR(100), value VARCHAR(100), PRIMARY KEY (id, key));");
            props.forEach(p -> {
                try {
                    Statement st = _con.createStatement();
                    Object[] args = { p.Id, p.Key, p.Value };
                    String query = MessageFormat.format("INSERT INTO property VALUES ({0}, ''{1}'', ''{2}'');",
                            args);
                    st.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CreateGraphSQL(Mapping mapping) {
        DropTablesIfExists();
        CreateNodeTable(mapping.Nodes);
        CreateEdgeTable(mapping.Edges);
        CreatePropertyTable(mapping.Properties);
        System.out.println("Mapping - Done.");
    }
}
