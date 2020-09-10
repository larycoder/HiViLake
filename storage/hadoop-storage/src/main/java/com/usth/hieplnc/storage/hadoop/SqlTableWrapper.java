package com.usth.hieplnc.storage.hadoop;

/**
 * DOC:
 * - the implementation class for SqlTable api of Hivilake
 *
 */

import java.lang.UnsupportedOperationException;
import java.lang.RuntimeException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Comparator;
import java.io.IOException;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.*;
import com.usth.hieplnc.storage.api.sql.model.*;
import com.usth.hieplnc.storage.hadoop.SqlResultWrapper;

/**
 * WARNING:
 * - The loadData method assume string type for all table is does not exists type
 *
 */

public class SqlTableWrapper implements SqlTable{
// variable
    private SqlParser loadEngine = null;
    private String path = null;

    private JSONObject dataFrame = null;

    private final String name;
    private String alias = null;

    private JSONObject insertAction = null;
    private JSONObject updateAction = null;
    private JSONObject deleteAction = null;
    private JSONObject alterAction = null;
    private JSONObject selectAction = null;
    private JSONObject joinAction = null;
    private JSONObject unionAllAction = null;

    private JSONObject meta = null;

//=================================================================//
// constructor
    public SqlTableWrapper(String tableName, SqlParser engine, String path){
        this.loadEngine = engine;
        this.name = tableName;
        this.path = path;
    }

//=================================================================//
// method
    public void setMeta(JSONObject meta){
        this.meta = meta;
    }

    @Override
    public JSONObject getMeta(){
        this.meta = new JSONObject();
        this.meta.put("name", this.name);
        this.meta.put("alias", this.alias);
        return this.meta;
    }

    @Override
    public SqlTable as(String tableName){
        this.alias = tableName;
        return this;
    }

    private void inactiveAction(){
        this.insertAction = null;
        this.updateAction = null;
        this.deleteAction = null;
        this.alterAction = null;
        this.selectAction = null;
        this.joinAction = null;
        this.unionAllAction = null;
    }

    @Override
    public SqlTable insert(List<String> columns, List<String> data){
        JSONObject insertAction = new JSONObject();
        insertAction.put("fields", columns);
        insertAction.put("data", data);

        inactiveAction();
        this.insertAction = insertAction;
        return this;
    }

    @Override
    public SqlTable update(List<String> columns, List<String> data, SqlCondition condition){
        JSONObject updateAction = new JSONObject();
        updateAction.put("fields", columns);
        updateAction.put("data", data);
        updateAction.put("condition", condition);

        inactiveAction();
        this.updateAction = updateAction;
        return this;
    }

    @Override
    public SqlTable delete(SqlCondition condition){
        JSONObject deleteAction = new JSONObject();
        deleteAction.put("condition", condition);

        inactiveAction();
        this.deleteAction = deleteAction;
        return this;
    }

    @Override
    public SqlTable alterCol(String name, int option, JSONObject extra){
        throw new UnsupportedOperationException("The alterCol method is not supported yet");
    }

    @Override
    public SqlTable select(List<Col> col, SqlFunc condition){
        JSONObject selectAction = new JSONObject();
        selectAction.put("fields", col);
        selectAction.put("condition", condition);

        inactiveAction();
        this.selectAction = selectAction;
        return this;
    }

    @Override
    public SqlTable join(SqlTable table, String leftCol, String rightCol){
        JSONObject joinAction = new JSONObject();
        joinAction.put("table", table);
        joinAction.put("leftCol", leftCol);
        joinAction.put("rightCol", rightCol);

        inactiveAction();
        this.joinAction = joinAction;
        return this;
    }

    @Override
    public SqlTable leftJoin(SqlTable table, String leftCol, String rightCol){
        throw new UnsupportedOperationException("The leftJoin method is not supported yet");
    }

    @Override
    public SqlTable rightJoin(SqlTable table, String leftCol, String rightCol){
        throw new UnsupportedOperationException("The rightJoin method is not supported yet");
    }

