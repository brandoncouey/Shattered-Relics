package com.shattered.database.mysql.command;


import com.shattered.database.mysql.query.SQLQuery;
import com.shattered.database.mysql.query.options.SQLOption;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SQLCommand extends SQLQuery {
    private String table;
    private List<SQLOption> options = new ArrayList();

    public SQLCommand(String table) {
        this.table = table;
    }

    public void addOption(SQLOption option) {
        if (option != null) {
            if (option.getApplicableCommands().contains(this.getClass())) {
                if (option.getLimit() != -1) {
                    int count = 0;
                    Iterator var4 = this.getOptions().iterator();

                    while(var4.hasNext()) {
                        SQLOption other = (SQLOption)var4.next();
                        if (other.getClass().isInstance(option)) {
                            ++count;
                        }
                    }

                    if (count >= option.getLimit()) {
                        return;
                    }
                }

                this.getOptions().add(option);
            }
        }
    }

    public SQLCommand addOptions(SQLOption... options) {
        SQLOption[] var5 = options;
        int var4 = options.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            SQLOption option = var5[var3];
            this.addOption(option);
        }

        return this;
    }

    public List<? extends SQLOption> getOptionsByType(Class<? extends SQLOption> type) {
        List<SQLOption> options = new ArrayList();
        Iterator var4 = this.getOptions().iterator();

        while(var4.hasNext()) {
            SQLOption option = (SQLOption)var4.next();
            if (type.isInstance(option)) {
                options.add(option);
            }
        }

        return options;
    }

    public SQLOption getOptionByType(Class<? extends SQLOption> type) {
        Iterator var3 = this.getOptions().iterator();

        while(var3.hasNext()) {
            SQLOption option = (SQLOption)var3.next();
            if (type.isInstance(option)) {
                return option;
            }
        }

        return null;
    }

    public abstract String getQuery();

    public String getTable() {
        return this.table;
    }

    public List<SQLOption> getOptions() {
        return this.options;
    }
}
