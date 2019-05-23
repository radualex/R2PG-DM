package com.java.r2pgdm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

public class CompositeForeignKey {
    @Getter
    @Setter
    List<ForeignKey> ForeignKeys;

    public CompositeForeignKey() {
        this.ForeignKeys = new ArrayList<ForeignKey>();
    }

    public void AddForeignKey(ForeignKey fk) {
        this.ForeignKeys.add(fk);
    }

    public boolean CheckIfForeignKeySameTargetTableNameExists(String targetTableName) {
        List<ForeignKey> checkFk = this.ForeignKeys.stream().filter(p -> p.TargetTable.equals(targetTableName))
                .collect(Collectors.toList());

        if (checkFk.size() != 0) {
            return true;
        }
        return false;
    }
}