    @Override
    public SqlTable fullJoin(SqlTable table, String leftCol, String rightCol){
        throw new UnsupportedOperationException("The fullJoin method is not supported yet");
    }

    @Override
    public SqlTable union(SqlTable table){
        throw new UnsupportedOperationException("The union method is not supported yet");
    }

    @Override
    public SqlTable unionAll(SqlTable table){
        JSONObject unionAllAction = new JSONObject();
        unionAllAction.put("table", table);

        inactiveAction();
        this.unionAllAction = unionAllAction;
        return this;
    }

    private void loadData() throws IOException{
        if(this.loadEngine != null){
            this.dataFrame = this.loadEngine.load(this.path);
            if(!this.dataFrame.containsKey("type")){
                List<Integer> type = new ArrayList<Integer>();
                List<String> fields = (List<String>) this.dataFrame.get("fields");

                for(int i = 0; i < fields.size(); i++){
                    type.add(DataType.STRING);
                }

                dataFrame.put("type", type);
            }
        }
    }

    private void performInsertAction(){
        List<List<String>> rowList = (List<List<String>>) this.dataFrame.get("data");
        List<String> fields = (List<String>) this.dataFrame.get("fields");
        List<String> insertFields = (List<String>) this.insertAction.get("fields");
        List<String> insertData = (List<String>) this.insertAction.get("data");
        List<String> newRow = new ArrayList<String>();

        // push null to new row
        for(int i = 0; i < fields.size(); i++){
            newRow.add("");
        }

        // add real value to new row
        for(int i = 0; i < insertFields.size(); i++){
            int index = fields.indexOf((String) insertFields.get(i));
            newRow.set(index, (String) insertData.get(i));
        }

        // add row to data
        rowList.add(newRow);
    }

    private void performUpdateAction() throws HVSqlConditionException{
        List<List<String>> rowList = (List<List<String>>) this.dataFrame.get("data");
        List<String> fields = (List<String>) this.dataFrame.get("fields");
        List<Integer> type = (List<Integer>) this.dataFrame.get("type");

        List<String> updateFields = (List<String>) this.updateAction.get("fields");
        List<String> updateData = (List<String>) this.updateAction.get("data");
        SqlCondition condition = (SqlCondition) this.updateAction.get("condition");

        JSONObject updateCondition = new JSONObject();
        updateCondition.put("fields", fields);
        updateCondition.put("type", type);
        updateCondition.put("data", "");

        for(List<String> row: rowList){
            updateCondition.replace("data", row);
            if(condition.check(updateCondition)){
                for(int i = 0; i < updateFields.size(); i++){
                    int index = fields.indexOf((String) updateFields.get(i));
                    row.set(index, (String) updateData.get(i));
                }
            }
        }
    }

    private void performDeleteAction() throws HVSqlConditionException{
        List<List<String>> rowList = (List<List<String>>) this.dataFrame.get("data");
        List<String> fields = (List<String>) this.dataFrame.get("fields");
        List<Integer> type = (List<Integer>) this.dataFrame.get("type");

        SqlCondition condition = (SqlCondition) this.updateAction.get("condition");

        JSONObject delCondition = new JSONObject();
        delCondition.put("fields", fields);
        delCondition.put("type", type);
        delCondition.put("data", "");

        for(int i = 0; i < rowList.size(); i++){
            List<String> row = rowList.get(i);
            delCondition.replace("data", row);
            if(condition.check(delCondition)){
                rowList.remove(i);
                i--;
            }
        }
    }

