package com.usth.hieplnc.util.base.filequery;

/**
 * DOC:
 * - This util provide sql mechansim to query tabular data
 *
 */

import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import com.usth.hieplnc.storage.api.*;
import com.usth.hieplnc.storage.api.sql.*;
import com.usth.hieplnc.storage.api.sql.model.*;
import com.usth.hieplnc.storage.api.filesystem.*;
import com.usth.hieplnc.storage.api.filesystem.model.*;

import com.usth.hieplnc.util.api.Service;
import com.usth.hieplnc.util.base.HVUtilException;
import com.usth.hieplnc.util.base.systemlog.SystemLog;

public class FileQuery implements Service{
// variable
    private final SqlWrapper sqlStorage;
    private final FilesystemWrapper fsStorage;
    private final SystemLog systemLog;

    private JSONObject status;
    private JSONObject parameter = null;

//=================================================================//
// constructor
    public FileQuery(StorageWrapper storage, SystemLog systemLog) throws HVUtilException{
        // check storage type supported
        List<String> supportType = storage.support();
        if(!supportType.contains("sql") || !supportType.contains("filesystem")){
            throw new HVUtilException("HVUtilStorageManagerException (code 0): the storage support type does not satify requirement");
        }
        this.sqlStorage = (SqlWrapper) storage;
        this.fsStorage = (FilesystemWrapper) storage;

        // check system log
        if(systemLog == null){
            throw new HVUtilException("HVUtilStorageManagerException (code 0): system log is not exists");
        } else{
            this.systemLog = systemLog;
        }

        initStatus();
    }

    private FileQuery(FilesystemWrapper fsStorage, SqlWrapper sqlStorage, SystemLog systemLog){
        this.fsStorage = fsStorage;
        this.sqlStorage = sqlStorage;
        this.systemLog = systemLog;

        initStatus();
    }

//=================================================================//
// method
    private void initStatus(){
        // init system
        JSONObject system = new JSONObject();
        system.put("error", new JSONArray());

        // init status
        this.status = new JSONObject();
        this.status.put("system", system);
        this.status.put("action", "");
        this.status.
        put("result", "");
    }

    private void resetStatus(){
        this.status.replace("action", "");
        this.status.replace("result", "");
    }

    private void updateAction(String status, String message, String code){
        JSONObject action = new JSONObject();
        action.put("status", status);
        action.put("message", message);
        action.put("code", code);
        this.status.replace("action", action);
    }

    private JSONObject dataFrame2Json(JSONObject dataFrame){
        // get name
        String name = (String) dataFrame.get("name");

        // get fields
        List<String> fieldString = (List<String>) dataFrame.get("fields");
        JSONArray fields = new JSONArray();
        for(String field: fieldString){
            fields.add(field);
        }

        // get type
        List<Integer> typeInt = (List<Integer>) dataFrame.get("type");
        JSONArray type = new JSONArray();
        for(Integer value: typeInt){
            int intValue = value;
            String stringValue;
            switch(intValue){
                case DataType.STRING:
                    stringValue = "STRING";
                    break;
                case DataType.INTEGER:
                    stringValue = "INTEGER";
                    break;
                case DataType.FLOAT:
                    stringValue = "FLOAT";
                    break;
                case DataType.TIMESTAMP:
                    stringValue = "TIMESTAMP";
                    break;
                case DataType.BIN:
                    stringValue = "BIN";
                    break;
                default:
                    stringValue = "";
            }

            type.add(stringValue);
        }

        // get data
        List<List<String>> dataList = (List<List<String>>) dataFrame.get("data");
        JSONArray data = new JSONArray();
        for(List<String> row: dataList){
            JSONArray jsonRow = new JSONArray();
            for(String value: row){
                jsonRow.add(value);
            }
            data.add(jsonRow);
        }

        // build Json object
        JSONObject jsonDataFrame = new JSONObject();
        jsonDataFrame.put("name", name);
        jsonDataFrame.put("fields", fields);
        jsonDataFrame.put("type", type);
        jsonDataFrame.put("data", data);
        
        return jsonDataFrame;
    }

    private Col genColCondition(String field, char opr, String value){
        Col column = new Col(field, ColType.REAL, DataType.STRING);
        switch(opr){
            case '=':
                column.eq(value);
                break;
            case '>':
                column.gt(value);
                break;
            case '<':
                column.lt(value);
                break;
            default:
                column.eq(value);
                break;
        }

        return column;
    }

