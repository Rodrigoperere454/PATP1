package controller;

import java.io.*;
import java.net.*;

/**
 * Classe Client
 * Esta classe representa um cliente que se conecta a um servidor.
 * Ela permite enviar e receber mensagens do servidor.
 */
public class Client {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private BufferedReader consoleOutput;

    public Client(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.consoleOutput = new BufferedReader(new InputStreamReader(System.in));
    }

    public String getHost() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }


    public void enviarMensagem(String mensagem) {
        output.println(mensagem);
    }

    public String receberMensagem() throws IOException {
        return input.readLine();
    }

    /**
     * Método para receber uma mensagem do servidor
     * @throws IOException Se ocorrer um erro de entrada/saída
     */
    public void fechar() throws IOException {
        socket.close();
        System.out.println("Conexão encerrada.");
    }
}
