package com.usth.hieplnc.util.base.storagemanager;

/**
 * DOC:
 * - This util allow store and manage file in fs 
 *
 */
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import com.usth.hieplnc.storage.api.*;
import com.usth.hieplnc.storage.api.filesystem.*;
import com.usth.hieplnc.storage.api.filesystem.model.*;
import com.usth.hieplnc.storage.api.sql.*;
import com.usth.hieplnc.storage.api.sql.model.*;

import com.usth.hieplnc.util.api.Service;
import com.usth.hieplnc.util.base.HVUtilException;
import com.usth.hieplnc.util.base.systemlog.SystemLog;
import com.usth.hieplnc.util.base.systemlog.model.*;

/**
 * WARNING:
 * - system should not return object JSONObject status directly to outside
 * - system did not null checking action properly
 *
 */

public class StorageManager implements Service{
// variable
    private final FilesystemWrapper fsStorage;
    private final SqlWrapper sqlStorage;
    private final SystemLog systemLog;

    private JSONObject status;
    private JSONObject parameter = null;

//=================================================================//
// constructor
    public StorageManager(StorageWrapper storage, SystemLog systemLog) throws HVUtilException{
        // load storage
        List<String> supportType = storage.support();
        if(!supportType.contains("filesystem") || !supportType.contains("sql")){
            throw new HVUtilException("HVUtilStorageManagerException (code 0): the storage support type does not satify requirement");
        }
        this.fsStorage = (FilesystemWrapper) storage;
        this.sqlStorage = (SqlWrapper) storage;

        if(systemLog == null){
            throw new HVUtilException("HVUtilStorageManagerException (code 0): system log is not exists");
        }else{
            this.systemLog = systemLog;
        }

        initStatus();
    }

    private StorageManager(FilesystemWrapper fsStorage, SqlWrapper sqlStorage, SystemLog systemLog){
        this.fsStorage = fsStorage;
        this.sqlStorage = sqlStorage;
        this.systemLog = systemLog;

        initStatus();
    }

//=================================================================//
// method
    // log working method
    private boolean isLogActionDone(JSONObject rawStatus){
        JSONObject action = (JSONObject) rawStatus.get("action");
        JSONObject resp = (JSONObject) action.get("response");
        String code = (String) resp.get("code");
        return code.equals("200");
    }
    // end log working method

    private void initStatus(){
        // init system
        JSONObject system = new JSONObject();
        system.put("error", new JSONArray());

        // init status
        this.status = new JSONObject();
        this.status.put("system", system);
        this.status.put("action", "");
        this.status.put("result", "");
    }

    private void addSysError(String status, String message){
        JSONObject system = (JSONObject) this.status.get("system");
        JSONArray error = (JSONArray) system.get("error");
        
        // new error
        JSONObject newError = new JSONObject();
        newError.put("status", status);
        newError.put("message", message);
        newError.put("code", 1);
        error.add(newError);
    }

    private void updateAction(String status, String message, String code){
        JSONObject action = new JSONObject();
        action.put("status", status);
        action.put("message", message);
        action.put("code", code);
        this.status.replace("action", action);
    }

    private void resetStatus(){
        this.status.replace("action", "");
        this.status.replace("result", "");
    }

