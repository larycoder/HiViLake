package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - The interface is represent of filesystem
 * - It extend the StorageWrapper interface
 * 
 */

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.HVSqlConditionException;

public interface SqlCondition{
    public boolean check(JSONObject row) throws HVSqlConditionException;
}