package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connect {
    Connection con;
    public Connection getConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase","root","");
            System.out.println("Connected");

    }
        catch (ClassNotFoundException | SQLException e){
            System.out.println(e);
        } return con;

        }

}
