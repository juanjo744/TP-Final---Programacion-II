package Connection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DBBackup {
    private String ejecutable = "C:\\\\Program Files\\\\MySQL\\\\MySQL Server 8.0\\\\bin\\\\mysqldump.exe";
    private String mibd = "2026_C7_JuanJose_SanchezMiller";
    private int numero = 0;
    private Path carpeta = Path.of("Backups").toAbsolutePath();

    public String crearBackup() throws IOException, InterruptedException {
        Files.createDirectories(carpeta);

        String fecha = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        Path archivo = carpeta.resolve("backup_" + fecha + "-" + numero + ".sql");
        numero++;

        ProcessBuilder constructor = new ProcessBuilder(ejecutable, "--login-path=hotel", "--single-transaction", "--result-file=" + archivo, mibd);

        constructor.inheritIO();

        Process pr = constructor.start();

        try {
            int codigo = pr.waitFor();

            if (codigo != 0) {
                Files.deleteIfExists(archivo);
                throw new IOException("No se pudo crear el backup. Código " + codigo + ".");
            }
        } catch (InterruptedException ex)
        {
            System.out.println("Error al crear el backup." + ex.getMessage());
        }
        finally {
            pr.destroy();
        }

        return "Backup creado en " + archivo + ".";
    }
}

/// ricota