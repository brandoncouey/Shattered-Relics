package com.shattered.database.mysql.command.impl;


import com.shattered.database.mysql.command.SQLCommand;
import com.shattered.database.mysql.query.options.impl.TableColumnValueOption;

import java.util.Iterator;
import java.util.List;

public class InsertCommand extends SQLCommand {
    public InsertCommand(String table) {
        super(table);
    }

    public String construct() {
        String query = this.getQuery();
        query = query.replace("<TABLE>", this.getTable());
        List<TableColumnValueOption> columns = (List<TableColumnValueOption>) this.getOptionsByType(TableColumnValueOption.class);
        if (columns.isEmpty()) {
            return null;
        } else {
            StringBuilder keys = new StringBuilder();
            StringBuilder values = new StringBuilder();
            Iterator var6 = columns.iterator();

            while(var6.hasNext()) {
                TableColumnValueOption column = (TableColumnValueOption)var6.next();
                keys.append(column.getName()).append(",");
                values.append("?").append(",");
                this.addParameter(column.getValue());
            }

            query = query.replace("<KEYS>", keys.substring(0, keys.length() - 1));
            query = query.replace("<VALUES>", values.substring(0, values.length() - 1));
            return query;
        }
    }

    public String getQuery() {
        return "INSERT INTO <TABLE> (<KEYS>) VALUES (<VALUES>)";
    }
}
