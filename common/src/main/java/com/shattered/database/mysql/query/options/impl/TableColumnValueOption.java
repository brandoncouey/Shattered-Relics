package com.shattered.database.mysql.query.options.impl;

import com.shattered.database.mysql.query.command.impl.InsertCommand;
import com.shattered.database.mysql.query.command.impl.UpdateCommand;
import com.shattered.database.mysql.query.command.SQLCommand;
import com.shattered.database.mysql.query.options.SQLOption;

import java.util.List;

public class TableColumnValueOption extends SQLOption {
    private String name;
    private Object value;

    public TableColumnValueOption(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public int getLimit() {
        return -1;
    }

    public List<Class<? extends SQLCommand>> getApplicableCommands() {
        this.APPLICABLES.add(UpdateCommand.class);
        this.APPLICABLES.add(InsertCommand.class);
        return this.APPLICABLES;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }
}
