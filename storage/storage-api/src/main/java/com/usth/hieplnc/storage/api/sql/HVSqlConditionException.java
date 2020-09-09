package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - Sql Exception: the field is not exists
 *
 */

public class HVSqlConditionException extends Exception{
// variable

//========================================================//
// constructor
    public HVSqlConditionException(String message){
        super("Hivilake Sql Condition Exception (code 0): " + message);
    }
//========================================================//
// method

}
