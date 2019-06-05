package com.java.r2pgdm;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class IdDataModel {
    @Getter
    @Setter
    public Integer RIdR;

    @Getter
    @Setter
    public String LabelR;

    @Getter
    @Setter
    public Integer RIdS;

    @Getter
    @Setter
    public String LabelS;

    @Getter
    @Setter
    public List<String> FksR;

    @Getter
    @Setter
    public List<String> FksS;

    public IdDataModel(Integer rId, String label) {
        this.RIdR = rId;
        this.LabelR = label;
    }

    public IdDataModel(Integer rIdR, String labelR, Integer rIdS, String labelS, List<String> fksR, List<String> fksS) {
        this.RIdR = rIdR;
        this.LabelR = labelR;
        this.RIdS = rIdS;
        this.LabelS = labelS;
        this.FksR = fksR;
        this.FksS = fksS;
    }
}