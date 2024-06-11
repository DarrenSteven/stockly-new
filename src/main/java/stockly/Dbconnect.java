package stockly;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Dbconnect {
    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DB_NAME = "stockly";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection getConnect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DB_NAME),
                    USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static ResultSet getData(String query) {
        ResultSet resultSet = null;
        try {
            Connection connection = getConnect();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public static void executeUpdate(String query) {
        try {
            Connection connection = getConnect();
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

}
