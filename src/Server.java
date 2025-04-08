import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = 1234;
        String message_begin = "<username> <hello>;";

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor aguardando conexão na porta " + port + "...");

            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado!");



            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));


            String clientMessage;
            while ((clientMessage = input.readLine()) != null){
                System.out.println("Cliente: " + clientMessage);

                if (clientMessage.equals("sair")) {
                    System.out.println("Cliente desconectou.");
                    break;
                }else if(clientMessage.equals("menu")) {
                    String response = "1. Opção 1 2. Opção 2\n3. Opção 3\nDigite sua opção: ";
                    output.println(response);
                    break;
                }


                System.out.print("Servidor: ");
                String response = consoleInput.readLine();
                output.println(response);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
