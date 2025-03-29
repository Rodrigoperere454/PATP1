package controller;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class DBconfig {
    private static final String iniPath = "src/config_database.ini";
    private static final Properties properties = new Properties();
    static Scanner scanner = new Scanner(System.in);

    /**
     * Função para realizar a conexão com a base de dados através do ficheiro de configuração ini.
     * verifica se o ficheiro ini está completo, caso contrário pede ao utilizador para preencher os dados.
     * @return conexão com a base de dados
     */
    public static Connection getConnection() {
        try {
            try (FileInputStream fis = new FileInputStream(iniPath)) {
                properties.load(fis);
            }

            String host = properties.getProperty("host");
            String port = properties.getProperty("port");
            String dbname = properties.getProperty("dbname");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");


            Connection conn = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + dbname, user, password);

            if (conn != null) {
                return conn;
            } else {
                System.out.println("Falha na conexão com base de dados.");
                //configurarBD();
            }
            return null;

        } catch (IOException | SQLException e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
            return null;
        }

    }

    /**
     * Função para configurar a base de dados, pede ao utilizador para preencher os dados da base de dados.
     */
    public static void configurarBD() {
            System.out.println("CONFIGURAÇÃO BASE DE DADOS");
            System.out.print("Host: ");
            String host = scanner.nextLine();
            System.out.print("Port: ");
            String port = scanner.nextLine();
            System.out.print("dbname: ");
            String dbname = scanner.nextLine();
            System.out.print("User: ");
            String user = scanner.nextLine();
            System.out.print("password: ");
            String password = scanner.nextLine();

            alterarDadosBDini(dbname, user, password, host, port);

            System.out.println("Configuração guardada com sucesso!");

    }

    /**
     * Função para alterar os dados da base de dados no ficheiro ini.
     * @param dataBase
     * @param user
     * @param password
     * @param host
     * @param port
     */
    public static void alterarDadosBDini(String dataBase, String user, String password, String host, String port) {
        try (FileOutputStream fos = new FileOutputStream(iniPath)) {
            properties.setProperty("dbname", dataBase);
            properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("host", host);
            properties.setProperty("port", port);
            properties.store(fos, null);
        } catch (IOException e) {
            System.err.println("Erro ao guardar dados: " + e.getMessage());
        }
    }
}
