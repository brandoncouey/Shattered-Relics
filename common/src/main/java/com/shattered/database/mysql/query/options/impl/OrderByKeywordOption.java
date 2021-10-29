package com.shattered.database.mysql.query.options.impl;


import com.shattered.database.mysql.command.SQLCommand;
import com.shattered.database.mysql.command.impl.SelectCommand;
import com.shattered.database.mysql.query.options.SQLOption;

import java.util.List;

public class OrderByKeywordOption extends SQLOption {
    private String name;
    private OrderByDirection direction;

    public OrderByKeywordOption(String name, OrderByDirection direction) {
        this.name = name;
        this.direction = direction;
    }

    public int getLimit() {
        return 1;
    }

    public List<Class<? extends SQLCommand>> getApplicableCommands() {
        this.APPLICABLES.add(SelectCommand.class);
        return this.APPLICABLES;
    }

    public String getName() {
        return this.name;
    }

    public OrderByDirection getDirection() {
        return this.direction;
    }

    public static enum OrderByDirection {
        ASCENDING,
        DESCENDING;

        private OrderByDirection() {
        }
    }
}