    private JSONObject searchStorage(int repoId, JSONObject expr){
        try{
            // get repo location
            String repoPath = this.systemLog.getRepoLocation(repoId);
            if(repoPath == null){
                throw new Exception("Could not retrieve repo path, system log report: " + this.systemLog.getRawStatus().toString());
            }

            // setup working file
            String metaPath = repoPath + "/.hivilake/meta.csv";
            String workPath;
            if(expr.containsKey("path")){
                workPath = (String) expr.get("path");
            } else{
                workPath = metaPath;
            }

            // discovery schema parser for path and build table
            SqlTable dataFrameTable;

            if(this.fsStorage.getStatus(workPath).isFile()){
                // this is file -> check type in meta
                SqlTable metaWrongFieldTable = this.sqlStorage.use(metaPath, null);
                SqlResult metaWrongFieldResult = metaWrongFieldTable.commit();

                // rebuild meta table
                List<List<String>> metaData = (List<List<String>>) metaWrongFieldResult.getData().get("data");
                List<String> metaField = (List<String>) metaWrongFieldResult.getSchema().get("fields");
                metaField.clear();
                metaField.addAll(metaData.remove(0));
                SqlTable metaTable = this.sqlStorage.use(metaWrongFieldResult);
                
                // field of meta
                List<Col> selectFields = new ArrayList<Col>();
                selectFields.add(new Col("name", ColType.REAL, DataType.STRING));
                selectFields.add(new Col("format", ColType.REAL, DataType.STRING));

                // where condition for table
                Col pathField = new Col("path", ColType.REAL, DataType.STRING);
                metaTable.select(selectFields, (new SqlFunc()).where(pathField.eq(workPath)));

                // get data
                List<List<String>> formatOfPath = (List<List<String>>) metaTable.commit().getData().get("data");
                if(formatOfPath.isEmpty()){ // could not get file format from meta
                    // let storage discovers by itself
                    dataFrameTable = this.sqlStorage.use(workPath, null);
                } else{
                    // get table name and format
                    List<String> formatRow = formatOfPath.get(0);

                    JSONObject sqlExtra = new JSONObject();
                    // add table name
                    sqlExtra.put("tableName", formatRow.get(0));
                    // add sql parser
                    String formatType = formatRow.get(1);
                    if(formatType == "csv"){
                        sqlExtra.put("parserCode", 0);
                    } else{
                        // lets storage discovers by itself
                        ;
                    }

                    dataFrameTable = this.sqlStorage.use(workPath, sqlExtra);
                }
            } else{
                // this is dir
                JSONObject sqlExtra = new JSONObject();
                // get dir name
                String[] workPathName = workPath.split("/");
                for(int i = workPathName.length; i >= 0; i--){
                    if(!workPathName[i].equals("")){
                        sqlExtra.put("tableName", workPathName[i]);
                        break;
                    }
                }
                sqlExtra.put("parserCode", 50);
                dataFrameTable = this.sqlStorage.use(workPath, sqlExtra);
            }

            SqlResult queryResult;

            // if query include "select" option
            if(expr.containsKey("select")){
                // generate select fields
                JSONArray fieldsList = (JSONArray) expr.get("select");
                List<Col> queryFields = new ArrayList<Col>();
                for(Object field: fieldsList){
                    queryFields.add(new Col((String) field, ColType.REAL, DataType.STRING));
                }
                
                // add table condition
                SqlFunc tableCondition = new SqlFunc();
                
                if(expr.containsKey("where")){
                    // get where condition
                    JSONArray jsonWhereCondition = (JSONArray) expr.get("where");
                    SqlExpr andCondition = new SqlExpr();
                    List<SqlCondition> conditionList = new ArrayList<SqlCondition>();
                    for(Object operation: jsonWhereCondition){
                        JSONObject opr = (JSONObject) operation;
                        String whereField = (String) opr.get("field");
                        String operator = (String) opr.get("operator");
                        String whereValue = (String) opr.get("value");
                        conditionList.add(genColCondition(whereField, operator.charAt(0), whereValue));
                    }
                    andCondition.and(conditionList);
                    tableCondition.where(andCondition);
                }
                    
                if(expr.containsKey("order_by")){
                    // order by condition
                    JSONObject jsonOrderBy = (JSONObject) expr.get("order_by");
                    String orderByField = (String) jsonOrderBy.get("field");
                    String orderByValue = (String) jsonOrderBy.get("value");
                    if(orderByValue.equals("ASC")){
                        tableCondition.orderBy(orderByField, OrderOpt.ASC);
                    } else if(orderByValue.equals("DESC")){
                        tableCondition.orderBy(orderByField, OrderOpt.DES);
                    }
                }

                if(expr.containsKey("limit")){
                    // limit
                    String limit = (String) expr.get("limit");
                    tableCondition.limit(Integer.parseInt(limit));
                }

                queryResult = dataFrameTable.select(fieldsList, tableCondition).commit();
            } else{
                queryResult = dataFrameTable.commit();
            }
            
            // build dataFrame
            JSONObject dataFrame = new JSONObject();
            dataFrame.put("name", queryResult.getSchema().get("name"));
            dataFrame.put("type", queryResult.getSchema().get("type"));
            dataFrame.put("fields", queryResult.getSchema().get("fields"));
            dataFrame.put("data", queryResult.getData().get("data"));

            JSONObject finalResult = dataFrame2Json(dataFrame);
            updateAction("search storage", "search storage - done", "200");

            return finalResult;
        } catch(Exception e){
            updateAction("search storage error", e.toString(), "3");
            return null;
        }
    }

