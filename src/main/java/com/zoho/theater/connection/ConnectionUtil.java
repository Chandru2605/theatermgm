package com.zoho.theater.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionUtil {
    static String url = "jdbc:mysql://localhost:3306/theater";
    static String username = "root";
    static String pass = "Chan@2605";
    public static Connection con;
    public static ResultSet selectQuery(String query) throws Exception{
        con = DriverManager.getConnection(url,username,pass);
        Statement statement = con.createStatement();
        return statement.executeQuery(query);

    }
    public static int insertQuery(String query) throws Exception{
        con = DriverManager.getConnection(url,username,pass);
        Statement st = con.createStatement();
        return st.executeUpdate(query);
    }
}
