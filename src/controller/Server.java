package controller;

import java.io.*;
import java.net.*;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor aguardando conexão na porta " + port + "...");
    }

    public void esperarCliente() throws IOException {
        socket = serverSocket.accept();
        System.out.println("Cliente conectado!");

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    public void iniciarComunicacao() throws IOException {
        String clientMessage;

        while ((clientMessage = input.readLine()) != null) {
            System.out.println("Cliente: " + clientMessage);

            if (clientMessage.equals("sair")) {
                System.out.println("Cliente desconectou.");
                break;
            }

            if (clientMessage.startsWith("<") && clientMessage.endsWith(">;")) {
                String conteudo = clientMessage.substring(1, clientMessage.length() - 2); // remove < e >;
                String[] partes = conteudo.split(" ");

                if (partes.length == 2 && partes[1].equalsIgnoreCase("hello")) {
                    output.println("<Olá, " + partes[0] + ">;");
                } else if (conteudo.equalsIgnoreCase("menu")) {
                    output.println("<1. Opção 1 2. Opção 2 3. Opção 3>;");
                } else {
                    output.println("<Comando não reconhecido>;");
                }
            } else {
                output.println("<Formato inválido>; Ex: <usuario hello>;");
            }
        }
    }

    public void fechar() throws IOException {
        if (socket != null) socket.close();
        if (serverSocket != null) serverSocket.close();
        System.out.println("Conexão encerrada.");
    }
}

