package com.usth.hieplnc.schema;

import java.sql.*;

import java.util.ArrayList;

public class DatabaseConnection{
// variable

  Connection con = null;
  Statement stmt = null;
  String rootDB = null;
  String db = null;
  int databaseID;

//=================================================//
// constructor

  public DatabaseConnection(Connection con) throws SQLException{
    this.con = con;
    this.stmt = con.createStatement();
    stmt.execute("use hivilake;");
  }

  public DatabaseConnection(Connection con, String rootDB) throws SQLException{
    this.con = con;
    this.rootDB = rootDB;

    stmt = con.createStatement();
    stmt.execute("use " + rootDB + ";");
  }

//=================================================//
// method

  public int hasDatabase(String nameDB) throws SQLException{
    String query = "SELECT id FROM virtual_database WHERE `database_name` = \"" + nameDB + "\"";
    ResultSet listDB = stmt.executeQuery(query);
    if(listDB.next()){
      return Integer.parseInt(listDB.getString(1));
    } else {
      return -1;
    }
  }

  public void createDatabase(String nameDB) throws SQLException{
    if(hasDatabase(nameDB) > 0) throw new SQLException("Database " + nameDB + " is already exits");
    String query = "INSERT INTO virtual_database (`database_name`) VALUES " + "(\"" + nameDB + "\")";
    stmt.executeUpdate(query);
  }

  public ResultSet listDatabase() throws SQLException{
    return stmt.executeQuery("SELECT `database_name` FROM `virtual_database`");
  }

  public void dropDatabase(String nameDB) throws SQLException{
    if(hasDatabase(nameDB) < 0) throw new SQLException("Database " + nameDB + " is not exists");
    useDatabase(nameDB);

    // drop table of database
    ResultSet tableList = listTable();
    while(tableList.next()){
      stmt.execute("DROP TABLE IF EXISTS `" + getTable(tableList.getString(1)) + "`;");
    }

    // delete metadata of database
    stmt.executeUpdate("DELETE FROM `map_table` WHERE `database_id` = \"" + databaseID + "\";");
    stmt.executeUpdate("DELETE FROM `virtual_database` WHERE `id` = \"" + databaseID + "\";");

    // stop using database
    databaseID = 0;
    db = null;
  }

  public void useDatabase(String nameDB) throws SQLException{
    int tempID = hasDatabase(nameDB);
    if(tempID < 0) throw new SQLException("Database " + nameDB + " is not exists");

    this.db = nameDB;
    this.databaseID = tempID;
  }

  public void createTable(String tableName, ArrayList<String> columnName, ArrayList<String> columnType) throws SQLException{
    if(db == null) throw new NullPointerException("Null database");
    else if(columnName.size() != columnType.size()) throw new SQLException("Column name and column type is not equal");
    
    // create real table name
    String tablePos = this.db + "_" + tableName;

    // update metadata
    String query = "INSERT INTO map_table (`database_id`, `table_name`, `table_pos`) VALUES ";
    query += "(\"" + this.databaseID + "\",\"" + tableName + "\",\"" + tablePos + "\");";
    stmt.executeUpdate(query);

    // gen database table
    query = "CREATE TABLE " + tablePos;
    // gen primary key
    query += "(id INT AUTO_INCREMENT PRIMARY KEY";
    // gen column
    for(int i = 0; i < columnName.size(); i++){
      query += ",`" + columnName.get(i) + "` " + columnType.get(i);
    }
    // set engine
    query += ") ENGINE = InnoDB;";

    stmt.execute(query);
  }

  public ResultSet listTable() throws SQLException{
    if(db == null) throw new NullPointerException("Null database");
    String query = "SELECT `table_name` FROM `map_table` WHERE `database_id` = \"" + databaseID + "\";";
    return stmt.executeQuery(query);
  }

  public String getTable(String table) throws SQLException{
    if(db == null) throw new NullPointerException("Null database");
    String query = "SELECT `table_pos` FROM `map_table` WHERE `database_id` = \"" + databaseID + "\" AND `table_name` = \"" + table + "\";";
    ResultSet result = stmt.executeQuery(query);
    result.next();
    return result.getString(1);
  }

  public void close() throws SQLException{
    try{
      stmt.close();
    } catch(NullPointerException e){}
  }

  public void getMetaData() throws SQLException{
    ResultSet result = listDatabase();
    while(result.next()){
      String name = result.getString(1);
      System.out.println(name);
    }
  }
}