package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - Sql Exception: the field is duplicate
 *
 */

import com.usth.hieplnc.storage.api.sql.HVSqlException;

public class HVSqlDuplicateFieldException extends HVSqlException{
// variable

//========================================================//
// constructor
    public HVSqlDuplicateFieldException(String message){
        super("Hivilake Sql Duplicate Field Exception (code 1): " + message);
    }
//========================================================//
// method

}