    public void createRepo(String path, JSONObject schema){
        boolean clean = false;
        try{
            if(this.fsStorage.exists(path)){
                updateAction("create repo error", "path has already exists", "3");
            } else{
                // create repo
                this.fsStorage.createPath(path, PathType.DIR);
                clean = true; // ready to clean if failure happen

                // add sub-directory for new repo
                // metadata
                String metaPath = path + "/" + ".hivilake";
                this.fsStorage.createPath(metaPath, PathType.DIR);

                // get fields of meta
                List<String> fields = new ArrayList<String>();
                fields.add("user");
                fields.add("time");
                fields.add("name");
                fields.add("type");
                fields.add("format");
                fields.add("label");
                fields.add("description");
                fields.add("path");
                fields.add("a_type");
                fields.add("a_format");
                fields.add("size");
                fields.add("status");
                fields.add("meta_null");
                fields.addAll((List) schema.get("fields"));
                
                // get list of type
                List<Integer> type = new ArrayList<Integer>();
                for(int i = 0; i < fields.size(); i++){
                    type.add(DataType.STRING);
                }

                JSONObject metaSchema = new JSONObject();
                metaSchema.put("fields", fields);
                metaSchema.put("type", type);
                metaSchema.put("name", "meta");
                
                // meta file
                this.sqlStorage.createTable(metaPath, "meta", metaSchema, this.sqlStorage.getParser(0));

                // data
                String dataPath = path + "/" + "data";
                this.fsStorage.createPath(dataPath, PathType.DIR);

                // update log
                RepoLogModel log = new RepoLogModel();
                log.name = path;
                log.describe = (String) schema.get("describe");
                log.type = (String) schema.get("type");

                // work with system time
                Calendar systemCalendar = Calendar.getInstance();

                TimeZone systemTimezone = systemCalendar.getTimeZone();
                log.timeLocation = systemTimezone.getDisplayName();

                Date systemDate = systemCalendar.getTime();
                log.updateTime = systemDate.getTime();
                log.auditTime = log.updateTime;

                // add extra info
                log.status = (String) schema.get("status");
                log.notes = (String) schema.get("notes");

                // add extra info to repo before commit it to system log
                JSONObject extraInfo = new JSONObject();
                extraInfo.put("update_time", Long.toString(log.updateTime));
                extraInfo.put("create_time", Long.toString(log.updateTime));
                extraInfo.put("total_file", Long.toString(log.totalFile));
                extraInfo.put("quality_file", Long.toString(log.qualityFile));
                extraInfo.put("size", Float.toString(log.size));
                extraInfo.put("audit_time", Long.toString(log.auditTime));
                extraInfo.put("timezone", log.timeLocation);
                extraInfo.put("name", log.name);
                extraInfo.put("desc", log.describe);

                String extraInfoPath = metaPath + "/repo_tracking.json";
                this.fsStorage.createPath(extraInfoPath, PathType.FILE);
                this.fsStorage.openFile(extraInfoPath).writeStream(new ByteArrayInputStream(extraInfo.toString().getBytes()));

                // commit repo to system log
                this.systemLog.addRepo(log);
                this.systemLog.saveLog("repository");

                // check error
                JSONObject rawStatus = (JSONObject) this.systemLog.getRawStatus();
                JSONObject statusAction = (JSONObject) rawStatus.get("action");
                JSONObject resp = (JSONObject) statusAction.get("response");
                String codeStatus = (String) resp.get("code");
                if(!codeStatus.equals("200")){
                    throw new Exception("can not add repo to system log, log error response: " + resp.toString());
                }

                updateAction("create repo", "create new repo - done", "200");
            }
        } catch(Exception e){
            updateAction("create repo error", e.toString(), "3");
            
            // cleaning if repo creation fail
            try{
                if(clean){
                    if(this.fsStorage.exists(path)){
                        this.fsStorage.deletePath(path, SWOption.ALL);
                    }
                }
            } catch(Exception forgetE){;}
        }
    }

