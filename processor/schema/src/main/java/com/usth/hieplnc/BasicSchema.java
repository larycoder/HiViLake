package com.usth.hieplnc;

import java.sql.*;

public class BasicSchema{
    //================================================================================//
    // variable
    Connection con = null;
    private String url = "jdbc:mariadb://kylo-mysql:3306/?user=root&password=password";

    //================================================================================//
    // constructor
    public BasicSchema(String url) throws SQLException{
        this.url = url;
        this.con = DriverManager.getConnection(url);
    }

    public BasicSchema() throws SQLException{ // factory constructor
        this.con = DriverManager.getConnection(url);
    }

    //================================================================================//
    // method
    public void resetConnection() throws SQLException{
        closeConnection();
        con = DriverManager.getConnection(url);
    }

    public Connection getConnection(){
        return con;
    }

    public void closeConnection() throws SQLException{
        try{
            con.close();
        } catch(NullPointerException e){}
    }

    public static void main( String[] args ) throws SQLException{
        BasicSchema bs = new BasicSchema();
        Connection con = bs.getConnection();
        
        DatabaseMetaData listDB = con.getMetaData();
        ResultSet result = listDB.getCatalogs();
        while(result.next()){
            String nameDB = result.getString(1);
            System.out.println(nameDB);
        }

        bi.closeConnection();
    }
}
