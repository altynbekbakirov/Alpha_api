package kg.bakirov.alpha.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static Connection instance;

    private DbConnection() {}

    public static Connection getInstance() {
        if (instance == null) {
            try {
                instance = DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=TIGERDB", "sa", "123456789");
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return instance;
    }
}