    public void updateRepo(int repoId, InputStream file, JSONObject meta){
        try{
            String repoPath = this.systemLog.getRepoLocation(repoId);
            if(repoPath == null){
                updateAction("update repo error", "can not get repo path", "3");
                return;
            }

            // check exists requirement fields
            String ch_user = (String) meta.get("user");
            String ch_name = (String) meta.get("name");
            String ch_type = (String) meta.get("type");
            String ch_format = (String) meta.get("format");
            String ch_label = (String) meta.get("label");

            String[] ch_list = {ch_user, ch_name, ch_type, ch_format, ch_label};
            for(String value: ch_list){
                if(value == null){
                    updateAction("update repo error", "requirement fields checking failure", "3");
                    return;
                }
            }

            // check exists user
            this.systemLog.getUserInfo(ch_user);
            if(!isLogActionDone(this.systemLog.getRawStatus())){
                updateAction("update repo error", "user checking failure with log: " + this.systemLog.getRawStatus().toString(), "3");
                return;
            }

            // check exists label
            this.systemLog.getCatalogInfo(ch_label);
            if(!isLogActionDone(this.systemLog.getRawStatus())){
                updateAction("update repo error", "catalog checking failure with log: " + this.systemLog.getRawStatus().toString(), "3");
                return;
            }

            // get meta input
            List<String> inputFields = new ArrayList<String>();
            List<String> inputData = new ArrayList<String>();
            inputFields.addAll((Set<String>) meta.keySet());
            for(String field: inputFields){
                inputData.add((String) meta.get(field));
            }

            // fullfill meta input
            // setup upload time
            Date inputDate = new Date();
            if(inputFields.indexOf("time") > -1){
                int index = inputFields.indexOf("time");
                inputData.set(index, Long.toString(inputDate.getTime()));
            } else{
                inputFields.add("time");
                inputData.add(Long.toString(inputDate.getTime()));
            }

            // setup path file
            String uploadPath = repoPath + "/data/" + Long.toString(inputDate.getTime()) + "." + ch_user + "." + ch_name + "." + ch_format;
            if(inputFields.indexOf("path") > -1){
                int index = inputFields.indexOf("path");
                inputData.set(index, uploadPath);
            } else{
                inputFields.add("path");
                inputData.add(uploadPath);
            }

            // load meta file
            String metaFile = repoPath + "/.hivilake/meta.csv";
            SqlResult metaResult = this.sqlStorage.use(metaFile, null).commit();
            
            // modify meta
            List<List<String>> data = (List<List<String>>) metaResult.getData().get("data");
            metaResult.getSchema().replace("fields", data.remove(0));

            // generate new meta
            SqlTable newMetaTable = this.sqlStorage.use(metaResult);
            newMetaTable.insert(inputFields, inputData);
            this.sqlStorage.addTable(repoPath + "/.hivilake", "meta", newMetaTable.commit(), this.sqlStorage.getParser(0));
            
            // upload file
            this.fsStorage.createPath(uploadPath, PathType.FILE);
            this.fsStorage.openFile(uploadPath).writeStream(file);

            updateAction("update repo", "update new repo file - done", "200");
        } catch(Exception e){
            updateAction("update repo error", e.toString(), "3");
        }
    }

    public OutputStream updateRepo(int repoId, JSONObject meta){
        try{
            String repoPath = this.systemLog.getRepoLocation(repoId);
            if(repoPath == null){
                updateAction("update repo error", "can not get repo path", "3");
                return null;
            }

            // check exists requirement fields
            String ch_user = (String) meta.get("user");
            String ch_name = (String) meta.get("name");
            String ch_type = (String) meta.get("type");
            String ch_format = (String) meta.get("format");
            String ch_label = (String) meta.get("label");

            String[] ch_list = {ch_user, ch_name, ch_type, ch_format, ch_label};
            for(String value: ch_list){
                if(value == null){
                    updateAction("update repo error", "requirement fields checking failure", "3");
                    return null;
                }
            }

            // check exists user
            this.systemLog.getUserInfo(ch_user);
            if(!isLogActionDone(this.systemLog.getRawStatus())){
                updateAction("update repo error", "user checking failure with log: " + this.systemLog.getRawStatus().toString(), "3");
                return null;
            }

            // check exists label
            this.systemLog.getCatalogInfo(ch_label);
            if(!isLogActionDone(this.systemLog.getRawStatus())){
                updateAction("update repo error", "catalog checking failure with log: " + this.systemLog.getRawStatus().toString(), "3");
                return null;
            }

            // get meta input
            List<String> inputFields = new ArrayList<String>();
            List<String> inputData = new ArrayList<String>();
            inputFields.addAll((Set<String>) meta.keySet());
            for(String field: inputFields){
                inputData.add((String) meta.get(field));
            }

            // fullfill meta input
            // setup upload time
            Date inputDate = new Date();
            if(inputFields.indexOf("time") > -1){
                int index = inputFields.indexOf("time");
                inputData.set(index, Long.toString(inputDate.getTime()));
            } else{
                inputFields.add("time");
                inputData.add(Long.toString(inputDate.getTime()));
            }

            // setup path file
            String uploadPath = repoPath + "/data/" + Long.toString(inputDate.getTime()) + "." + ch_user + "." + ch_name + "." + ch_format + ".bin";
            if(inputFields.indexOf("path") > -1){
                int index = inputFields.indexOf("path");
                inputData.set(index, uploadPath);
            } else{
                inputFields.add("path");
                inputData.add(uploadPath);
            }

            // load meta file
            String metaFile = repoPath + "/.hivilake/meta.csv";
            SqlResult metaResult = this.sqlStorage.use(metaFile, null).commit();
            
            // modify meta
            List<List<String>> data = (List<List<String>>) metaResult.getData().get("data");
            metaResult.getSchema().replace("fields", data.remove(0));

            // generate new meta
            SqlTable newMetaTable = this.sqlStorage.use(metaResult);
            newMetaTable.insert(inputFields, inputData);
            this.sqlStorage.addTable(repoPath + "/.hivilake", "meta", newMetaTable.commit(), this.sqlStorage.getParser(0));
            
            // upload file
            this.fsStorage.createPath(uploadPath, PathType.FILE);
            OutputStream streamWriter = this.fsStorage.openFile(uploadPath).getStreamWriter();
            if(streamWriter == null){
                throw new Exception("could not retrieve file writer");
            }

            updateAction("update repo", "update new repo file - done", "200");
            return streamWriter;
        } catch(Exception e){
            updateAction("update repo error", e.toString(), "3");
            return null;
        }
    }
    