    private void performSelectAction() throws HVSqlException{
        List<String> fields = (List<String>) this.dataFrame.get("fields");
        List<List<String>> data = (List<List<String>>) this.dataFrame.get("data");
        List<Integer> type = (List<Integer>) this.dataFrame.get("type");

        List<Col> selectCol = (List<Col>) selectAction.get("fields");
        SqlFunc selectCondition = (SqlFunc) this.selectAction.get("condition");

        // split column by its type
        List<Col> virtualCol = new ArrayList<Col>();
        List<Col> realCol = new ArrayList<Col>();
        for(Col column: selectCol){
            if(column.getType() == ColType.IMAGE){
                virtualCol.add(column);
            } else{
                realCol.add(column);
            }
        }
        
        // add virtual column to field
        for(Col column: virtualCol){
            if(fields.contains(column.getAlias())){
                throw new HVSqlDuplicateFieldException("The field with name " + column.getAlias() + " is already existed");
            }
            fields.add(column.getAlias());
            type.add(column.getDataType());
        }

        // add virtual data to data and check where condition
        JSONObject whereCondition = new JSONObject();
        whereCondition.put("fields", fields);
        whereCondition.put("type", type);
        whereCondition.put("data", "");
        for(int i = 0; i < data.size(); i++){
            List<String> row = data.get(i);

            for(Col column: virtualCol){
                row.add(column.getName());
            }

            whereCondition.replace("data", row);
            if(!selectCondition.getWhere(whereCondition)){
                data.remove(i);
                i--;
            }
        }

        // get selection column
        List<Integer> indexList = new ArrayList<Integer>();
        for(Col column: selectCol){
            indexList.add(fields.indexOf(column.getName()));
        }

        // update fields
        List<String> tempData = new ArrayList<String>();
        for(Col column: selectCol){
            if(column.getType() == ColType.REAL){
                if(column.getAlias() != null){
                    tempData.add(column.getAlias());
                } else{
                    tempData.add(column.getName());
                }
            } else{
                tempData.add(column.getAlias());
            }
        }
        fields.clear();
        fields.addAll(tempData);

        // update type of fields
        List<Integer> tempType = new ArrayList<Integer>();
        for(int index: indexList){
            tempType.add(type.get(index));
        }
        type.clear();
        type.addAll(tempType);

        // update data of fields
        for(List<String> row: data){
            tempData.clear();
            for(int index: indexList){
                tempData.add(row.get(index));
            }
            row.clear();
            row.addAll(tempData);
        }

        // check duplicate in fields
        HashSet checkField = new HashSet(fields);
        if(checkField.size() != fields.size()) throw new HVSqlDuplicateFieldException("The duplicate happen in fields of table " + this.name + " after alias");

        // group data
        List<String> groupStringCol = (List<String>) selectCondition.getGroup();
        if(groupStringCol != null){
            List<Col> groupCol = new ArrayList<Col>();
            SqlExpr groupAnd = new SqlExpr();
            JSONObject groupRow = whereCondition;

            // convert Col to SqlCondition to add into "AND"
            List<SqlCondition> groupSqlCondition = new ArrayList<SqlCondition>();
            for(int i = 0; i < groupCol.size(); i++){
                groupSqlCondition.add((SqlCondition) groupCol.get(i));
            }
            groupAnd.and(groupSqlCondition);

            for(String colName: groupStringCol){
                groupCol.add(new Col(colName, ColType.REAL, DataType.STRING));
            }

            for(int i = 0; i < data.size(); i++){
                // create column condition
                List<String> row = (List<String>) data.get(i);
                for(Col column: groupCol){
                    int index = fields.indexOf(column.getName());
                    column.eq(row.get(index));
                }

                // update below row by column condition
                for(int j = i + 1; j < data.size(); j++){
                    groupRow.replace("data", data.get(j));
                    if(groupAnd.check(groupRow)){
                        data.remove(j);
                        j--;
                    }
                }
            }
        }

        // check having condition
        JSONObject havingCondition = whereCondition;
        for(int i = 0; i < data.size(); i++){
            havingCondition.replace("data", data.get(i));
            if(!selectCondition.getHaving(havingCondition)){
                data.remove(i);
                i--;
            }
        }

        // re-order data
        JSONObject orderBy = selectCondition.getOrderBy();
        if(orderBy != null){
            String orderCol = (String) orderBy.get("fields");
            Integer orderType = (Integer) type.get(fields.indexOf(orderCol));

            final int orderColIndex = fields.indexOf(orderCol);
            final JSONObject orderCondition = whereCondition;
            final Col compareCol = new Col(orderCol, ColType.REAL, orderType);
            final int orderOpt = (int) orderBy.get("option");

            data.sort(new Comparator<Object>(){
                @Override
                public int compare(Object objRow1, Object objRow2){
                    // get compare value
                    List<String> row1 = (List<String>) objRow1;
                    List<String> row2 = (List<String>) objRow2;

                    orderCondition.replace("data", row2);

                    compareCol.eq(row1.get(orderColIndex));
                    if(orderOpt == OrderOpt.ASC){
                        compareCol.gt(row1.get(orderColIndex));
                    } else{
                        compareCol.lt(row1.get(orderColIndex));
                    }
                    try{
                        if(compareCol.check(orderCondition)){
                            return 1;
                        } else{
                            return -1;
                        }
                    } catch(HVSqlException e){
                        throw new RuntimeException(e.toString());
                    }
                }
            });
        }

        // limit row
        Integer limitThreshold = selectCondition.getLimit();
        if(limitThreshold != null){
            for(int i = 0; i < data.size(); i++){
                if(!(i < limitThreshold)){
                    data.remove(i);
                    i--;
                }
            }
        }
    }

