package com.java.r2pgdm;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Identifier {
    @Getter
    @Setter
    private static Integer GlobalID = 1;

    public static Integer id(Integer rId, String label) {
        return GlobalID++;
    }

    public static Integer id(Integer rIdR, String labelR, Integer rIdS, String labelS, List<String> fksR,
            List<String> fksS) {
        try {
            if (fksR.size() != fksS.size()) {
                throw new RuntimeException("The foreign keys size is different.");
            }
            return GlobalID++;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }
}