package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - Sql Exception: the field is not exists
 *
 */

import com.usth.hieplnc.storage.api.sql.HVSqlException;

public class HVSqlConditionException extends HVSqlException{
// variable

//========================================================//
// constructor
    public HVSqlConditionException(String message){
        super("Hivilake Sql Condition Exception (code 0): " + message);
    }
//========================================================//
// method

}
