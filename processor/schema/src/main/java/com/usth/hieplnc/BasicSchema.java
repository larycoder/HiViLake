package com.usth.hieplnc;

import java.sql.*;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.SAXException;

import com.usth.hieplnc.DatabaseConnection;

public class BasicSchema{
// variable

    Connection con = null;
    FileSystem fs = null;
    Element xml = null;

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

    public void loadXML(String path) throws IOException, ParserConfigurationException, SAXException{
        FSDataInputStream in = null;
        Document document = null;

        try{
            // load xml file
            in = this.fs.open(new Path(path));
        
            // get document builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            // parse file
            document = builder.parse(in);

            // close file
            in.close();
        } catch(ParserConfigurationException | SAXException e){
            try{
                in.close();
            } catch(NullPointerException npe){}
            throw e;
        }

        // normalize tree
        document.getDocumentElement().normalize();

        // get root file
        this.xml = document.getDocumentElement();
    }

    public void genDatabase() throws SQLException{
        // prepare state
        Element database = (Element) xml.getElementsByTagName("database").item(0);
        
        // create database
        String nameDB = database.getElementsByTagName("name").item(0).getTextContent();
        virtualDB.createDatabase(nameDB);
        virtualDB.useDatabase(nameDB);

        // create table for database
        NodeList listTables = ((Element) database.getElementsByTagName("tables").item(0)).getElementsByTagName("table");
        for(int i = 0; i < listTables.getLength(); i++){
            Element table = (Element) listTables.item(i);
            String tableName = table.getElementsByTagName("name").item(0).getTextContent();
            
            NodeList listColumns = table.getElementsByTagName("column");
            ArrayList<String> columnNames = new ArrayList<String>();
            ArrayList<String> columnTypes = new ArrayList<String>();

            for(int j = 0; j < listColumns.getLength(); j++){
                Element column = (Element) listColumns.item(j);
                columnNames.add(column.getElementsByTagName("name").item(0).getTextContent());
                columnTypes.add(column.getElementsByTagName("type").item(0).getTextContent());
            }

            virtualDB.createTable(tableName, columnNames, columnTypes);
        }
    }

    public static void main( String[] args ) throws IOException, SQLException, ParserConfigurationException, SAXException{
        BasicSchema bs = new BasicSchema();

        // load schema file
        Configuration conf = new Configuration();
        bs.setFS((new Path("file://")).getFileSystem(conf));
        bs.loadXML("/tmp/hieplnc/hivilake/input/.hivilake/SOF.xml");

        // initialize virtual database
        bs.setVirtualDB(new DatabaseConnection(bs.getConnection()));

        // gen database following schema
        bs.genDatabase();

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
