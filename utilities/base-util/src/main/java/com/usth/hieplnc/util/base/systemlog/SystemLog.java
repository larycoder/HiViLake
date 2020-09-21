package com.usth.hieplnc.util.base.systemlog;

/**
 * DOC:
 * - This util provide a logging solution for whole system
 *
 */

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import com.usth.hieplnc.storage.api.*;
import com.usth.hieplnc.storage.api.sql.*;
import com.usth.hieplnc.storage.api.sql.model.*;
import com.usth.hieplnc.storage.api.filesystem.FilesystemWrapper;
import com.usth.hieplnc.storage.api.filesystem.model.*;

import com.usth.hieplnc.util.api.Service;
import com.usth.hieplnc.util.base.HVUtilException;
import com.usth.hieplnc.util.base.systemlog.Log;
import com.usth.hieplnc.util.base.systemlog.CentralInfo;
import com.usth.hieplnc.util.base.systemlog.model.*;

/**
 * WARNNING:
 * - all method pass out type checking in dataframe -> can cause dataframe type corrupt
 * - method add new data does not check position of fields before adding
 * - the return object of getParameter is reference return -> should be deep copy result return
 * - method save log is not checked type pass in -> can cause error
 * - systemlog should not be the singleton class since it will be used by multiple class -> it is
 * not thread-safe
 *
 */

public class SystemLog implements Log, CentralInfo, Service{
// variable
    private final SqlWrapper sqlStorage;
    private final FilesystemWrapper fsStorage;
    private final String path; // path to data stored location

    private final String activityLog = "activity_log";
    private final String repoLog = "repo_log";
    private final String userLog = "user_log";
    private final String catalogLog = "catalog_log";
    private final String timeLocationFile = "time_zone";

    private JSONObject status;
    private JSONObject parameter = null;
    private JSONObject activityDataFrame;
    private JSONObject repoDataFrame;
    private JSONObject userDataFrame;
    private JSONObject catalogDataFrame;

//=================================================================//
// constructor
    public SystemLog(StorageWrapper storage, String path) throws HVUtilException{
        // initialize status
        initStatus();

        // setup storage
        List<String> supportType = storage.support();
        if(supportType.contains("sql") && supportType.contains("filesystem")){
            this.sqlStorage = (SqlWrapper) storage;
            this.fsStorage = (FilesystemWrapper) storage;
        } else{
            this.sqlStorage = null;
            this.fsStorage = null;

            // error code
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Expected storage engine type (sql, filesystem) is not supported");
            error.put("code", "0");
            updateSystemError(error);
        }

        // setup data location
        this.path = path;

        // initialize log system
        initializeActivityLog();
        initializeRepoLog();
        this.userDataFrame = initCentralInfo(this.userLog, "user log");
        this.catalogDataFrame = initCentralInfo(this.catalogLog, "catalog");

        // check system error
        JSONArray systemError = (JSONArray) ((JSONObject) this.status.get("system")).get("error");
        if(!systemError.isEmpty()){
            String error = "HVUtilSystemLogException: ";
            for(Object errorObject: systemError){
                JSONObject errorValue = (JSONObject) errorObject;
                error += "\n\n" + errorValue.toString();
            }
            throw new HVUtilException(error);
        }
    }

//=================================================================//
// method
    public JSONObject getRawStatus(){
        return this.status;
    }

    private void initStatus(){
        // system log
        JSONArray error = new JSONArray();

        JSONObject system = new JSONObject();
        system.put("error", error);

        this.status = new JSONObject();
        this.status.put("system", system);

        // action log
        JSONObject action = new JSONObject();
        action.put("response", "");
        this.status.put("action", action);

        // result
        JSONObject result = new JSONObject();
        result.put("response", "");
        this.status.put("result", result);
    }

    private void updateSystemError(JSONObject status){
        JSONObject system = (JSONObject) this.status.get("system");
        JSONArray error = (JSONArray) system.get("error");
        error.add(status);
    }

    private void updateAction(JSONObject resp){
        JSONObject action = (JSONObject) this.status.get("action");
        action.replace("response", resp);
    }