    public void auditRepo(int repoId){
        updateAction("audit repo error", "this action is not supported yet", "3");
    }

    @Override
    public boolean setParameter(JSONObject param){
        try{
            this.parameter  = new JSONObject();

            // load parameter
            this.parameter.put("action", param.get("action"));
            String action = (String) param.get("action");

            // check action argument
            if(action.equals("createRepo")){
                JSONObject input = (JSONObject) param.get("parameter");
                if(input.get("path") == null || input.get("schema") == null){
                    this.parameter = null;
                    return false;
                } else{
                    this.parameter.put("p_path", input.get("path"));
                    this.parameter.put("p_schema", input.get("schema"));
                    return true;
                }
            } else if(action.equals("updateRepo")){
                JSONObject input = (JSONObject) param.get("parameter");
                if(input.get("repoId") == null || input.get("meta") == null){
                    this.parameter = null;
                    return false;
                } else{
                    String repoId = (String) input.get("repoId");
                    int repoIdInt;

                    try{ // convert repoId from String to Int
                        repoIdInt = Integer.parseInt(repoId);
                    } catch(Exception e){
                        this.parameter = null;
                        return false;
                    }

                    this.parameter.put("p_repoId", repoIdInt);
                    this.parameter.put("p_meta", input.get("meta"));
                    this.parameter.put("c_skip", 0);
                    return true;
                }
            } else if(action.equals("auditRepo")){
                this.parameter = null;
                return false;
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

        if(action.equals("createRepo")){
            resetStatus();
            String path = (String) this.parameter.get("p_path");
            JSONObject schema = (JSONObject) this.parameter.get("p_schema");

            createRepo(path, schema);
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
        return null;
    }

    @Override
    public void pushFile(InputStream data){
        // check null param
        if(this.parameter == null){
            return;
        } else{
            Integer skip = (Integer) this.parameter.get("c_skip");
            if(skip == 1){
                return;
            }
        }

        // perform action
        String action = (String) this.parameter.get("action");

        if(action.equals("updateRepo")){
            resetStatus();
            int repoId = (int) this.parameter.get("p_repoId");
            JSONObject meta = (JSONObject) this.parameter.get("p_meta");
            updateRepo(repoId, data, meta);
            this.parameter.replace("c_skip", 1);
        }
    }

    @Override
    public OutputStream pushFile(){
        // check null param
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

        if(action.equals("updateRepo")){
            resetStatus();
            int repoId = (int) this.parameter.get("p_repoId");
            JSONObject meta = (JSONObject) this.parameter.get("p_meta");
            OutputStream fileWriter = updateRepo(repoId, meta);
            this.parameter.replace("c_skip", 1);
            return fileWriter;
        } else{
            return null;
        }
    }

    @Override
    public Service duplicate(){
        return new StorageManager(this.fsStorage, this.sqlStorage, (SystemLog) this.systemLog.duplicate());
    }
}