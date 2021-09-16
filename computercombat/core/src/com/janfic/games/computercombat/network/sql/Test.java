package com.janfic.games.computercombat.network.sql;

import java.sql.Connection;
import java.sql.DriverManager;
//import com.mysql.jdbc.Driver;

/**
 *
 * @author Jan Fic
 */
public class Test {

    public static void main(String[] args) throws Exception {
        Connection connection = null;
        String url = "jdbc:mysql://computer-combat-db.cloqezbutiub.us-east-1.rds.amazonaws.com:3306";
        String user = "admin";
        String password = "computer-combat-db";
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
