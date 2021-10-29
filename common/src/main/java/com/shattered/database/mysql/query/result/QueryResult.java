package com.shattered.database.mysql.query.result;

import com.shattered.database.mysql.query.SQLQuery;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class QueryResult {
    private SQLQuery query;
    private int rows;
    private ResultSet set;

    public QueryResult(SQLQuery query, int result) {
        this.query = query;
        this.rows = result;
    }

    public QueryResult(SQLQuery query, ResultSet result) {
        this.query = query;
        this.set = result;
    }

    public ResultSet getRow() {
        try {
            boolean state = this.getResultSet().next();
            return state ? this.getResultSet() : null;
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public int getRowCount() {
        try {
            int current = this.getResultSet().getRow();
            this.getResultSet().last();
            int count = this.getResultSet().getRow();
            if (current != 0) {
                this.getResultSet().absolute(current);
            } else {
                this.getResultSet().beforeFirst();
            }

            return count;
        } catch (Exception var3) {
            var3.printStackTrace();
            return 0;
        }
    }

    public List<String> getColumnNames() {
        try {
            ResultSetMetaData meta = this.getResultSet().getMetaData();
            int count = meta.getColumnCount();
            List<String> columns = new ArrayList(count);

            for(int i = 1; i <= count; ++i) {
                columns.add(meta.getColumnName(i));
            }

            return columns;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public void terminate() {
        try {
            if (this.getResultSet() == null) {
                return;
            }

            if (this.getResultSet().isClosed()) {
                return;
            }

            this.getResultSet().close();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public SQLQuery getQuery() {
        return this.query;
    }

    public int getAffectedRows() {
        return this.rows;
    }

    public ResultSet getResultSet() {
        return this.set;
    }
}
