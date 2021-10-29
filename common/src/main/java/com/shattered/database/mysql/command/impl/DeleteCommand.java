package com.shattered.database.mysql.command.impl;


import com.shattered.database.mysql.command.SQLCommand;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;

import java.util.List;

public class DeleteCommand extends SQLCommand {
    public DeleteCommand(String table) {
        super(table);
    }

    public String construct() {
        String query = this.getQuery();
        query = query.replace("<TABLE>", this.getTable());
        List<WhereConditionOption> conditions = (List<WhereConditionOption>) this.getOptionsByType(WhereConditionOption.class);
        if (!conditions.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("WHERE ");

            for(int i = 0; i < conditions.size(); ++i) {
                WhereConditionOption condition = (WhereConditionOption)conditions.get(i);
                builder.append(condition.getName() + condition.getOperator() + "?");
                if (i < conditions.size() - 1) {
                    builder.append(" AND ");
                }

                this.addParameter(condition.getValue());
            }

            query = query.replace("<CONDITIONS>", builder.toString());
        } else {
            query = query.replace("<CONDITIONS>", "");
        }

        return query;
    }

    public String getQuery() {
        return "DELETE FROM <TABLE> <CONDITIONS>";
    }
}