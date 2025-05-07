package controller;

import model.Fabricante;
import model.Utilizador;
import model.Log;
import view.AppMenus;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.InputMismatchException;
import java.util.Scanner;


public class UtilizadorController {
    Scanner scanner = new Scanner(System.in);
    Connection conexao = DBconfig.getConnection();
    DBconfig alter_bd = new DBconfig();
    DBController controller = new DBController(conexao);
    AppMenus menus = new AppMenus();
    LocalDateTime data_inicio_aplicacao = LocalDateTime.now();
    DayOfWeek dia_semana = data_inicio_aplicacao.getDayOfWeek();
    private RegexValidations regex = new RegexValidations();


    /**
     * Função utilizada para calcular o tempo de execução durante o uso aplicação
     * @param data_inicio_aplicacao
     * @param dia_semana
     * @param data_fim_aplicacao
     */
    public void calcularExecucao(LocalDateTime data_inicio_aplicacao, DayOfWeek dia_semana, LocalDateTime data_fim_aplicacao) {
        System.out.println("Inicio do Processo: " + dia_semana + "; " + data_inicio_aplicacao + "\n");
        System.out.println("Fim do Processo: " + dia_semana + "; " + data_fim_aplicacao + "\n");

        Duration duracao = Duration.between(data_inicio_aplicacao, data_fim_aplicacao);

        long milissegundos = duracao.toMillis();
        long segundos = duracao.toSecondsPart();
        long minutos = duracao.toMinutesPart();
        long horas = duracao.toHoursPart();

        System.out.println("Tempo de execução: " + milissegundos + " milissegundos" + "( " + segundos + " segundos, " + minutos + " minutos, " + horas + " horas)");
    }


    /**
     * Função utilizada para realizar o registo de um utilizador, onde é pedido o nome, username, password, email e tipo de utilizador.
     * Consoante o tipo de utilizador, é pedido informação adicional, como o NIF, telefone, morada, sector comercial, área de especialização e nível de especialização.
     * @throws Exception
     */
    public void fazerRegistoFabricante() throws Exception {
        System.out.println("=== NOVO FABRICANTE ===");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();
        String hashedPassword = controller.hashPassword(password);

        boolean mailvalido;
        String email;

        do {
            System.out.print("Email: ");
            email = scanner.nextLine();
            mailvalido = regex.validarEmail(email);
            if (!mailvalido) {
                System.out.println("Mail inválido!!!");
            }
        } while (!mailvalido);

        System.out.print("NIF: ");
        String nif = scanner.nextLine();

        boolean telefoneValido;
        String telefone;
        do {
            System.out.print("Telefone: ");
            telefone = scanner.nextLine();
            telefoneValido = regex.validarTelemovel(telefone);
            if (!telefoneValido) {
                System.out.println("Telemovel inválido! Tem de começar em 9, 2 ou 3 e ter 9 digitos!");
            }
        } while (!telefoneValido);

        System.out.print("Morada: ");
        String morada = scanner.nextLine();

        System.out.print("Sector Comercial: ");
        String sector_comercial = scanner.nextLine();

        LocalDate data_inicio = LocalDate.now();

        Fabricante fabricante = new Fabricante(nome, username, hashedPassword, email, "fabricante", nif, telefone, morada, sector_comercial, data_inicio);
        boolean sucesso = controller.inserirFabricanteClient(fabricante);

        if (sucesso) {
            int id_utilizador = controller.getIDbyusername(username);
            Log log = new Log(fabricante.getUsername(), "Fabricante " + id_utilizador + " foi registado na aplicação!");
            System.out.println("\033[32mFabricante inserido com sucesso!\033[0m");
        } else {
            System.out.println("\033[31mErro ao inserir Fabricante.\033[0m");
        }
    }


