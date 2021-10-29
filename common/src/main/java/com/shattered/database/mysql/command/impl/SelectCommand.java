package com.shattered.database.mysql.command.impl;


import com.shattered.database.mysql.command.SQLCommand;
import com.shattered.database.mysql.query.options.impl.LimitClauseOption;
import com.shattered.database.mysql.query.options.impl.OrderByKeywordOption;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;
import com.shattered.database.mysql.query.options.impl.SelectionRangeOption;

import java.util.List;

public class SelectCommand extends SQLCommand {
    public SelectCommand(String table) {
        super(table);
    }

    public String construct() {
        String query = this.getQuery();
        query = query.replace("<TABLE>", this.getTable());
        SelectionRangeOption range = (SelectionRangeOption)this.getOptionByType(SelectionRangeOption.class);
        if (range == null) {
            query = query.replace("<RANGE>", "*");
        } else {
            query = query.replace("<RANGE>", String.join(",", range.getColumns()));
        }

        List<WhereConditionOption> conditions = (List<WhereConditionOption>) this.getOptionsByType(WhereConditionOption.class);
        if (conditions.isEmpty()) {
            query = query.replace("<CONDITIONS>", "");
        } else {
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
        }

        List<OrderByKeywordOption> ordering = (List<OrderByKeywordOption>) this.getOptionsByType(OrderByKeywordOption.class);
        if (ordering.isEmpty()) {
            query = query.replace("<ORDERING>", "");
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("ORDER BY ");

            for(int i = 0; i < ordering.size(); ++i) {
                OrderByKeywordOption order = (OrderByKeywordOption)ordering.get(i);
                builder.append(order.getName() + " " + (order.getDirection() == OrderByKeywordOption.OrderByDirection.ASCENDING ? "ASC" : "DESC"));
                if (i < conditions.size() - 1) {
                    builder.append(",");
                }
            }

            query = query.replace("<ORDERING>", builder.toString());
        }

        LimitClauseOption clause = (LimitClauseOption)this.getOptionByType(LimitClauseOption.class);
        if (clause == null) {
            query = query.replace("<CLAUSE>", "");
        } else {
            query = query.replace("<CLAUSE>", "LIMIT " + clause.getPosition() + ", " + clause.getMaxmimum());
        }

        return query;
    }

    public String getQuery() {
        return "SELECT <RANGE> FROM <TABLE> <CONDITIONS> <ORDERING> <CLAUSE>";
    }
}