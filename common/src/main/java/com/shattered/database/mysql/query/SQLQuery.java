package com.shattered.database.mysql.query;

import java.util.HashMap;
import java.util.Map;

public abstract class SQLQuery {


    private Map<Integer, Object> PARAMETERS = new HashMap();

    public SQLQuery() {
    }

    public abstract String construct();

    public void addParameter(Object value) {
        this.getParameters().put(this.getParameters().size() + 1, value);
    }

    public Map<Integer, Object> getParameters() {
        return this.PARAMETERS;
    }
}