import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.sql.Connection;

import model.*;
import controller.*;

public class Server {
    Connection conexao = DBconfig.getConnection();
    DBController query = new DBController(conexao);

    public String[] decodeMessage(String message) {
        message = message.replaceAll("[<>;]", "");
        message = message.replaceAll(",", " ");
        String[] decoded = message.split(" ");

        return decoded;

    }

    public void readMessage(String[] decodedMessage) {
        for (int i = 0; i < Array.getLength(decodedMessage); i++) {
            System.out.print("Decoded messsage:");
            System.out.println(Array.get(decodedMessage, i));
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 1234;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor aguardando conexão na porta " + port + "...");

            Socket socket = serverSocket.accept();
            InetAddress clientAddress = socket.getInetAddress();
            String message_begin = "<" + InetAddress.getLocalHost().getHostName() + "> " + "<hello>;";
            String message_acknowledge = "<" + InetAddress.getLocalHost().getHostName() + "> " + "<ack>;";

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Cliente conectado!");
            output.println(message_begin);


            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                System.out.println("Cliente: " + clientMessage);
                Server server = new Server();
                String[] decodedMessage = server.decodeMessage(clientMessage);
                server.readMessage(decodedMessage);

                while (!decodedMessage[1].equals("hello")) {
                    output.println(message_begin);
                    clientMessage = input.readLine();
                    if (clientMessage == null) break;
                    decodedMessage = server.decodeMessage(clientMessage);
                    server.readMessage(decodedMessage);
                }

                output.println(message_acknowledge);

                if (decodedMessage[1].equals("autenticar")) {
                    Utilizador utilizador = server.query.loginUtilizador(decodedMessage[2], decodedMessage[3]);
                    if (utilizador != null) {
                        output.println("<" + utilizador.getUsername() + "> <autenticar> <success>;");
                    } else {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <autenticar> <fail>;");
                    }
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
