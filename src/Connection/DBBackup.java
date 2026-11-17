package Connection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DBBackup {
    private String ejecutable = "C:\\Users\\Juan\\Documents";
    private String mibd = "mibd";
    private Path carpeta = Path.of("Backups").toAbsolutePath();

    public String crearBackup() throws IOException, InterruptedException {
        Files.createDirectories(carpeta);

        String fecha = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        Path archivo = carpeta.resolve("backup_"+fecha+".sql");

        ProcessBuilder constructor = new ProcessBuilder(
                ejecutable,
                "--login-path=hotel",
                "--single-transaction",
                "--result-file=" + archivo,
                mibd
        );

        constructor.inheritIO();

        try (Process pr = constructor.start()) {
            int codigo = pr.waitFor();

            if (codigo != 0) {
                Files.deleteIfExists(archivo);
                throw new IOException("No se pudo crear el backup. Código " + codigo + ".");
            }
        }


        return "Backup creado en " + archivo + ".";
    }
}