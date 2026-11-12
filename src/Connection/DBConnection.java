package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private String url;
    private String username;
    private String password;
    private static Connection conn;

    public Connection crearConexion()
    {
        url = "jdbc:mysql://ip:puerto/midb";
        username = "use";
        password = "psw";

        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex)
        {
            System.out.printf(ex.getMessage());
        }

        return conn;
    }

    public void cerrarConexion()
    {
        try {
            if(conn != null)
                conn.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
