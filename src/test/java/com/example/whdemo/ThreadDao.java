package com.example.whdemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ThreadDao {
    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();
    public Connection initialValue() throws SQLException {
        return DriverManager.getConnection("DB_URL");
    }

    public static Connection getConnection(){
        return connectionHolder.get();
    }
}