    /**
     * Função utilizada para mostrar o menu inicial da aplicação, onde o utilizador pode fazer login, registo ou sair da aplicação.
     * @throws Exception
     */
    public void menuInicial() throws Exception {

        int opcao = -1;
        do {
            menus.menuInicial();

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        //fazerLogin(controller);
                        System.out.println("NÃO É POSSÍVEL FAZER LOGIN NESTA VERSÃO!");
                        break;
                    case 2:
                        fazerRegistoFabricante();
                        break;
                    case 3:
                        alter_bd.configurarBD();
                        break;
                    case 4:
                        System.out.print("INDICA A PORTA A UTILIZAR: ");
                        int porta = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("INDICA O HOST QUE DESEJA UTILIZAR: ");
                        String host = scanner.nextLine();
                        Client client = new Client(host, porta);
                        System.out.println("Servidor: " + client.receberMensagem());
                        System.out.print("================RESPONDER SERVIDOR==================\n");
                        System.out.println("1-SIM");
                        System.out.println("2-NÃO");
                        int resposta = scanner.nextInt();
                        scanner.nextLine();
                        if (resposta == 1) {
                            while (true) {
                                String serverResponse;
                                System.out.println("DIGITE A PALAVRA 'hello' PARA INICIAR A COMUNICAÇÃO COM O SERVIDOR.");
                                String hello = scanner.nextLine();
                                String helloMessage = "<" + client.getHost() + "> <" + hello + ">;";
                                client.enviarMensagem(helloMessage);
                                serverResponse = client.receberMensagem();
                                System.out.println("Servidor: " + serverResponse);

                                if (serverResponse.contains("<ack>")) {
                                    break;
                                } else {
                                    System.out.println("Servidor não reconheceu o hello. Tentando novamente...");
                                    //client.enviarMensagem(helloMessage);
                                }
                            }
                        } else {
                            System.out.println("A sair...");
                            System.exit(0);
                        }

                        while(true){
                            if(!client.isConnected()){
                                break;
                            }
                            menus.clientMenu();
                            int opcao_client = scanner.nextInt();
                            scanner.nextLine();
                            switch (opcao_client) {
                                case 1:
                                    System.out.print("Username: ");
                                    String username = scanner.nextLine();
                                    System.out.print("Password: ");
                                    String password = scanner.nextLine();
                                    client.enviarMensagem("<" + client.getHost() + "> " + "<autenticar> " +"<" + username + "," + password + ">;");
                                    String resposta_server = client.receberMensagem();
                                    System.out.println("Servidor: " + resposta_server);
                                    if(resposta_server.contains("<success>")) {
                                        Utilizador utilizador = controller.loginUtilizador(username, password);
                                        int opcao_fabricante = -1;
                                        do{
                                            menus.menuFabricanteCliente();
                                            opcao_fabricante = scanner.nextInt();
                                            scanner.nextLine();
                                            switch (opcao_fabricante) {
                                                case 1:
                                                    System.out.println("=================NOVO FABRICANTE=================");
                                                    System.out.print("Nome: ");
                                                    String nome_fabricante = scanner.nextLine();
                                                    System.out.print("Username: ");
                                                    String username_fabricante = scanner.nextLine();
                                                    System.out.print("Password: ");
                                                    String password_fabricante = scanner.nextLine();
                                                    String hashedPassword_fabricante = controller.hashPassword(password_fabricante);
                                                    System.out.print("Email: ");
                                                    String email_fabricante = scanner.nextLine();
                                                    System.out.print("NIF: ");
                                                    String nif_fabricante = scanner.nextLine();
                                                    System.out.print("Telefone: ");
                                                    String telefone_fabricante = scanner.nextLine();
                                                    System.out.print("Morada: ");
                                                    String morada_fabricante = scanner.nextLine();
                                                    System.out.print("Sector Comercial: ");
                                                    String sector_comercial_fabricante = scanner.nextLine();

                                                    if(nome_fabricante.isEmpty() || username_fabricante.isEmpty() || password_fabricante.isEmpty() || email_fabricante.isEmpty() || nif_fabricante.isEmpty() || telefone_fabricante.isEmpty() || morada_fabricante.isEmpty() || sector_comercial_fabricante.isEmpty()){
                                                        System.out.println("Preencha todos os campos!");
                                                        break;
                                                    }

                                                    if(!regex.validarTelemovel(telefone_fabricante)){
                                                        System.out.println("Telemovel inválido! Tem de começar em 9, 2 ou 3 e ter 9 digitos!");
                                                        break;
                                                    } else if (!regex.validarEmail(email_fabricante)){
                                                        System.out.println("Email inválido!");
                                                        break;
                                                    }
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<registar> " + "<fabricante> " + "<" + nome_fabricante + "," + username_fabricante + "," + hashedPassword_fabricante + "," + email_fabricante + "," + nif_fabricante + "," + telefone_fabricante + "," + morada_fabricante + "," + sector_comercial_fabricante + ">;");
                                                    String resposta_registo_fabricante = client.receberMensagem();
                                                    System.out.println("Servidor: " + resposta_registo_fabricante);

                                                    break;
                                                case 2:
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<info>;");
                                                    String info = client.receberMensagem();
                                                    System.out.println("Servidor: " + info);
                                                    break;
                                                case 3:
                                                    System.out.println("PREENCHA OS SEGUINTES CAMPOS PARA ALTERAR OS SEUS DADOS");
                                                    System.out.print("Nome: ");
                                                    String nome = scanner.nextLine();
                                                    System.out.print("Email: ");
                                                    String email = scanner.nextLine();
                                                    System.out.print("Password: ");
                                                    String password_alterar = scanner.nextLine();
                                                    System.out.print("Telefone: ");
                                                    String telefone = scanner.nextLine();
                                                    System.out.print("Morada: ");
                                                    String morada = scanner.nextLine();
                                                    System.out.print("NIF: ");
                                                    String nif = scanner.nextLine();
                                                    if(nome.isEmpty() || email.isEmpty() || password_alterar.isEmpty() || telefone.isEmpty() || morada.isEmpty() || nif.isEmpty() ){
                                                        System.out.println("Preencha todos os campos!");
                                                        break;
                                                    }
                                                    if(!regex.validarTelemovel(telefone)){
                                                        System.out.println("Telemovel inválido! Tem de começar em 9, 2 ou 3 e ter 9 digitos!");
                                                        break;
                                                    } else if (!regex.validarEmail(email)){
                                                        System.out.println("Email inválido!");
                                                        break;
                                                    }
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<update> " + "<" + nome + "," + email + "," + password_alterar + "," + telefone + "," + morada + "," + nif + ">;");
                                                    String resposta_update = client.receberMensagem();
                                                    System.out.println("Servidor: " + resposta_update);
                                                    if(resposta_update.contains("<ok>")){
                                                        client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<ack>;");
                                                        String resposta_ack = client.receberMensagem();
                                                        System.out.println("Servidor: " + resposta_ack);
                                                    } else {
                                                        System.out.println("Erro ao alterar dados!");
                                                    }
                                                    break;
                                                case 4:
                                                    System.out.println("==================ADICIONAR UM EQUIPAMENTO==================");
                                                    System.out.print("Marca:");
                                                    String marca = scanner.nextLine();
                                                    System.out.print("Modelo:");
                                                    String modelo = scanner.nextLine();
                                                    System.out.print("Setor Comercial:");
                                                    String sector_comercial = scanner.nextLine();
                                                    System.out.print("Potência:");
                                                    int potencia = scanner.nextInt();
                                                    System.out.print("Amperagem:");
                                                    int amaperagem = scanner.nextInt();
                                                    System.out.print("Número de Modelo:");
                                                    int numero_modelo = scanner.nextInt();

                                                    if(marca.isEmpty() || modelo.isEmpty() || sector_comercial.isEmpty() || potencia <= 0 || amaperagem <= 0 || numero_modelo <= 0) {
                                                        System.out.println("Preencha todos os campos!");
                                                        break;
                                                    }
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<inserir> " + "<equipamento> " + "<" + marca + "," + modelo + "," + sector_comercial + "," + potencia + "," + amaperagem + "," + numero_modelo + ">");
                                                    String inserir_equip_resposta = client.receberMensagem();
                                                    System.out.println("Servidor: " + inserir_equip_resposta);
                                                    if(inserir_equip_resposta.contains("<ok>")){
                                                        client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<ack>;");
                                                        String resposta_ack = client.receberMensagem();
                                                        System.out.println("Servidor: " + resposta_ack);
                                                    } else {
                                                        System.out.println("Erro ao adicionar equipamento!");
                                                    }
                                                    break;
                                                case 5:
                                                    System.out.println("DIGITE O CÓDIGO_SKU DO EQUIPAMENTO: ");
                                                    String sku_equipamento = scanner.nextLine();
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<pesquisa> " + "<equipamento> " + "<" + sku_equipamento + ">;");
                                                    String resposta_equipamento = client.receberMensagem();
                                                    System.out.println("Servidor: " + resposta_equipamento);
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<ack>;");
                                                    String resposta_ack_equipamento = client.receberMensagem();
                                                    System.out.println("Servidor: " + resposta_ack_equipamento);
                                                    break;

                                                case 6:
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<listar> <equipamento>");
                                                    System.out.println("==========LISTA DOS MEUS EQUIPAMENTOS==========");
                                                    String listar_equipamento = client.receberMensagem();
                                                    System.out.println("Servidor: " + listar_equipamento);
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<ack>;");
                                                    String resposta_ack_listar = client.receberMensagem();
                                                    System.out.println("Servidor: " + resposta_ack_listar);
                                                    break;

                                                case 7:
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<listar> <certificacao>");
                                                    System.out.println("==========LISTA DOS MEUS PEDIDOS DE CERTIFICAÇÃO==========");
                                                    System.out.println("Formato : <id_certificacao, id_equipamento, id_tecnico, id_fabricante, data_realizacao, tempo_decorrido, custo, estado, numero_certificacao, numero_licenca>");
                                                    String listar_certificacao = "";
                                                    while(!listar_certificacao.contains("<ListFinished>")){
                                                        listar_certificacao = client.receberMensagem();
                                                        System.out.println("Servidor: " + listar_certificacao);
                                                    }
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<ack>;");
                                                    String resposta_ack_listar_certificacao = client.receberMensagem();
                                                    System.out.println("Servidor: " + resposta_ack_listar_certificacao);
                                                    break;
                                                case 8:
                                                    System.out.println("DIGITE O NÚMERO DA CERTIFICAÇÃO: ");
                                                    int numero_certificacao = scanner.nextInt();
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<pesquisa> <certificacao> " + "<" + numero_certificacao + ">;");
                                                    System.out.println("==========INFO DA CERTIFICAÇÃO==========");
                                                    String resposta_certificacao = client.receberMensagem();
                                                    System.out.println("Servidor: " + resposta_certificacao);
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<ack>;");
                                                    String resposta_ack_certificacao = client.receberMensagem();
                                                    System.out.println("Servidor: " + resposta_ack_certificacao);

                                                    break;
                                                case 0:
                                                    System.out.println("A sair...");
                                                    client.enviarMensagem("<" + utilizador.getUsername() + "> " + "<bye>;");
                                                    String resposta_bye = client.receberMensagem();
                                                    System.out.println("Servidor: " + resposta_bye);
                                                    client.fechar();
                                                    break;
                                            }

                                        }while(opcao_fabricante != 0);
                                    } else {
                                        System.out.println("Erro ao autenticar utilizador.");
                                        break;
                                    }

                                case 2:
                                    client.fechar();
                                    System.exit(0);
                                    break;
                                default:
                                    System.out.println("Opção inválida!");
                            }
                        }
                    case 0:
                        LocalDateTime data_fim_aplicacao = LocalDateTime.now();
                        System.out.println("A sair...");
                        Log log = new Log( "Sistema", "Utilizador saiu da aplicação!");
                        calcularExecucao(data_inicio_aplicacao, dia_semana, data_fim_aplicacao);
                        System.exit(0);
                        break;
                    default:
                        System.out.println("\033[31mOpção inválida, selecione uma nova opção!\033[0m\n");
                }

            } catch (InputMismatchException e) {
                System.out.println("\033[31mOpção inválida, insira um número válido!\033[0m\n");
                scanner.nextLine();
            }
        } while (opcao != 0);
        scanner.close();
    }

    }

