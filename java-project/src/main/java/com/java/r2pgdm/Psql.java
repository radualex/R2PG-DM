package com.java.r2pgdm;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.naming.spi.DirStateFactory.Result;

import com.java.r2pgdm.graph.Edge;
import com.java.r2pgdm.graph.Node;
import com.java.r2pgdm.graph.Property;

public class Psql {

    private static final int FIVE = 5;
    private static final String[] TYPES = new String[] { "TABLE" };
    private Connection _con;
    private DatabaseMetaData _metaData;

    public Psql(String url) {
        Connect(url);
        GetMetaData();
    }

    private void Connect(String url) {
        try {
            _con = DriverManager.getConnection(url);
            System.out.println("Connection Psql established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void GetMetaData() {
        try {
            _metaData = _con.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> GetTableName() {
        List<String> tables = new ArrayList<String>();
        try {
            ResultSet rs = _metaData.getTables(null, null, "%", TYPES);
            while (rs.next()) {
                tables.add(rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return tables;
        }
    }

    public List<CompositeForeignKey> GetForeignKeys(String tableName) {
        List<CompositeForeignKey> Fks = new ArrayList<CompositeForeignKey>();
        try {
            try (ResultSet foreignKeys = _metaData.getImportedKeys(null, null, tableName)) {
                while (foreignKeys.next()) {
                    boolean flag = false;
                    String st = foreignKeys.getString("FKTABLE_NAME");
                    String tt = foreignKeys.getString("PKTABLE_NAME");
                    String sa = foreignKeys.getString("FKCOLUMN_NAME");
                    String ta = foreignKeys.getString("PKCOLUMN_NAME");
                    Integer keySeq = Integer.parseInt(foreignKeys.getString("KEY_SEQ"));
                    ForeignKey tempFk = new ForeignKey(st, tt, sa, ta);

                    for (int i = 0; i < Fks.size() && !flag; i++) {
                        CompositeForeignKey currentFk = Fks.get(i);
                        if (keySeq > 1) {
                            currentFk.AddForeignKey(tempFk);
                            flag = true;
                        }
                    }

                    if (!flag) {
                        CompositeForeignKey cfk = new CompositeForeignKey();
                        cfk.AddForeignKey(tempFk);
                        Fks.add(cfk);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return Fks;
        }
    }

    // #region Helpers
    private Integer GetTupleIdFromRelation(String relName, String val, String key) {
        String sql = "SELECT (ctid::text::point)[1]::bigint AS rId FROM ".concat(relName).concat(" WHERE ").concat(val)
                .concat("='").concat(key).concat("';");

        try {
            Statement stmt = _con.createStatement();
            ResultSet values = stmt.executeQuery(sql);
            while (values.next()) {
                if (values.getRow() == 1) {
                    return values.getInt(1);
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Column> JoinFks(CompositeForeignKey cfk) {
        String sqlSel = "SELECT ";
        String sqlWhe = " ON ";
        for (int i = 0; i < cfk.ForeignKeys.size(); i++) {
            ForeignKey fk = cfk.ForeignKeys.get(i);
            sqlSel = sqlSel.concat(fk.SourceTable).concat(".").concat(fk.SourceAttribute).concat(",");
            sqlWhe = sqlWhe.concat(fk.SourceTable).concat(".").concat(fk.SourceAttribute).concat(" = ")
                    .concat(fk.TargetTable).concat(".").concat(fk.TargetAttribute).concat(" AND ");
        }
        sqlSel = sqlSel.substring(0, sqlSel.length() - 1);
        sqlWhe = sqlWhe.substring(0, sqlWhe.length() - 5);
        String sql = sqlSel.concat(" FROM ").concat(cfk.SourceTable).concat(" INNER JOIN ").concat(cfk.TargetTable)
                .concat(sqlWhe).concat(";");

        try {
            Statement stmt = _con.createStatement();
            ResultSet values = stmt.executeQuery(sql);
            ResultSetMetaData valuesMd = values.getMetaData();
            List<Column> local = new ArrayList<>();
            // Join and create columns for each tuple. Column represents one fk with value.
            // (no duplicates allowed.)
            while (values.next()) {
                int cols = valuesMd.getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    String currentVal = values.getString(i);
                    String relName = cfk.SourceTable;
                    ForeignKey currFk = cfk.ForeignKeys.get(i - 1);
                    Column newCol = new Column(relName, valuesMd.getColumnLabel(i), currentVal, currFk.TargetTable,
                            currFk.TargetAttribute);
                    Optional<Column> checkExists = local.stream()
                            .filter(c -> c.SourceAttribute.equals(newCol.SourceAttribute)
                                    && c.SourceRelationName.equals(newCol.SourceRelationName)
                                    && c.TargetAttribute.equals(newCol.TargetAttribute)
                                    && c.TargetRelationName.equals(newCol.TargetRelationName)
                                    && c.Value.equals(newCol.Value))
                            .findFirst();
                    if (!checkExists.isPresent()) {
                        local.add(newCol);
                    }
                }
            }
            return local;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String CreateNode(ResultSet values, String relName) {
        try {
            Integer rId = values.getInt(1);
            String currIdentifier = Identifier.id(Optional.of(rId), Optional.of(relName), null, null, null, null, null)
                    .toString();
            Node n = new Node(currIdentifier, relName);
            PsqlGraph.InsertNodeRow(n);
            return currIdentifier;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void CreateProperty(ResultSet values, ResultSetMetaData valuesMd, String currIdentifier) {
        try {
            int length = valuesMd.getColumnCount();
            for (int i = 2; i <= length; i++) {
                String currAtt = valuesMd.getColumnName(i);
                Object currVal = values.getObject(i);
                if (currVal != null) {
                    Property p = new Property(currIdentifier, currAtt, currVal.toString());
                    PsqlGraph.InsertPropertyRow(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // #endregion

    public void CreateNodesAndProperties(String relName) {
        String sql = "SELECT (ctid::text::point)[1]::bigint AS rId, * FROM ".concat(relName);
        try {
            PreparedStatement stmt = _con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.setFetchSize(FIVE);
            ResultSet values = stmt.executeQuery();
            ResultSetMetaData valuesMd = values.getMetaData();
            while (values.next()) {
                String currIdentifier = CreateNode(values, relName);
                CreateProperty(values, valuesMd, currIdentifier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CreateEdges(CompositeForeignKey cfk) {
        try {
            List<Column> results = JoinFks(cfk);

            List<String> fksR = new ArrayList<>();
            List<String> fksS = new ArrayList<>();

            // Create set of foreign keys
            for (int i = 0; i < cfk.ForeignKeys.size(); i++) {
                ForeignKey fk = cfk.ForeignKeys.get(i);
                fksR.add(fk.SourceAttribute);
                fksS.add(fk.TargetAttribute);
            }

            Integer rId = -1, sId = -1;

            // Get tuple ids.
            for (int z = 0; z < results.size(); z++) {
                Column curr = results.get(z);
                rId = GetTupleIdFromRelation(curr.SourceRelationName, curr.SourceAttribute, curr.Value);
                sId = GetTupleIdFromRelation(curr.TargetRelationName, curr.TargetAttribute, curr.Value);
            }

            if (rId == -1 || sId == -1) {
                throw new NullPointerException("rId or sId is -1.");
            }

            // Create edges here: (take into consideration composed Fks (size of results /
            // size of foreign keys composing the Composed fk))
            int length = results.size() / cfk.ForeignKeys.size();

            for (int z = 0; z < length; z++) {
                Column curr = results.get(z);
                // for each value -> get the node ids of the nodes with label
                // cfk.SourceTable/cfk.TargetTable
                // and has a property value = curr.Value
                List<String> sNodeIds = PsqlGraph.JoinNodeAndProperty(curr.SourceRelationName, curr.SourceAttribute,
                        curr.Value);
                List<String> tNodeIds = PsqlGraph.JoinNodeAndProperty(curr.TargetRelationName, curr.TargetAttribute,
                        curr.Value);
                // For all ids obtained -> create edge from all source ids to all target ids.
                for (int i = 0; i < sNodeIds.size(); i++) {
                    String sNodeId = sNodeIds.get(i);
                    for (int j = 0; j < tNodeIds.size(); j++) {
                        Integer id = Identifier.id(Optional.of(rId), Optional.of(cfk.SourceTable), null,
                                Optional.of(sId), Optional.of(cfk.TargetTable), Optional.of(fksR), Optional.of(fksS));
                        String tNodeId = tNodeIds.get(j);
                        PsqlGraph.InsertEdgeRow(new Edge(id.toString(), sNodeId, tNodeId,
                                cfk.SourceTable.concat("-").concat(cfk.TargetTable)));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
