package com.java.r2pgdm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import lombok.Getter;
import lombok.Setter;

public class Mapping {

    private static final String VALUE = "value";
    private static final String KEY = "key";
    private static final String TGTID = "tgtid";
    private static final String SRCID = "srcid";
    private static final String LABEL = "label";
    private static final String ID = "id";
    private static final String PROPERTY = "property";
    private static final String EDGE = "edge";
    private static final String NODE = "node";

    @Getter
    @Setter
    public Table Nodes;

    @Getter
    @Setter
    public Table Edges;

    @Getter
    @Setter
    public Table Properties;

    public Mapping(List<Table> tables) {
        InitializeMapping();
        GenerateNodes(tables);
        tables.forEach(t -> GenerateEdgesAndProps(t));
        System.out.println();
    }

    private void InitializeMapping() {
        Nodes = new Table(NODE);
        Edges = new Table(EDGE);
        Properties = new Table(PROPERTY);
        Nodes.Columns.add(new Column(ID));
        Nodes.Columns.add(new Column(LABEL));
        Nodes.PrimaryKey = new CompositePrimaryKey();
        Nodes.PrimaryKey.AddPrimaryKey(new PrimaryKey(NODE, ID));
        Edges.Columns.add(new Column(ID));
        Edges.Columns.add(new Column(SRCID));
        Edges.Columns.add(new Column(TGTID));
        Edges.Columns.add(new Column(LABEL));
        Edges.PrimaryKey = new CompositePrimaryKey();
        Edges.PrimaryKey.AddPrimaryKey(new PrimaryKey(EDGE, ID));
        Properties.Columns.add(new Column(ID));
        Properties.Columns.add(new Column(KEY));
        Properties.Columns.add(new Column(VALUE));
        Properties.PrimaryKey = new CompositePrimaryKey();
        Properties.PrimaryKey.AddPrimaryKey(new PrimaryKey(PROPERTY, ID));
        Properties.PrimaryKey.AddPrimaryKey(new PrimaryKey(PROPERTY, KEY));
    }

    public void GenerateEdgesAndProps(Table r) {
        for (int i = 0; i < r.ForeignKeys.size(); i++) {
            CompositeForeignKey cFk = r.ForeignKeys.get(i);
            GenerateEdge(r, App.FindTable(cFk.TargetTable), cFk);
        }
        GenerateProperty(r);
    }

    // Id---Label
    private void GenerateNodes(List<Table> listR) {
        listR.forEach(r -> {
            for (int i = 0; i < r.NrTuples; i++) {
                int id = Identifier.id(r.getRids().get(i), r.TableName);
                this.Nodes.FindColumn(ID).UpdateValues(Integer.toString(id));
                this.Nodes.FindColumn(LABEL).UpdateValues(r.TableName);
            }
        });
    }

    private Pair<List<String>, List<String>> GetFksForEdges(CompositeForeignKey cFk) {
        List<String> attSource = new ArrayList<>();
        List<String> attTarget = new ArrayList<>();

        cFk.ForeignKeys.forEach(fk -> {
            attSource.add(fk.SourceAttribute);
            attTarget.add(fk.TargetAttribute);
        });

        return new Pair<List<String>, List<String>>(attSource, attTarget);
    }

    // Id---srcId---tgtId---label
    // Check if values match as well for foreign keys
    private void GenerateEdge(Table r, Table s, CompositeForeignKey cfk) {
        Pair<List<String>, List<String>> fks = GetFksForEdges(cfk);
        List<Pair<String, String>> mapR = Nodes.getTuple(r.TableName, ID, LABEL);
        List<Pair<String, String>> mapS = Nodes.getTuple(s.TableName, ID, LABEL);
        for (int i = 0; i < mapR.size(); i++) {
            Pair<String, String> currentSource = mapR.get(i);
            for (int j = 0; j < mapS.size(); j++) {
                Pair<String, String> currentTarget = mapS.get(j);
                int id = Identifier.id(r.getRids().get(i), r.TableName, s.getRids().get(j), s.TableName,
                        fks.getValue0(), fks.getValue1());
                this.Edges.FindColumn(ID).UpdateValues(Integer.toString(id));
                this.Edges.FindColumn(SRCID).UpdateValues(currentSource.getValue0());
                this.Edges.FindColumn(TGTID).UpdateValues(currentTarget.getValue0());
                this.Edges.FindColumn(LABEL)
                        .UpdateValues(currentSource.getValue1().concat("-").concat(currentTarget.getValue1()));
            }
        }
    }

    // Id---key---value
    private void GenerateProperty(Table r) {

    }
}