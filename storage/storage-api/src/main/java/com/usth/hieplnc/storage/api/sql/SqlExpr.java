package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - The class presents the logic condition
 * 
 */

import java.util.List;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.SqlCondition;
import com.usth.hieplnc.storage.api.sql.HVSqlConditionException;

public class SqlExpr implements SqlCondition{
// variable
    private List<SqlCondition> andValue = null;
    private List<SqlCondition> orValue = null;

//========================================================//
// constructor

//========================================================//
// method
    public SqlExpr and(List<SqlCondition> listExpr){
        this.andValue = listExpr;
        this.orValue = null;
        return this;
    }

    public SqlExpr or(List<SqlCondition> listExpr){
        this.orValue = listExpr;
        this.andValue = null;
        return this;
    }

    @Override
    public boolean check(JSONObject row) throws HVSqlConditionException{
        Boolean checkResult = null;

        if(this.andValue != null){
            for(SqlCondition value: this.andValue){
                if(checkResult == null){
                    checkResult = value.check(row);
                } else{
                    checkResult = (checkResult && value.check(row));
                }
            }
        } else if(this.orValue != null){
            for(SqlCondition value: this.orValue){
                if(checkResult == null){
                    checkResult = value.check(row);
                } else{
                    checkResult = (checkResult || value.check(row));
                }
            }
        } else{
            return true;
        }

        return checkResult;
    }
}