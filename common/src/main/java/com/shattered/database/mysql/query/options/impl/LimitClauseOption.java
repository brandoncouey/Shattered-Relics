package com.shattered.database.mysql.query.options.impl;


import com.shattered.database.mysql.query.command.SQLCommand;
import com.shattered.database.mysql.query.command.impl.SelectCommand;
import com.shattered.database.mysql.query.options.SQLOption;

import java.util.List;

public class LimitClauseOption extends SQLOption {
    private int maximum;
    private int position;

    public LimitClauseOption(int maximum, int position) {
        this.maximum = maximum;
        this.position = position;
    }

    public LimitClauseOption(int maximum) {
        this(maximum, 0);
    }

    public int getLimit() {
        return 1;
    }

    public List<Class<? extends SQLCommand>> getApplicableCommands() {
        this.APPLICABLES.add(SelectCommand.class);
        return this.APPLICABLES;
    }

    public int getMaxmimum() {
        return this.maximum;
    }

    public int getPosition() {
        return this.position;
    }
}