    private JSONObject searchCache(int repoId, JSONObject expr){
        updateAction("search cache error", "this action is not supported yet", "3");
        return null;
    }

    private InputStream loadData(String path){
        try{
            // check path exists
            if(!this.fsStorage.exists(path)){
                throw new Exception("path does not exists");
            }
            
            // check path is file
            if(!this.fsStorage.getStatus(path).isFile()){
                throw new Exception("path is not file");
            }

            InputStream reader = this.fsStorage.openFile(path).readStream();
            updateAction("load data", "success return InputStream - done", "200");
            return reader;
        } catch(Exception e){
            updateAction("load data error", e.toString(), "3");
            return null;
        }
    }

    @Override
    public boolean setParameter(JSONObject param){
        try{
            this.parameter = new JSONObject();

            // load parameter
            this.parameter.put("action", param.get("action"));
            String action = (String) param.get("action");
            if(action.equals("searchStorage")){
                JSONObject input = (JSONObject) param.get("parameter");
                if(input.get("repoId") == null || input.get("expr") == null){
                    this.parameter = null;
                    return false;
                } else{
                    String repoId = (String) input.get("repoId");
                    try{
                        this.parameter.put("p_repoId", Integer.parseInt(repoId));
                    } catch(Exception e){
                        this.parameter = null;
                        return false;
                    }
                    this.parameter.put("p_expr", input.get("expr"));
                    return true;
                }
            } else if(action.equals("loadData")){
                JSONObject input = (JSONObject) param.get("parameter");
                if(input.get("path") == null){
                    this.parameter = null;
                    return false;
                }
                this.parameter.put("p_path", input.get("path"));
                this.parameter.put("c_skip", 0);
                return true;
            } else{
                this.parameter = null;
                return false;
            }
        } catch(Exception e){
            this.parameter = null;
            return false;
        }
    }

    @Override
    public JSONObject getStatus(){
        // check null param
        if(this.parameter == null){
            resetStatus();
            updateAction("get status error", "please setup parameter", "3");
            return this.status;
        }

        // perform action
        String action = (String) this.parameter.get("action");

        if(action.equals("searchStorage")){
            resetStatus();
            JSONObject result = searchStorage((int) this.parameter.get("p_repoId"), (JSONObject) this.parameter.get("p_expr"));
            if(result != null){
                this.status.replace("result", result);
            }
            this.parameter = null;
            return this.status;
        } else{
            Integer skip = (Integer) this.parameter.get("c_skip");
            if(skip == 0){
                resetStatus();
                updateAction((String) this.parameter.get("action"), "waiting to execute", "NULL");
            }
            return this.status;
        }
    }

    @Override
    public InputStream pullFile(){
        // check null parameter
        if(this.parameter == null){
            return null;
        } else{
            Integer skip = (Integer) this.parameter.get("c_skip");
            if(skip == 1){
                return null;
            }
        }

        // perform action
        String action = (String) this.parameter.get("action");

        if(action.equals("loadData")){
            resetStatus();
            InputStream reader = loadData((String) this.parameter.get("p_path"));
            this.parameter.replace("c_skip", 1);
            return reader;
        } else{
            resetStatus();
            updateAction("pull file error", "Could not recognize action", "3");
            return null;
        }
    }

    @Override
    public void pushFile(InputStream data){
        resetStatus();
        this.parameter = null;
        updateAction("push file", "this method is not supported", "3");
    }

    @Override
    public OutputStream pushFile(){
        resetStatus();
        this.parameter = null;
        updateAction("push file", "this method is not supported", "3");
        return null;
    }

    @Override
    public Service duplicate(){
        return new FileQuery(this.fsStorage, this.sqlStorage, (SystemLog) this.systemLog.duplicate());
    }
}