    private void updateResult(JSONObject resp){
        JSONObject result = (JSONObject) this.status.get("result");
        result.replace("response", resp);
    }

    private void resetStatus(){
        // reset action
        JSONObject temp = (JSONObject) this.status.get("action");
        temp.replace("response", "");

        // reset result
        temp = (JSONObject) this.status.get("result");
        temp.replace("response", "");
    }

    private void initializeActivityLog(){
        try{
            if(!this.fsStorage.exists(this.path + "/" + this.activityLog + ".csv")){
                // get schema
                ActivityLogModel tempAct = new ActivityLogModel();
                JSONObject schema = tempAct.toDataFrame(this.activityLog);
                schema.remove("data");

                // create data file
                this.sqlStorage.createTable(this.path, this.activityLog, schema, this.sqlStorage.getParser(0));
            }

            // load data
            SqlTable table = this.sqlStorage.use(this.path + "/" + this.activityLog + ".csv", null);

            // check successfully loading data
            if(table == null){
                JSONObject error = new JSONObject();
                error.put("status", "activity log initialization error");
                error.put("message", "Loading data return null");
                error.put("code", "1");
                updateSystemError(error);
            } else{
                SqlResult tableResult = table.commit();

                // get schema of table
                this.activityDataFrame = tableResult.getSchema();

                // get data of table
                List<List<String>> data = (List<List<String>>) tableResult.getData().get("data");
                List<String> fields = data.remove(0);

                // update dataFrame
                this.activityDataFrame.replace("fields", fields);
                this.activityDataFrame.put("data", data);
            }

        } catch(Exception e){
            JSONObject error = new JSONObject();
            error.put("status", "activity log initialization error");
            error.put("message", e.toString());
            error.put("code", "1");
            updateSystemError(error);

            this.activityDataFrame = null;
        }
    }

    private void initializeRepoLog(){
        try{
            if(!this.fsStorage.exists(this.path + "/" + this.repoLog + ".csv")){
                // get schema
                RepoLogModel tempRep = new RepoLogModel();
                JSONObject schema = tempRep.toDataFrame(this.repoLog);
                schema.remove("data");

                // create data file
                this.sqlStorage.createTable(this.path, this.repoLog, schema, this.sqlStorage.getParser(0));
            }

            // load data
            SqlTable table = this.sqlStorage.use(this.path + "/" + this.repoLog + ".csv", null);

            // check successfully loading data
            if(table == null){
                JSONObject error = new JSONObject();
                error.put("status", "repo log initialization error");
                error.put("message", "Loading data return null");
                error.put("code", "1");
                updateSystemError(error);
            } else{
                SqlResult tableResult = table.commit();

                // get schema of table
                this.repoDataFrame = tableResult.getSchema();

                // get data of table
                List<List<String>> data = (List<List<String>>) tableResult.getData().get("data");
                List<String> fields = data.remove(0);

                // update dataFrame
                this.repoDataFrame.replace("fields", fields);
                this.repoDataFrame.put("data", data);
            }

        } catch(Exception e){
            JSONObject error = new JSONObject();
            error.put("status", "repo log initialization error");
            error.put("message", e.toString());
            error.put("code", "1");
            updateSystemError(error);

            this.repoDataFrame = null;
        }
    }

    private JSONObject initCentralInfo(String tableName, String errorName){
        JSONObject dataFrame = new JSONObject();

        try{
            if(!this.fsStorage.exists(this.path + "/" + tableName + ".csv")){
                // get schema
                CentralInfoModel tempModel = new CentralInfoModel();
                JSONObject schema = tempModel.toDataFrame(tableName);
                schema.remove("data");

                // create data file
                this.sqlStorage.createTable(this.path, tableName, schema, this.sqlStorage.getParser(0));
            }

            // load data
            SqlTable table = this.sqlStorage.use(this.path + "/" + tableName + ".csv", null);

            // check successfully loading data
            if(table == null){
                JSONObject error = new JSONObject();
                error.put("status", errorName + " initialization error");
                error.put("message", "Loading data return null");
                error.put("code", "1");
                updateSystemError(error);

                return null;
            } else{
                SqlResult tableResult = table.commit();

                // get schema of table
                dataFrame = tableResult.getSchema();

                // get data of table
                List<List<String>> data = (List<List<String>>) tableResult.getData().get("data");
                List<String> fields = data.remove(0);

                // update dataFrame
                dataFrame.replace("fields", fields);
                dataFrame.put("data", data);
            }

            return dataFrame;

        } catch(Exception e){
            JSONObject error = new JSONObject();
            error.put("status", errorName + " initialization error");
            error.put("message", e.toString());
            error.put("code", "1");
            updateSystemError(error);

            return null;
        }
    }

