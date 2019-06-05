package com.java.r2pgdm.graph;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Node {
    @Getter
    @Setter
    public String Id;
    @Getter
    @Setter
    public String Label;
    @Getter
    @Setter
    public List<Property> Props;

    public Node(String id, String label) {
        this.Id = id;
        this.Label = label;
        this.Props = new ArrayList<>();
    }

    public void AddProp(Property p) {
        Props.add(p);
    }
}