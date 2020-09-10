package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - The class presents the condition of select in sql
 * 
 */

import java.util.List;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.SqlCondition;
import com.usth.hieplnc.storage.api.sql.HVSqlConditionException;

public class SqlFunc{
// variable
    private List groupByValue = null;
    private JSONObject orderByValue = null;
    private Integer limitValue = null;
    private SqlCondition whereValue = null;
    private SqlCondition havingValue = null;

//========================================================//
// constructor

//========================================================//
// method
    public SqlFunc groupBy(List<String> col){
        this.groupByValue = col;
        return this;
    }

    public SqlFunc orderBy(String col, int opt){
        this.orderByValue = new JSONObject();
        this.orderByValue.put("fields", col);
        this.orderByValue.put("option", opt);
        return this;
    }

    public SqlFunc limit(int limit){
        this.limitValue = limit;
        return this;
    }

    public SqlFunc where(SqlCondition expr){
        this.whereValue = expr;
        return this;
    }

    public SqlFunc having(SqlCondition expr){
        this.havingValue = expr;
        return this;
    }

    public List getGroup(){
        return this.groupByValue;
    }
    
    public JSONObject getOrderBy(){
        return this.orderByValue;
    }

    public int getLimit(){
        return this.limitValue;
    }

    public boolean getWhere(JSONObject row) throws HVSqlConditionException{
        if(this.whereValue == null){
            return true;
        }
        return this.whereValue.check(row);
    }

    public boolean getHaving(JSONObject row) throws HVSqlConditionException{
        if(this.havingValue == null){
            return true;
        }
        return this.havingValue.check(row);
    }
}