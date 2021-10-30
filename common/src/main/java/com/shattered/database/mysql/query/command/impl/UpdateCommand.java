package com.shattered.database.mysql.query.command.impl;

import com.shattered.database.mysql.query.command.SQLCommand;
import com.shattered.database.mysql.query.options.SQLOption;
import com.shattered.database.mysql.query.options.impl.TableColumnValueOption;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;

import java.util.Iterator;
import java.util.List;

public class UpdateCommand extends SQLCommand {
    public UpdateCommand(String table) {
        super(table);
    }

    public String construct() {
        String query = this.getQuery();
        query = query.replace("<TABLE>", this.getTable());
        List<? extends SQLOption> columns = this.getOptionsByType(TableColumnValueOption.class);
        if (columns.isEmpty()) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator var5 = columns.iterator();

            while(var5.hasNext()) {
                TableColumnValueOption column = (TableColumnValueOption)var5.next();
                builder.append(column.getName() + "=?").append(",");
                this.addParameter(column.getValue());
            }

            query = query.replace("<VALUES>", builder.substring(0, builder.length() - 1));
            List<? extends SQLOption> conditions = this.getOptionsByType(WhereConditionOption.class);
            if (conditions.isEmpty()) {
                query = query.replace("<CONDITIONS>", "");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("WHERE ");

                for(int i = 0; i < conditions.size(); ++i) {
                    WhereConditionOption condition = (WhereConditionOption)conditions.get(i);
                    sb.append(condition.getName() + condition.getOperator() + "?");
                    if (i < conditions.size() - 1) {
                        sb.append(" AND ");
                    }

                    this.addParameter(condition.getValue());
                }

                query = query.replace("<CONDITIONS>", sb.toString());
            }

            return query;
        }
    }

    public String getQuery() {
        return "UPDATE <TABLE> SET <VALUES> <CONDITIONS>";
    }
}
