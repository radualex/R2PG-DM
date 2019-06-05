package com.java.r2pgdm.graph;

import lombok.Getter;
import lombok.Setter;

public class Edge {
    @Getter
    @Setter
    public String Id;
    @Getter
    @Setter
    public String SrcId;
    @Getter
    @Setter
    public String TgtId;
    @Getter
    @Setter
    public String Label;

    public Edge(String id, String src, String tgt, String lbl) {
        this.Id = id;
        this.SrcId = src;
        this.TgtId = tgt;
        this.Label = lbl;
    }
}