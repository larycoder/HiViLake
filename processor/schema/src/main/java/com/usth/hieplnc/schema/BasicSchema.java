package com.usth.hieplnc.schema;

import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import com.usth.hieplnc.schema.DatabaseConnection;

import com.usth.hieplnc.common.xml.XMLParser;
import com.usth.hieplnc.common.xml.model.*;

public class BasicSchema{
// variable

    Connection con = null;
    FileSystem fs = null;
    SchemaModel xml = null;

    private String url = "jdbc:mariadb://kylo-mysql:3306?user=root&password=password";
    private DatabaseConnection virtualDB = null;

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

    public void setVirtualDB(DatabaseConnection dc){
        this.virtualDB = dc;
    }

    public DatabaseConnection getVirtualDB(){
        return virtualDB;
    }

    public void setConnection(String url) throws SQLException{
        this.url = url;
        resetConnection();
    }

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

    public void setFS(FileSystem fs){
        this.fs = fs;
    }

    public void loadXML(String path) throws IOException{
        FSDataInputStream in = null;
        SchemaModel document = null;

        try{
            // load xml file
            in = this.fs.open(new Path(path));
        
            // parse file
            XMLParser xmlParser = new XMLParser(in);
            document = xmlParser.parse(SchemaModel.class);

            // close file
            in.close();
        } catch(NullPointerException e){
            try{
                in.close();
            } catch(NullPointerException npe){}
            throw e;
        }

        this.xml = document;
    }

    public void genDatabase() throws SQLException{
        // prepare state
        DatabaseModel database = xml.getDatabase();
        
        // create database
        virtualDB.createDatabase(database.getName());
        virtualDB.useDatabase(database.getName());

        // create table for database
        for(TableModel table : database.getTable()){
            ArrayList<String> columnNames = new ArrayList<>();
            ArrayList<String> columnTypes = new ArrayList<>();

            for(ColumnModel column : table.getColumn()){
                columnNames.add(column.getName());
                columnTypes.add(column.getType());
            }

            virtualDB.createTable(table.getName(), columnNames, columnTypes);
        }
    }

    public static void main( String[] args ) throws IOException, SQLException{
        BasicSchema bs = new BasicSchema();

        // load schema file
        Configuration conf = new Configuration();
        bs.setFS((new Path("file://")).getFileSystem(conf));
        bs.loadXML("/tmp/hieplnc/hivilake/input/.hivilake/SOF.xml");

        // initialize virtual database
        bs.setVirtualDB(new DatabaseConnection(bs.getConnection()));

        // set argument for outside
        for(int i = 0; i < args.length; i++){
            String[] parameter = args[i].split("=");

            // set parameter
            if(parameter[0].equals("--fileSystem")) bs.setFS((new Path(parameter[1])).getFileSystem(conf));
            else if(parameter[0].equals("--schemaDir")) bs.loadXML(parameter[1]);
            else if(parameter[0].equals("--sqlURL")){
                bs.setConnection(parameter[1]);
                try{
                    bs.getVirtualDB().close();
                } catch(NullPointerException e){}
                bs.setVirtualDB(new DatabaseConnection(bs.getConnection()));
            }

            // execute parameter
            else if(parameter[0].equals("genDatabase")) bs.genDatabase();
            else if(parameter[0].equals("listDatabase")){
                ResultSet listDB = bs.getVirtualDB().listDatabase();
                while(listDB.next()){
                    System.out.println(listDB.getString(1));
                }
            }
            else if(parameter[0].equals("listTableOf")){
                bs.getVirtualDB().useDatabase(parameter[1]);
                ResultSet listTable = bs.getVirtualDB().listTable();
                while(listTable.next()){
                    System.out.println(listTable.getString(1));
                }
            }
            else if(parameter[0].equals("dropDatabase")){
                bs.getVirtualDB().dropDatabase(parameter[1]);
            }
        }

//======================================================================================//
// test
       
        // test xml file
        // System.out.println(bs.xml.getTagName());
        // bs.genDatabase();

        // test mysql connection
        // Connection con = bs.getConnection();
        // DatabaseMetaData listDB = con.getMetaData();
        // ResultSet result = listDB.getCatalogs();
        // while(result.next()){
        //     String nameDB = result.getString(1);
        //     System.out.println(nameDB);
        // }

//=======================================================================================//

        bs.closeConnection();
    }
}
