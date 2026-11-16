package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static String url = "jdbc:mysql://ip:puerto/midb";
    private static String username = "use";
    private static String password = "psw";
    private static Connection conn;

    public Connection crearConexion() throws SQLException
    {
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex)
        {
            throw new SQLException("Error al conectar con la base de datos", ex);
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
