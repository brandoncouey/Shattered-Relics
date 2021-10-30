package com.shattered.database.mysql.query.options.impl;



import com.shattered.database.mysql.query.command.impl.UpdateCommand;
import com.shattered.database.mysql.query.command.SQLCommand;
import com.shattered.database.mysql.query.command.impl.DeleteCommand;
import com.shattered.database.mysql.query.command.impl.SelectCommand;
import com.shattered.database.mysql.query.options.SQLOption;

import java.util.List;

public class WhereConditionOption extends SQLOption {
    private String name;
    private Object value;
    private String operator;

    public WhereConditionOption(String name, Object value, String operator) {
        this.name = name;
        this.value = value;
        this.operator = operator;
    }

    public WhereConditionOption(String name, Object value) {
        this(name, value, "=");
    }

    public int getLimit() {
        return -1;
    }

    public List<Class<? extends SQLCommand>> getApplicableCommands() {
        this.APPLICABLES.add(SelectCommand.class);
        this.APPLICABLES.add(UpdateCommand.class);
        this.APPLICABLES.add(DeleteCommand.class);
        return this.APPLICABLES;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public String getOperator() {
        return this.operator;
    }
}
