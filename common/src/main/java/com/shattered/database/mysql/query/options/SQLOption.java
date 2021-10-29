package com.shattered.database.mysql.query.options;

import com.shattered.database.mysql.command.SQLCommand;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLOption {

    protected List<Class<? extends SQLCommand>> APPLICABLES = new ArrayList();

    public SQLOption() {
    }

    public abstract int getLimit();

    public abstract List<Class<? extends SQLCommand>> getApplicableCommands();
}