    private void updateActionError(String status, String message){
        JSONObject error = new JSONObject();
        error.put("status", status);
        error.put("message", message);
        error.put("code", "3");
        updateAction(error);
    }

    @Override
    public void trackActivity(ActivityLogModel log){
        try{
            List<List<String>> data = (List<List<String>>) this.activityDataFrame.get("data");
            List<List<String>> newData = (List<List<String>>) log.toDataFrame(this.activityLog).get("data");
            data.add(newData.get(0));

            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "track activity log - done");
            resp.put("code", "200");
            updateAction(resp);
        } catch(Exception e){
            updateActionError("track activity error", e.toString());
        }
    }

    @Override
    public Integer countActivity(){
        try{
            List<List<String>> data = (List<List<String>>) this.activityDataFrame.get("data");
            
            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "count activity log - done");
            resp.put("code", "200");
            updateAction(resp);

            return data.size();
        } catch(Exception e){
            updateActionError("count activity error", e.toString());
            return 0;
        }
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

    @Override
    public JSONObject listActivity(){
        try{
            JSONObject result = dataFrame2Json(this.activityDataFrame);

            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "list all activity log - done");
            resp.put("code", "200");
            updateAction(resp);

            return result;
        } catch(Exception e){
            updateActionError("list activity error", e.toString());
            return null;
        }
    }

    @Override
    public void addRepo(RepoLogModel log){
        try{
            List<List<String>> data = (List<List<String>>) this.repoDataFrame.get("data");
            List<List<String>> newData = (List<List<String>>) log.toDataFrame(this.repoLog).get("data");
            data.add(newData.get(0));

            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "add new repo to repo log - done");
            resp.put("code", "200");
            updateAction(resp);
        } catch(Exception e){
            updateActionError("add repo error", e.toString());
        }
    }

    @Override
    public void updateRepo(int repoId, JSONObject data){
        try{
            List<List<String>> repoData = (List<List<String>>) this.repoDataFrame.get("data");
            // get update data
            List<String> updateFields = (List<String>) data.get("fields");
            List<String> updateValue = (List<String>) data.get("data");

            // get fields of dataframe
            List<String> fields = (List<String>) this.repoDataFrame.get("fields");

            // get index of update fields
            List<Integer> index = new ArrayList<Integer>();
            for(String updateField: updateFields){
                index.add(fields.indexOf(updateField));
            }

            // update row
            List<String> row = repoData.get(repoId);
            for(int i = 0; i < index.size(); i++){
                row.set(index.get(i), updateValue.get(i));
            }

            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "update repo log - done");
            resp.put("code", "200");
            updateAction(resp);

        } catch(Exception e){
            JSONObject resp = new JSONObject();
            resp.put("status", "update repo error");
            resp.put("message", e.toString());
            resp.put("code", "3");
            updateAction(resp);
        }
    }

    @Override
    public String getRepoLocation(int repoId){
        try{
            List<List<String>> data = (List<List<String>>) this.repoDataFrame.get("data");
            List<String> fields = (List<String>) this.repoDataFrame.get("fields");
            int index = fields.indexOf("name");
            String location = data.get(repoId).get(index);

            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "get repo location - done");
            resp.put("code", "200");
            updateAction(resp);

            return location;
        } catch(Exception e){
            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "get repo location error");
            resp.put("message", e.toString());
            resp.put("code", "3");
            updateAction(resp);
            
            return null;
        }
    }

    @Override
    public JSONObject listRepo(){
        try{
            JSONObject result = dataFrame2Json(this.repoDataFrame);

            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "get list of all repo information - done");
            resp.put("code", "200");
            updateAction(resp);

            return result;
        } catch(Exception e){
            updateActionError("list repo error", e.toString());
            return null;
        }
    }

    @Override
    public void registerUser(String name, String desc){
        try{
            // get position of name
            List<String> fields = (List<String>) this.userDataFrame.get("fields");
            int index = fields.indexOf("name");

            // get data of data frame
            List<List<String>> data = (List<List<String>>) this.userDataFrame.get("data");

            // check user exists
            int userExists = 0;
            for(List<String> row: data){
                if(name.equals(row.get(index))){
                    userExists++;
                    break;
                }
            }

            if(userExists > 0){
                JSONObject resp = new JSONObject();
                resp.put("status", "add new user error");
                resp.put("message", "user already existed");
                resp.put("code", "3");
                updateAction(resp);

            } else{
                // add new user
                CentralInfoModel newUser = new CentralInfoModel();
                newUser.name = name;
                newUser.desc = desc;

                List<List<String>> newData = (List<List<String>>) newUser.toDataFrame(this.userLog).get("data");
                data.add(newData.get(0));

                // save new data
                SqlParser parser = this.sqlStorage.getParser(0);
                
                // set schema
                JSONObject saveSchema = new JSONObject();
                saveSchema.put("name", this.userLog);
                saveSchema.put("fields", this.userDataFrame.get("fields"));
                // set data
                JSONObject saveData = new JSONObject();
                saveData.put("data", this.userDataFrame.get("data"));
                // save csv file
                parser.save(this.path, this.userLog, saveSchema, saveData);

                // load data
                this.userDataFrame = initCentralInfo(this.userLog, "user log");

                JSONObject resp = new JSONObject();
                resp.put("status", "success");
                resp.put("message", "add new user - done");
                resp.put("code", "200");
                updateAction(resp);
            }
        } catch(Exception e){
            JSONObject resp = new JSONObject();
            resp.put("status", "register user error");
            resp.put("message", e.toString());
            resp.put("code", "3");
            updateAction(resp);
        }
    }

    @Override
    public List<String> listUser(){
        try{
            List<String> userList = new ArrayList<String>();
            List<String> fields = (List<String>) this.userDataFrame.get("fields");
            int index = fields.indexOf("name");

            List<List<String>> data = (List<List<String>>) this.userDataFrame.get("data");
            for(List<String> row: data){
                userList.add(row.get(index));
            }

            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "list of user - done");
            resp.put("code", "200");
            updateAction(resp);

            return userList;
        } catch(Exception e){
            updateActionError("list user error", e.toString());
            return null;
        }
    }

    @Override
    public String getUserInfo(String name){
        try{
            List<String> fields = (List<String>) this.userDataFrame.get("fields");
            int nameIndex = fields.indexOf("name");
            int descIndex = fields.indexOf("describe");

            List<List<String>> data = (List<List<String>>) this.userDataFrame.get("data");
            for(List<String> row: data){
                if(name.equals(row.get(nameIndex))){
                    // reponse
                    JSONObject resp = new JSONObject();
                    resp.put("status", "success");
                    resp.put("message", "get user describe - done");
                    resp.put("code", "200");
                    updateAction(resp);

                    return row.get(descIndex);
                }
            }

            // response
            JSONObject resp = new JSONObject();
            resp.put("status", "get user info error");
            resp.put("message", "can not find user with name " + name);
            resp.put("code", "3");
            updateAction(resp);

            return null;
        } catch(Exception e){
            updateActionError("get user info error", e.toString());
            return null;
        }
    }

    @Override
    public void registerCatalog(String name, String desc){
        try{
            // get position of name
            List<String> fields = (List<String>) this.catalogDataFrame.get("fields");
            int index = fields.indexOf("name");

            // get data of data frame
            List<List<String>> data = (List<List<String>>) this.catalogDataFrame.get("data");

            // check user exists
            int userExists = 0;
            for(List<String> row: data){
                if(name.equals(row.get(index))){
                    userExists++;
                    break;
                }
            }

            if(userExists > 0){
                JSONObject resp = new JSONObject();
                resp.put("status", "add new catalog error");
                resp.put("message", "catalog already existed");
                resp.put("code", "3");
                updateAction(resp);

            } else{
                // add new user
                CentralInfoModel newCatalog = new CentralInfoModel();
                newCatalog.name = name;
                newCatalog.desc = desc;

                List<List<String>> newData = (List<List<String>>) newCatalog.toDataFrame(this.catalogLog).get("data");
                data.add(newData.get(0));

                // save new data
                SqlParser parser = this.sqlStorage.getParser(0);
                
                // set schema
                JSONObject saveSchema = new JSONObject();
                saveSchema.put("name", this.catalogLog);
                saveSchema.put("fields", this.catalogDataFrame.get("fields"));
                // set data
                JSONObject saveData = new JSONObject();
                saveData.put("data", this.catalogDataFrame.get("data"));
                // save csv file
                parser.save(this.path, this.catalogLog, saveSchema, saveData);

                // load data
                this.catalogDataFrame = initCentralInfo(this.catalogLog, "catalog log");

                JSONObject resp = new JSONObject();
                resp.put("status", "success");
                resp.put("message", "add new catalog - done");
                resp.put("code", "200");
                updateAction(resp);
            }
        } catch(Exception e){
            JSONObject resp = new JSONObject();
            resp.put("status", "register catalog error");
            resp.put("message", e.toString());
            resp.put("code", "3");
            updateAction(resp);
        }
    }

    @Override
    public List<String> listCatalog(){
        try{
            List<String> catalogList = new ArrayList<String>();
            List<String> fields = (List<String>) this.catalogDataFrame.get("fields");
            int index = fields.indexOf("name");

            List<List<String>> data = (List<List<String>>) this.catalogDataFrame.get("data");
            for(List<String> row: data){
                catalogList.add(row.get(index));
            }

            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "list of catalog - done");
            resp.put("code", "200");
            updateAction(resp);

            return catalogList;
        } catch(Exception e){
            updateActionError("list catalog error", e.toString());
            return null;
        }
    }

    @Override
    public String getCatalogInfo(String name){
        try{
            List<String> fields = (List<String>) this.catalogDataFrame.get("fields");
            int nameIndex = fields.indexOf("name");
            int descIndex = fields.indexOf("describe");

            List<List<String>> data = (List<List<String>>) this.catalogDataFrame.get("data");
            for(List<String> row: data){
                if(name.equals(row.get(nameIndex))){
                    // reponse
                    JSONObject resp = new JSONObject();
                    resp.put("status", "success");
                    resp.put("message", "get catalog describe - done");
                    resp.put("code", "200");
                    updateAction(resp);

                    return row.get(descIndex);
                }
            }

            // response
            JSONObject resp = new JSONObject();
            resp.put("status", "get catalog info error");
            resp.put("message", "can not find catalog with name " + name);
            resp.put("code", "3");
            updateAction(resp);

            return null;
        } catch(Exception e){
            updateActionError("get catalog info error", e.toString());
            return null;
        }
    }

    @Override
    public void setupTime(String startTime, String location){
        try{
            // check file exists
            if(!this.fsStorage.exists(this.path + "/" + this.timeLocationFile + ".txt")){
                this.fsStorage.createPath(this.path + "/" + this.timeLocationFile + ".txt", PathType.FILE);
            }

            // convert data to json
            JSONObject data = new JSONObject();
            data.put("startTime", startTime);
            data.put("timezone", location);
            String stringData = data.toString();

            // write data to file
            InputStream writer = new ByteArrayInputStream(stringData.getBytes());
            this.fsStorage.openFile(this.path + "/" + this.timeLocationFile + ".txt").writeStream(writer);

            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "setup new utc start time and timezone - done");
            resp.put("code", "200");
            updateAction(resp);
        } catch(Exception e){
            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "setup time error");
            resp.put("message", e.toString());
            resp.put("code", "3");
            updateAction(resp);
        }
    }

    @Override
    public String getCurrent(){
        try{
            Date date = new Date();

            // reponse
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "get current time - done");
            resp.put("code", "200");
            updateAction(resp);

            return Long.toString(date.getTime());
        } catch(Exception e){
            updateActionError("get current time error", e.toString());
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
            if(action.equals("countActivity")){
                return true;
            } else if(action.equals("listActivity")){
                return true;
            } else if(action.equals("listRepo")){
                return true;
            } else if(action.equals("listUser")){
                return true;
            } else if(action.equals("listCatalog")){
                return true;
            } else if(action.equals("getUserInfo")){
                JSONObject input = (JSONObject) param.get("parameter");
                if(input.get("name") == null){
                    this.parameter = null;
                    return false;
                } else{
                    this.parameter.put("p_name", input.get("name"));
                    return true;
                }
            } else if(action.equals("getCatalogInfo")){
                JSONObject input = (JSONObject) param.get("parameter");
                if(input.get("name") == null){
                    this.parameter = null;
                    return false;
                } else{
                    this.parameter.put("p_name", input.get("name"));
                    return true;
                }
            } else if(action.equals("registerUser")){
                JSONObject input = (JSONObject) param.get("parameter");
                if(input.get("name") == null || input.get("describe") == null){
                    this.parameter = null;
                    return false;
                } else{
                    this.parameter.put("p_name", input.get("name"));
                    this.parameter.put("p_describe", input.get("describe"));
                    return true;
                }
            } else if(action.equals("registerCatalog")){
                JSONObject input = (JSONObject) param.get("parameter");
                if(input.get("name") == null || input.get("describe") == null){
                    this.parameter = null;
                    return false;
                } else{
                    this.parameter.put("p_name", input.get("name"));
                    this.parameter.put("p_describe", input.get("describe"));
                    return true;
                }
            } else if(action.equals("setupTime")){
                JSONObject input = (JSONObject) param.get("parameter");
                if(input.get("utc") == null || input.get("timezone") == null){
                    this.parameter = null;
                    return false;
                } else{
                    this.parameter.put("p_utc", input.get("utc"));
                    this.parameter.put("p_timezone", input.get("timezone"));
                    return true;
                }
            } else if(action.equals("saveLog")){
                JSONObject input = (JSONObject) param.get("parameter");
                if(input.get("log") == null){
                    this.parameter = null;
                    return false;
                } else{
                    this.parameter.put("p_log", input.get("log"));
                    return true;
                }
            } else{
                this.parameter = null;
                return false;
            }
        } catch(Exception e){
            this.parameter = null;
            return false;
        }
    }

    public void saveLog(String log){
        try{
            if(log.equals("activity")){
                // save new data
                SqlParser parser = this.sqlStorage.getParser(0);
                
                // set schema
                JSONObject saveSchema = new JSONObject();
                saveSchema.put("name", this.activityLog);
                saveSchema.put("fields", this.activityDataFrame.get("fields"));
                // set data
                JSONObject saveData = new JSONObject();
                saveData.put("data", this.activityDataFrame.get("data"));
                // save csv file
                parser.save(this.path, this.activityLog, saveSchema, saveData);

                // load data
                initializeActivityLog();
            } else if(log.equals("repository")){
                // save new data
                SqlParser parser = this.sqlStorage.getParser(0);
                
                // set schema
                JSONObject saveSchema = new JSONObject();
                saveSchema.put("name", this.repoLog);
                saveSchema.put("fields", this.repoDataFrame.get("fields"));
                // set data
                JSONObject saveData = new JSONObject();
                saveData.put("data", this.repoDataFrame.get("data"));
                // save csv file
                parser.save(this.path, this.repoLog, saveSchema, saveData);

                // load data
                initializeRepoLog();
            } else{
                JSONObject resp = new JSONObject();
                resp.put("status", "save log error");
                resp.put("message", "could not find out log type");
                resp.put("code", "3");
                updateAction(resp);
                return;
            }

            // response
            JSONObject resp = new JSONObject();
            resp.put("status", "success");
            resp.put("message", "save log of" + log + " - done");
            resp.put("code", "200");
            updateAction(resp);

        } catch(Exception e){
            JSONObject error = new JSONObject();
            error.put("status", "save log error");
            error.put("message", e.toString());
            error.put("code", "3");
            updateAction(error);
        }
    }

    @Override
    public JSONObject getStatus(){
        if(this.sqlStorage == null){ // check storage init
            resetStatus();
            return this.status;
        } else if(this.activityDataFrame == null || this.repoDataFrame == null || this.userDataFrame == null || this.catalogDataFrame == null){ // check data loading
            resetStatus();
            return this.status;
        } else if(this.parameter == null){ // check parameter input
            resetStatus();
            updateActionError("get status error", "please setup parameter");
            return this.status;
        } else{ // perform action
            String action = (String) this.parameter.get("action");

            if(action.equals("countActivity")){ // countActivity
                resetStatus();
                Integer result = countActivity();
                JSONObject jsonResult = new JSONObject();
                jsonResult.put("row_count", (result == null) ? "" : Integer.toString(result));
                updateResult(jsonResult);
                this.parameter = null;
                return this.status;
            } else if(action.equals("listActivity")){ // listActivity
                resetStatus();
                JSONObject result = listActivity();
                // check result
                if(result == null){
                    this.parameter = null;
                    return this.status;
                }

                updateResult(result);
                this.parameter = null;
                return this.status;
            } else if(action.equals("listRepo")){ // listRepo
                resetStatus();
                JSONObject result = listRepo();
                // check result
                if(result == null){
                    this.parameter = null;
                    return this.status;
                }

                updateResult(result);
                this.parameter = null;
                return this.status;
            } else if(action.equals("registerUser")){ // registerUser
                resetStatus();
                registerUser((String) this.parameter.get("p_name"), (String) this.parameter.get("p_describe"));
                this.parameter = null;
                return this.status;
            } else if(action.equals("registerCatalog")){ // registerCatalog
                resetStatus();
                registerCatalog((String) this.parameter.get("p_name"), (String) this.parameter.get("p_describe"));
                this.parameter = null;
                return this.status;
            } else if(action.equals("listUser")){ // listUser
                resetStatus();
                List<String> result = listUser();
                if(result == null){
                    this.parameter = null;
                    return this.status;
                }

                JSONArray jsonArrayResult = new JSONArray();
                for(String name: result){
                    jsonArrayResult.add(name);
                }
                JSONObject jsonResult = new JSONObject();
                jsonResult.put("list_user", jsonArrayResult);
                updateResult(jsonResult);
                this.parameter = null;
                return this.status;
            } else if(action.equals("listCatalog")){ // listCatalog
                resetStatus();
                List<String> result = listCatalog();
                if(result == null){
                    this.parameter = null;
                    return this.status;
                }

                JSONArray jsonArrayResult = new JSONArray();
                for(String name: result){
                    jsonArrayResult.add(name);
                }
                JSONObject jsonResult = new JSONObject();
                jsonResult.put("list_catalog", jsonArrayResult);
                updateResult(jsonResult);
                this.parameter = null;
                return this.status;
            } else if(action.equals("getUserInfo")){ // getUserInfo
                resetStatus();
                String result = getUserInfo((String) this.parameter.get("p_name"));
                JSONObject jsonResult = new JSONObject();
                jsonResult.put("user_info", result);
                updateResult(jsonResult);
                this.parameter = null;
                return this.status;
            } else if(action.equals("getCatalogInfo")){ // getCatalogInfo
                resetStatus();
                String result = getCatalogInfo((String) this.parameter.get("p_name"));
                JSONObject jsonResult = new JSONObject();
                jsonResult.put("catalog_info", result);
                updateResult(jsonResult);
                this.parameter = null;
                return this.status;
            } else if(action.equals("setupTime")){ // setupTime (not usefull mehtod)
                resetStatus();
                setupTime((String) this.parameter.get("p_utc"), (String) this.parameter.get("p_timezone"));
                this.parameter = null;
                return this.status;
            } else if(action.equals("saveLog")){ // saveLog
                resetStatus();
                saveLog((String) this.parameter.get("p_log"));
                this.parameter = null;
                return this.status;
            } else{
                resetStatus();
                this.parameter = null;
                return null;
            }
        }
    }

    @Override
    public InputStream pullFile(){
        return null;
    }

    @Override
    public void pushFile(InputStream data){
        ;
    }

    @Override
    public OutputStream pushFile(){
        return null;
    }
}
