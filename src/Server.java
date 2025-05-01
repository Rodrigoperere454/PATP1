import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.sql.Connection;

import model.*;
import controller.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Server {

    public static String[] decodeMessage(String message) {
        message = message.replaceAll("[<>;]", "");
        message = message.replaceAll(",", " ");
        String[] decoded = message.split(" ");

        return decoded;

    }

    public static void readMessage(String[] decodedMessage) {
        for (int i = 0; i < Array.getLength(decodedMessage); i++) {
            System.out.print("Decoded messsage:");
            System.out.println(Array.get(decodedMessage, i));
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 1234;
        Connection conexao = DBconfig.getConnection();
        DBController query = new DBController(conexao);

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


            String[] decodedMessage = decodeMessage(clientMessage);
            readMessage(decodedMessage);

            while (!decodedMessage[1].equals("hello")) {
                output.println(message_begin);
                clientMessage = input.readLine();
                if (clientMessage == null) {
                    socket.close();
                    return;
                }
                decodedMessage = decodeMessage(clientMessage);
                readMessage(decodedMessage);
            }

            output.println(message_acknowledge);

            Fabricante fabricante = null;

            while ((clientMessage = input.readLine()) != null) {
                System.out.println("Cliente: " + clientMessage);
                decodedMessage = decodeMessage(clientMessage);
                readMessage(decodedMessage);

                if (decodedMessage[1].equals("autenticar")) {
                    System.out.println("Autenticando utilizador...");
                    fabricante = (Fabricante) query.loginUtilizadorFabricanteClient(decodedMessage[2], decodedMessage[3]);
                    if (fabricante != null && fabricante.getType().equals("fabricante")) {
                        output.println("<" + fabricante.getUsername() + "> <autenticar> <success>;");
                    } else {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <autenticar> <fail>;");
                    }
                } else if (decodedMessage[1].equals("bye")) {
                    output.println("<" + InetAddress.getLocalHost().getHostName() + "> <bye>;");
                } else if (decodedMessage[1].equals("info")) {
                    int id_fabricante = query.getIDbyusername(fabricante.getUsername());
                    String[] info = query.buscarDadosFabricanteClient(id_fabricante);
                    output.println("<" + InetAddress.getLocalHost().getHostName() + "> <info> <" + info[0] + "," + info[1] + "," + info[2] + "," + info[3] + "," + info[4] + "," + info[5] + "," + info[6] + "," + info[7] + ">;");
                } else if (decodedMessage[1].equals("update")) {
                    int fabricante_id = query.getIDbyusername(fabricante.getUsername());
                    String encript_pass = query.hashPassword(decodedMessage[4]);
                    boolean u_update_success = query.alterarDadosClient(decodedMessage[2], decodedMessage[3], encript_pass, decodedMessage[5], decodedMessage[6], decodedMessage[7], fabricante_id);
                    if (u_update_success) {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <update> <ok>;");
                    } else {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <update> <fail>;");
                    }

                } else if (decodedMessage[1].equals("inserir") && decodedMessage[2].equals("equipamento")) {
                    int fabricante_id = query.getIDbyusername(fabricante.getUsername());
                    int potencia = 0;
                    int amperagem = 0;
                    int num_model = 0;
                    try {
                        potencia = Integer.parseInt(decodedMessage[6]);
                        amperagem = Integer.parseInt(decodedMessage[7]);
                        num_model = Integer.parseInt(decodedMessage[8]);
                    } catch (NumberFormatException e) {
                        System.out.println("A amperagem, potencia e numero de modelo devem ser um inteiros");
                    }
                    Equipamento equipamento_client = new Equipamento(fabricante_id, decodedMessage[3], decodedMessage[4], decodedMessage[5], potencia, amperagem, num_model);
                    boolean equip_insenrt_success = query.adicionarEquipamentos(equipamento_client);
                    if (equip_insenrt_success) {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <inserir> <equipamento> <ok>;");
                    } else {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <inserir> <equipamento> <fail>;");
                    }

                } else if (decodedMessage[1].equals("pesquisa") && decodedMessage[2].equals("equipamento")) {
                    int fabricante_id = query.getIDbyusername(fabricante.getUsername());
                    String[] pesquisa_equip = query.pesquisarEquipamentosFabricanteClient(decodedMessage[3], fabricante_id);

                    if (pesquisa_equip != null) {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <pesquisa> <equipamento> <" + pesquisa_equip[0] + "," + pesquisa_equip[1] + "," + pesquisa_equip[2] + "," + pesquisa_equip[3] + "," + pesquisa_equip[4] + "," + pesquisa_equip[5] + "," + pesquisa_equip[6] + "," + pesquisa_equip[7] + "," + pesquisa_equip[8] + ">");

                    } else {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <pesquisa> <equipamento> <fail>;");
                    }
                } else if (decodedMessage[1].equals("listar") && decodedMessage[2].equals("equipamento")) {
                    int fabricante_id = query.getIDbyusername(fabricante.getUsername());
                    String equipamento_message = "";
                    String[] listar_equip = query.listarEquipamentosFabricanteClient(fabricante_id);
                    //System.out.println("Listar equipamentos: " + Arrays.toString(listar_equip));

                    for (int i = 0; i < listar_equip.length; i += 9) {
                        String equipamento = listar_equip[i] + "," + listar_equip[i + 1] + "," + listar_equip[i + 2] + "," + listar_equip[i + 3] + "," + listar_equip[i + 4] + "," + listar_equip[i + 5] + "," + listar_equip[i + 6] + "," + listar_equip[i + 7] + "," + listar_equip[i + 8] + ";";
                        //System.out.println("Equipamento: " + listar_equip[i] + " " + listar_equip[i+1] + " " + listar_equip[i+2] + " " + listar_equip[i+3] + " " + listar_equip[i+4] + " " + listar_equip[i+5] + " " + listar_equip[i+6] + " " + listar_equip[i+7] + " " + listar_equip[i+8]);
                        equipamento_message = equipamento_message + equipamento;
                    }

                    if (!equipamento_message.isEmpty()) {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <listar> <equipamento> <" + equipamento_message + ">");
                    } else {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <listar> <equipamento> <fail>;");
                    }

                } else if (decodedMessage[1].equals("listar") && decodedMessage[2].equals("certificacao")) {
                    int fabricante_id = query.getIDbyusername(fabricante.getUsername());
                    String[] listar_certificacao = query.listarCertificacoesFabricanteClient(fabricante_id);
                    System.out.println("Listar certificacoes: " + Arrays.toString(listar_certificacao));
                    if (listar_certificacao != null) {
                        for (int i = 0; i < listar_certificacao.length; i += 10) {
                            String certificacao = listar_certificacao[i] + "," + listar_certificacao[i + 1] + "," + listar_certificacao[i + 2] + "," + listar_certificacao[i + 3] + "," + listar_certificacao[i + 4] + "," + listar_certificacao[i + 5] + "," + listar_certificacao[i + 6] + "," + listar_certificacao[i + 7] + "," + listar_certificacao[i + 8] + "," + listar_certificacao[i + 9];
                            output.println("<" + InetAddress.getLocalHost().getHostName() + "> <listar> <certificacao> <" + certificacao + ">");
                        }
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <listar> <certificacao> <ListFinished>;");
                    } else {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <listar> <certificacao> <fail>;");
                    }

                } else if (decodedMessage[1].equals("pesquisa") && decodedMessage[2].equals("certificacao")) {
                    int fabricante_id = query.getIDbyusername(fabricante.getUsername());
                    String[] pesquisa_Cert = query.pesquisarCertificacaoFabricanteClient(decodedMessage[3], fabricante_id);

                    if (pesquisa_Cert != null) {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <pesquisa> <certificacao> <" + pesquisa_Cert[0] + "," + pesquisa_Cert[1] + "," + pesquisa_Cert[2] + "," + pesquisa_Cert[3] + "," + pesquisa_Cert[4] + ">");
                    } else {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <pesquisa> <certificacao> <fail>;");
                    }

                } else if (decodedMessage[1].equals("ack")) {
                    System.out.println("Cliente: " + clientMessage);
                    output.println("<" + InetAddress.getLocalHost().getHostName() + "> <ack>;");

                }else if (decodedMessage[1].equals("registar")){
                    fabricante = new Fabricante(decodedMessage[3], decodedMessage[4], decodedMessage[5], decodedMessage[6], "fabricante", decodedMessage[7], decodedMessage[8], decodedMessage[9], decodedMessage[10], LocalDate.now());
                    boolean registo_sucesso = query.inserirFabricanteClient(fabricante);
                    if (registo_sucesso) {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <registar> <ok>;");
                    } else {
                        output.println("<" + InetAddress.getLocalHost().getHostName() + "> <registar> <fail>;");
                    }
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
