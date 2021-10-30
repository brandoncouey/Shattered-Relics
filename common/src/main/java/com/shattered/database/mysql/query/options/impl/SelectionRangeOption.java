package com.shattered.database.mysql.query.options.impl;


import com.shattered.database.mysql.query.command.SQLCommand;
import com.shattered.database.mysql.query.command.impl.SelectCommand;
import com.shattered.database.mysql.query.options.SQLOption;

import java.util.List;

public class SelectionRangeOption extends SQLOption {
    private String[] columns;

    public SelectionRangeOption(String column) {
        this.columns = new String[]{column};
    }

    public SelectionRangeOption(String... columns) {
        this.columns = columns;
    }

    public int getLimit() {
        return 1;
    }

    public List<Class<? extends SQLCommand>> getApplicableCommands() {
        this.APPLICABLES.add(SelectCommand.class);
        return this.APPLICABLES;
    }

    public String[] getColumns() {
        return this.columns;
    }
}