    private void performJoinAction() throws HVSqlException, IOException{
        SqlTable joinTable = (SqlTable) this.joinAction.get("table");
        String leftCol = (String) this.joinAction.get("leftCol");
        String rightCol = (String) this.joinAction.get("rightCol");

        // get dataframe of join table
        SqlResult joinResultTable = joinTable.commit();
        JSONObject joinDataFrame = joinResultTable.getSchema();
        joinDataFrame.put("data", joinResultTable.getData().get("data"));

        // get field and compare position of 2 table
        List<String> leftFields = (List<String>) this.dataFrame.get("fields");
        List<String> rightFields = (List<String>) joinDataFrame.get("fields");

        int leftIndex = leftFields.indexOf(leftCol);
        int rightIndex = rightFields.indexOf(rightCol);

        // get data of 2 table
        List<List<String>> leftData = (List<List<String>>) this.dataFrame.get("data");
        List<List<String>> rightData = (List<List<String>>) joinDataFrame.get("data");

        // check type of fields
        List<Integer> leftType = (List<Integer>) this.dataFrame.get("type");
        List<Integer> rightType = (List<Integer>) joinDataFrame.get("type");
        if(leftType.get(leftIndex) != rightType.get(rightIndex)){
            throw new HVSqlIncompatibleException("The join action error because of 2 threshold fields is not same type");
        }

        // create checking condition
        Col joinCondition = new Col(rightCol, ColType.REAL, leftType.get(leftIndex));

        // join 2 table data
        List<List<String>> newTable = new ArrayList<List<String>>();

        JSONObject joinRow = new JSONObject();
        joinRow.put("fields", joinDataFrame.get("fields"));
        joinRow.put("type", joinDataFrame.get("type"));
        joinRow.put("data", "");

        for(List<String> leftRow: leftData){
            // set compare value
            joinCondition.eq(leftRow.get(leftIndex));
            
            // merge with right table data
            for(List<String> rightRow: rightData){
                joinRow.replace("data", rightRow);
                if(joinCondition.check(joinRow)){
                    List<String> newRow = new ArrayList<String>();
                    newRow.addAll(leftRow);
                    newRow.addAll(rightRow);
                    newTable.add(newRow);
                }
            }
        }

        // merge 2 table fields and type
        List<String> newFields = new ArrayList<String>();
        List<Integer> newType = new ArrayList<Integer>();

        for(int i = 0; i < leftFields.size(); i++){
            // merge field
            String field = leftFields.get(i);
            if(this.alias != null){
                newFields.add(this.alias + "." + field);
            } else{
                newFields.add(this.name + "." + field);
            }
            // merge type
            newType.add(leftType.get(i));
        }

        JSONObject rightTableMeta = joinTable.getMeta();
        String rightName = (String) rightTableMeta.get("name");
        String rightAlias = (String) rightTableMeta.get("alias");
        for(int i = 0; i < rightFields.size(); i++){
            // merge field
            String field = rightFields.get(i);
            if(rightAlias != null){
                newFields.add(rightAlias + "." + field);
            } else{
                newFields.add(rightAlias + "." + field);
            }
            // merge type
            newType.add(rightType.get(i));
        }

        // update dataframe
        this.dataFrame.replace("fields", newFields);
        this.dataFrame.replace("type", newType);
        this.dataFrame.replace("data", newTable);
    }

