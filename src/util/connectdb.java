package util;
import java.sql.*;

public class connectdb {
    public static Connection getConnection() throws Exception {
        String URL = "jdbc:mysql://localhost:3306/TiemNetDB?useSSL=false&serverTimezone=UTC";
        String USER = "root";
        String PASSWORD = "12345678";

        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connected MySQL successfully!");
        
        return conn;
    }
}