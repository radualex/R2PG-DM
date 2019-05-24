package com.java.r2pgdm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import lombok.Getter;
import lombok.Setter;

public class Mapping {

    @Getter
    @Setter
    public List<Node> Nodes;

    @Getter
    @Setter
    public List<Edge> Edges;

    @Getter
    @Setter
    public List<Property> Properties;

    public Mapping(List<Table> tables) {
        InitializeMapping();
        GenerateNodes(tables);
        tables.forEach(t -> GenerateProperty(t));
        UpdateNodesWithProps();
        tables.forEach(t -> GenerateEdges(t));
    }

    private void InitializeMapping() {
        this.Nodes = new ArrayList<>();
        this.Edges = new ArrayList<>();
        this.Properties = new ArrayList<>();
    }

    private void UpdateNodesWithProps() {
        for (int i = 0; i < Nodes.size(); i++) {
            Node current = Nodes.get(i);
            List<Property> props = Properties.stream().filter(p -> p.Id.equals(current.Id))
                    .collect(Collectors.toList());
            for (int j = 0; j < props.size(); j++) {
                Property p = props.get(j);
                current.AddProp(p);
            }
        }
    }

    public void GenerateEdges(Table r) {
        for (int i = 0; i < r.ForeignKeys.size(); i++) {
            CompositeForeignKey cFk = r.ForeignKeys.get(i);
            GenerateEdge(r, App.FindTable(cFk.TargetTable), cFk);
        }
    }

    // Id---Label
    private void GenerateNodes(List<Table> listR) {
        for (int i = 0; i < listR.size(); i++) {
            Table r = listR.get(i);
            for (int j = 0; j < r.NrTuples; j++) {
                String id = Integer.toString(Identifier.id(r.getRids().get(j), r.TableName));
                this.Nodes.add(new Node(id, r.TableName));
            }
        }
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
    private void GenerateEdge(Table r, Table s, CompositeForeignKey cfk) {
        try {
            Pair<List<String>, List<String>> fks = GetFksForEdges(cfk);

            if (fks.getValue0().size() != fks.getValue1().size()) {
                throw new RuntimeException("Foreign keys different size (edges).");
            }

            String srcTable = r.TableName;
            String tgtTable = s.TableName;
            List<Node> srcNodes = Nodes.stream().filter(n -> n.Label.equals(srcTable)).collect(Collectors.toList());
            List<Node> tgtNodes = Nodes.stream().filter(n -> n.Label.equals(tgtTable)).collect(Collectors.toList());
            Integer rId = -1;
            Integer sId = -1;

            List<Triplet<String, String, Integer>> toBeEdge = new ArrayList<>();

            for (int i = 0; i < fks.getValue0().size(); i++) {
                String sourceAtt = fks.getValue0().get(i);
                String targetAtt = fks.getValue1().get(i);

                for (int j = 0; j < srcNodes.size(); j++) {
                    Node currSrcNode = srcNodes.get(j);
                    Property currSrcProp = currSrcNode.Props.stream().filter(p -> p.Key.equals(sourceAtt)).findFirst()
                            .get();

                    for (int z = 0; z < tgtNodes.size(); z++) {
                        Node currTgtNode = tgtNodes.get(z);
                        Property currTgtProp = currTgtNode.Props.stream().filter(p -> p.Key.equals(targetAtt))
                                .findFirst().get();

                        if (currSrcProp.Value.equals(currTgtProp.Value)) {
                            rId = r.GetIdFromProperty(currSrcProp);
                            sId = s.GetIdFromProperty(currTgtProp);
                            Optional<Triplet<String, String, Integer>> exists = toBeEdge.stream().filter(
                                    t -> t.getValue0().equals(currSrcNode.Id) && t.getValue1().equals(currTgtNode.Id))
                                    .findFirst();
                            if (exists.isPresent()) {
                                Triplet<String, String, Integer> triplet = exists.get();
                                Integer counter = triplet.getValue2();
                                toBeEdge.remove(triplet);
                                toBeEdge.add(new Triplet<String, String, Integer>(triplet.getValue0(),
                                        triplet.getValue1(), ++counter));
                            } else {
                                Triplet<String, String, Integer> triplet = new Triplet<String, String, Integer>(
                                        currSrcNode.Id, currTgtNode.Id, 1);
                                toBeEdge.add(triplet);
                            }
                        }
                    }
                }
            }

            if (rId.equals(-1) || sId.equals(-1)) {
                throw new RuntimeException("rId or sId is -1 (Check GetIdFromProperty in Table.");
            }

            for (int i = 0; i < toBeEdge.size(); i++) {
                Triplet<String, String, Integer> currPossibleEdge = toBeEdge.get(i);
                String srcId = currPossibleEdge.getValue0();
                String tgtId = currPossibleEdge.getValue1();
                Integer counter = currPossibleEdge.getValue2();
                if (counter.equals(fks.getValue0().size())) {
                    int id = Identifier.id(rId, r.TableName, sId, s.TableName, fks.getValue0(), fks.getValue1());
                    Edges.add(
                            new Edge(Integer.toString(id), srcId, tgtId, r.TableName.concat("-").concat(s.TableName)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Id---key---value
    private void GenerateProperty(Table r) {
        List<Node> relevantNodes = Nodes.stream().filter(n -> n.Label.equals(r.TableName)).collect(Collectors.toList());
        for (int i = 0; i < relevantNodes.size(); i++) {
            Node currNode = relevantNodes.get(i);
            for (int j = 0; j < r.Columns.size(); j++) {
                Column col = r.Columns.get(j); // current column
                String val = col.Values.get(i); // current tuple
                if (val != null) {
                    Properties.add(new Property(currNode.Id, col.Attribute, val));
                }
            }
        }
    }
}