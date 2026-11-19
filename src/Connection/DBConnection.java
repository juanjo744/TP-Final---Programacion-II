package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static String url = "jdbc:mysql://localhost:3306/tp7_agregacion";
    private static String username = "root";
    private static String password = "mesasilla3Dorada";
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
