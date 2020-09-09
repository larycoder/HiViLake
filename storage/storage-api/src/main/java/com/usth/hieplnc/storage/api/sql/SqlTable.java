package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - The interface is represent of filesystem
 * - It extend the StorageWrapper interface
 * 
 */

import java.util.List;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.SqlResult;
import com.usth.hieplnc.storage.api.sql.SqlCondition;
import com.usth.hieplnc.storage.api.sql.SqlFunc;

public interface SqlTable{
    public JSONObject getMeta();
    public SqlTable as(String tableName);
    public SqlTable insert(List<String> columns, List<String> data);
    public SqlTable update(List<String> columns, List<String> data, SqlCondition condition);
    public SqlTable delete(SqlCondition condition);
    public SqlTable alterCol(String name, int option, JSONObject extra);
    public SqlTable select(List<Col> col, SqlFunc condition);
    public SqlTable join(SqlTable table);
    public SqlTable leftJoin(SqlTable table);
    public SqlTable rightJoin(SqlTable table);
    public SqlTable fullJoin(SqlTable table);
    public SqlTable union(SqlTable table);
    public SqlTable unionAll(SqlTable table);
    public SqlResult commit();
}