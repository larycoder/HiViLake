package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - Sql Exception: type incompatible
 *
 */

import com.usth.hieplnc.storage.api.sql.HVSqlException;

public class HVSqlIncompatibleException extends HVSqlException{
// variable

//========================================================//
// constructor
    public HVSqlIncompatibleException(String message){
        super("Hivilake Sql Incompatible Exception (code 2): " + message);
    }
//========================================================//
// method

}