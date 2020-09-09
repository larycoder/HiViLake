package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - The class presents the field of tabular data
 * 
 */

import java.util.List;
import java.util.ArrayList;
import java.lang.NumberFormatException;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.SqlCondition;
import com.usth.hieplnc.storage.api.sql.model.DataType;

/**
 * WARNING:
 * - wrong like operator in method checkString(String dataValue)
 *
 */

public class Col implements SqlCondition{
// variable
    private final String name;
    private String alias;
    private String type;

    private String eqValue = null;
    private String gtValue = null;
    private String ltValue = null;
    private List<String> inValue = null;
    private String likeValue = null;

//========================================================//
// constructor
    public Col(String name, String type){
        this.name = name;
        this.type = type;
    }

//========================================================//
// method
    public Col eq(String value){
        this.eqValue = value;
        return this;
    }

    public Col gt(String value){
        this.gtValue = value;
        this.ltValue = null;
        return this;
    }

    public Col lt(String value){
        this.ltValue = value;
        this.gtValue = null;
        return this;
    }

    public Col in(List<String> value){
        this.inValue = value;
        return this;
    }

    public Col like(String value){
        this.likeValue = value;
        return this;
    }

    private boolean checkString(String dataValue) throws HVSqlConditionException{
        String value = dataValue;

        if(this.eqValue != null){
            if(!this.eqValue.equals(value)) return false;
        }
        if(this.gtValue != null){
            throw new HVSqlConditionException("Field " + this.name + " is String and does not support \">\" operator");
        }
        if(this.ltValue != null){
            throw new HVSqlConditionException("Field " + this.name + " is String and does not support \"<\" operator");
        }
        if(this.inValue != null){
            int skip = 0;
            for(String strEl: (List<String>) this.inValue){
                if(strEl.equals(value)) skip = 1;
            }
            if(skip != 1) return false;
        }
        if(this.likeValue != null){
            if(!this.likeValue.equals(value)) return false;
        }

        return true;
    }

    private boolean checkInt(String dataValue) throws HVSqlConditionException{
        try{
            int value = Integer.parseInt(dataValue);
            
            if(this.eqValue != null){
                int eqValue = Integer.parseInt(this.eqValue);
                if(!(eqValue == value)) return false;
            }
            if(this.gtValue != null){
                int gtValue = Integer.parseInt(this.gtValue);
                if(!(gtValue < value)) return false;
            }
            if(this.ltValue != null){
                int ltValue = Integer.parseInt(this.ltValue);
                if(!(ltValue > value)) return false;
            }
            if(this.inValue != null){
                int skip = 0;
                for(String el: (List<String>) this.inValue){
                    int intEl = Integer.parseInt(el);
                    if(intEl == value) skip = 1;
                }
                if(!(skip == 1)) return false;
            }
            if(this.likeValue != null){
                throw new HVSqlConditionException("Field " + this.name + " is Integer and does not support LIKE operator");
            }

            return true;
        } catch(NumberFormatException e){
            throw new HVSqlConditionException("Could not format value to expected type with message:\n" + e.toString());
        }
    }

    private boolean checkFloat(String dataValue) throws HVSqlConditionException{
        try{
            float value = Float.parseFloat(dataValue);
            
            if(this.eqValue != null){
                float eqValue = Float.parseFloat(this.eqValue);
                if(!(eqValue == value)) return false;
            }
            if(this.gtValue != null){
                float gtValue = Float.parseFloat(this.gtValue);
                if(!(gtValue < value)) return false;
            }
            if(this.ltValue != null){
                float ltValue = Float.parseFloat(this.ltValue);
                if(!(ltValue > value)) return false;
            }
            if(this.inValue != null){
                float skip = 0;
                for(String el: (List<String>) this.inValue){
                    float floatEl = Float.parseFloat(el);
                    if(floatEl == value) skip = 1;
                }
                if(!(skip == 1)) return false;
            }
            if(this.likeValue != null){
                throw new HVSqlConditionException("Field " + this.name + " is Float and does not support LIKE operator");
            }

            return true;
        } catch(NumberFormatException e){
            throw new HVSqlConditionException("Could not format value to expected type with message:\n" + e.toString());
        }
    }

    private boolean checkTimeStamp(String dataValue) throws HVSqlConditionException{
        throw new HVSqlConditionException("This version has not supported timestamp type yet");
    }

    private boolean checkBin(String dataValue) throws HVSqlConditionException{
        throw new HVSqlConditionException("This version has not supported binary type yet");
    }

    private boolean checkValue(String value, int type) throws HVSqlConditionException{
        switch(type){
            case DataType.STRING:
                return checkString(value);
            case DataType.INTEGER:
                return checkInt(value);
            case DataType.FLOAT:
                return checkFloat(value);
            case DataType.TIMESTAMP:
                return checkTimeStamp(value);
            case DataType.BIN:
                return checkBin(value);
            default:
                throw new HVSqlConditionException("Field " + this.name + " has unrecognized type");
        }
    }

    @Override
    public boolean check(JSONObject row) throws HVSqlConditionException{
        List schema = (List<String>) row.get("fields");
        List type = (List<String>) row.get("type");
        List data = (List<String>) row.get("data");
        // check field exists
        if(!schema.contains(this.name)){
            throw new HVSqlConditionException("Field " + this.name + " is not existed");
        }

        int index = schema.indexOf(this.name);
        String dataValue = (String) data.get(index);
        int dataType = (int) type.get(index);
        return checkValue(dataValue, dataType);
    }
}