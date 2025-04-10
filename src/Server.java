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

            String clientMessage = input.readLine();
            if (clientMessage == null) {
                socket.close();
                return;
            }

            Server server = new Server();
            String[] decodedMessage = server.decodeMessage(clientMessage);
            server.readMessage(decodedMessage);

            while (!decodedMessage[1].equals("hello")) {
                output.println(message_begin);
                clientMessage = input.readLine();
                if (clientMessage == null) {
                    socket.close();
                    return;
                }
                decodedMessage = server.decodeMessage(clientMessage);
                server.readMessage(decodedMessage);
            }

            output.println(message_acknowledge);

            Fabricante fabricante = null;

            while ((clientMessage = input.readLine()) != null){
                System.out.println("Cliente: " + clientMessage);
                decodedMessage = server.decodeMessage(clientMessage);
                server.readMessage(decodedMessage);

                if (decodedMessage[1].equals("autenticar")){
                    System.out.println("Autenticando utilizador...");
                    fabricante = (Fabricante) server.query.loginUtilizadorFabricanteClient(decodedMessage[2], decodedMessage[3]);
                    if (fabricante != null && fabricante.getType().equals("fabricante")){
                        output.println("<" + fabricante.getUsername() + "> <autenticar> <success>;");
                    } else {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <autenticar> <fail>;");
                    }
                } else if (decodedMessage[1].equals("bye")){
                    output.println("<" + InetAddress.getLocalHost().getHostName() + "> <bye>;");
                } else if (decodedMessage[1].equals("info")){
                    output.println("<" + InetAddress.getLocalHost().getHostName() + "> <info> <" + fabricante.getUsername() + "," + fabricante.getPassword() + "," + fabricante.getName() + "," + fabricante.getEmail() + "," + fabricante.getNif() + "," + fabricante.getTelefone() + "," + fabricante.getMorada() +
                            ">");

                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
