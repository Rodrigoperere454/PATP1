import controller.DBconfig;
import controller.UtilizadorController;

import java.sql.Connection;

public class Main {
    /**
     * Função principal, verifica a conexão com a base de dados e inicia o programa
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {

        Connection connection = null;

        while (connection == null) {
            connection = DBconfig.getConnection();
            Thread.sleep(1000);
            if (connection == null) {
                System.out.println("Se é a sua primeira vez no programa este erro é normal, caso este erro seja persistente contacte o suporte(Professor Marco de PA).");
                DBconfig.configurarBD();
            }
        }

        UtilizadorController runner = new UtilizadorController();
        runner.menuInicial();
    }
}
