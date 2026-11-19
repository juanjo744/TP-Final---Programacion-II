package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static String url = "jdbc:mysql://172.16.1.64:3306/2026_C7_JuanJose_SanchezMiller";
    private static String username = "bdd1";
    private static String password = "bdd1";
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