    private void performUnionAllAction() throws HVSqlException, IOException{
        SqlTable unionTable = (SqlTable) this.unionAllAction.get("table");

        // get dataframe of union table
        SqlResult unionTableResult = unionTable.commit();
        JSONObject unionDataFrame = unionTableResult.getSchema();
        unionDataFrame.put("data", (List<List<String>>) unionTableResult.getData().get("data"));

        // get fields of 2 table
        List<String> topFields = (List<String>) this.dataFrame.get("fields");
        List<String> bottomFields = (List<String>) unionDataFrame.get("fields");

        // get type of 2 table
        List<Integer> topType = (List<Integer>) this.dataFrame.get("type");
        List<Integer> bottomType = (List<Integer>) unionDataFrame.get("type");

        // get data of 2 table
        List<List<String>> topData = (List<List<String>>) this.dataFrame.get("data");
        List<List<String>> bottomData = (List<List<String>>) unionDataFrame.get("data");

        // verify 2 fields compatible
        if(topFields.size() != bottomFields.size()) throw new HVSqlIncompatibleException("union all action error because 2 field is not incompatible");

        List<Integer> indexList = new ArrayList<Integer>();
        for(int i = 0; i < topFields.size(); i++){
            int index = bottomFields.indexOf(topFields.get(i));
            if(index < 0){
                throw new HVSqlIncompatibleException("union all action error because 2 field is not incompatible");
            }
            indexList.add(index);

            // verify 2 type compatible
            if(topType.get(i).intValue() != bottomType.get(index).intValue()){
                throw new HVSqlIncompatibleException("union all action error because 2 table data type is not incompatible");
            }
        }

        // union 2 table
        for(int i = 0; i < bottomData.size(); i++){
            List<String> row = bottomData.get(i);
            // re-order row
            List<String> newRow = new ArrayList<String>();
            for(int j = 0; j < indexList.size(); j++){
                newRow.add(row.get(indexList.get(j)));
            }
            // union new row
            topData.add(newRow);
        }
    }

    @Override
    public SqlResult commit() throws HVSqlException, IOException{
        // load data
        loadData();
        
        // perform action
        if(this.insertAction != null){
            performInsertAction();
        } else if(this.updateAction != null){
            performUpdateAction();
        } else if(this.deleteAction != null){
            performDeleteAction();
        } else if(this.selectAction != null){
            performSelectAction();
        } else if(this.joinAction != null){
            performJoinAction();
        } else if(this.unionAllAction != null){
            performUnionAllAction();
        }

        // build result
        String tableName = (this.alias != null) ? this.alias : this.name;
        SqlResult finalResult = null;
        if(dataFrame != null){
            finalResult = new SqlResultWrapper(tableName, (List<String>) this.dataFrame.get("fields"), (List<Integer>) this.dataFrame.get("type"), (List<List<String>>) this.dataFrame.get("data"));
        }

        // clear action after commit
        inactiveAction();
        this.dataFrame = null;

        return finalResult;
    }
}