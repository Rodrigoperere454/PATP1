package controller;

import model.Fabricante;
import model.Tecnico;
import model.Utilizador;
import model.Equipamento;
import model.Certificacao;
import model.Teste;
import model.Log;
import view.AppMenus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class UtilizadorController {
    Scanner scanner = new Scanner(System.in);
    Connection conexao = DBconfig.getConnection();
    DBconfig alter_bd = new DBconfig();
    DBController controller = new DBController(conexao);
    AppMenus menus = new AppMenus();
    LocalDateTime data_inicio_aplicacao = LocalDateTime.now();
    DayOfWeek dia_semana = data_inicio_aplicacao.getDayOfWeek();
    private RegexValidations regex = new RegexValidations();


    final static Logger logger = Logger.getLogger(UtilizadorController.class.getName());
    private static FileHandler lf = null;

    public void logs() {
        try {
            lf = new FileHandler("appLogs.txt", true);
            lf.setFormatter(new SimpleFormatter());
            logger.addHandler(lf);
            logger.setLevel(Level.INFO);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
     * Função utilizada para realizar a auntenticação do utilizador que usa uma query para verificar se
     * existe um utilizador na bd com o username e password inserido, se existir cria um objeto de utilizador, se esse objeto for devolvido então
     * o login teve sucesso e avança para o próximo menu.
     * @param controller
     * @throws Exception
     */
    public void fazerLogin(DBController controller) throws Exception {

        System.out.println("=== LOGIN UTILIZADOR ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();


        Utilizador utilizador = controller.loginUtilizador(username, password);
        String type = null;
        if (utilizador != null) {
            int id_user = controller.getIDbyusername(username);
            logger.log(Level.INFO, "Utilizador " + utilizador.getName() + " fez login com sucesso!");
            Log log = new Log(utilizador.getUsername(), "Utilizador " + id_user + " fez login com sucesso!");
            controller.enviarLog(log);
            System.out.println("\033[32mSUCESSO! Bem-vindo, \033[0m" + utilizador.getName() + "(" + utilizador.getType() + ")");
            System.out.println("ID: " + id_user);

            menuUtilizador(utilizador);
        } else {
            System.out.println("\033[31mFalha no login. Verifique suas credenciais.\033[0m");
            fazerLogin(controller);

        }
    }

    /**
     * Função utilizada para realizar o registo de um utilizador, onde é pedido o nome, username, password, email e tipo de utilizador.
     * Consoante o tipo de utilizador, é pedido informação adicional, como o NIF, telefone, morada, sector comercial, área de especialização e nível de especialização.
     * @param tipo
     * @throws Exception
     */
    public void fazerRegisto(String tipo) throws Exception {
        System.out.println("=== NOVO UTILIZADOR ===");
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
                logger.log(Level.WARNING, "Email inválido!");
                System.out.println("Mail inválido!!!");
            }
        } while (!mailvalido);


        if (tipo.equals("gestor")) {
            Utilizador utilizador;
            utilizador = new Utilizador(nome, username, hashedPassword, email, tipo);
            boolean sucesso = controller.inserirUtilizador(utilizador);
            if (sucesso) {
                int id_utilizador = controller.getIDbyusername(username);
                controller.enviarNotificacao(id_utilizador, "Pedido de Registo de Conta", "gestor", "Gestores");
                logger.log(Level.INFO, "Gestor" + id_utilizador + " inserido com sucesso!");
                Log log = new Log(utilizador.getUsername(), "Gestor " + id_utilizador + " foi registado na aplicação!");
                controller.enviarLog(log);
                System.out.println("\033[32mGestor inserido com sucesso!\033[0m");
            } else {
                logger.log(Level.SEVERE, "Erro ao tentar inserir um utilizador");
                System.out.println("\033[31mErro ao inserir Gestor.\033[0m");
            }

        } else if (tipo.equals("fabricante")) {
            System.out.print("NIF: ");
            String nif = scanner.nextLine();
            boolean telefoneValido;
            String telefone;
            do {
                System.out.print("Telefone: ");
                telefone = scanner.nextLine();
                telefoneValido = regex.validarTelemovel(telefone);
                if (!telefoneValido) {
                    logger.log(Level.WARNING, "Número de telemóvel inválido");
                    System.out.println("Telemovel inválido! Tem de começar em 9, 2 ou 3 e ter 9 digitos!");
                }
            } while (!telefoneValido);

            System.out.print("Morada: ");
            String morada = scanner.nextLine();
            System.out.print("Sector Comercial: ");
            String sector_comercial = scanner.nextLine();
            LocalDate data_inicio = LocalDate.now();


            Fabricante fabricante = new Fabricante(nome, username, hashedPassword, email, tipo, nif, telefone, morada, sector_comercial, data_inicio);
            boolean sucesso = controller.inserirFabricante(fabricante);
            if (sucesso) {
                int id_utilizador = controller.getIDbyusername(username);
                controller.enviarNotificacao(id_utilizador, "Pedido de Registo de Conta", "fabricante", "Gestores");
                logger.log(Level.INFO, "Fabricante " + id_utilizador + " foi inserido com sucesso!!");
                Log log = new Log(fabricante.getUsername(), "Fabrincante " + id_utilizador + " foi registado na aplicação!");
                controller.enviarLog(log);
                System.out.println("\033[32mFabricante inserido com sucesso!\033[0m");
            } else {
                logger.log(Level.SEVERE, "Erro ao tentar inserir um fabricante");
                System.out.println("\033[31mErro ao inserir Fabricante.\033[0m");
            }

        } else if (tipo.equals("tecnico")) {
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
            System.out.print("Área de Especialização: ");
            String area_especializacao = scanner.nextLine();
            System.out.print("Nível de Especialização: ");
            int nivel_especializacao = scanner.nextInt();
            Tecnico tecnico = new Tecnico(nome, username, hashedPassword, email, tipo, nif, telefone, morada, area_especializacao, nivel_especializacao);
            boolean sucesso = controller.inserirTecnico(tecnico);
            int id_utilizador = controller.getIDbyusername(username);

            if (sucesso) {
                System.out.println("\033[32mTécnico inserido com sucesso!\033[0m");
                Log log = new Log(tecnico.getUsername(), "Técnico " + id_utilizador + " foi registado na aplicação!");
                controller.enviarLog(log);
                controller.enviarNotificacao(id_utilizador, "Pedido de Registo de Conta", "tecnico1", "Gestores");
            } else {
                System.out.println("\033[31mErro ao inserir Técnico.\033[0m");
            }
        } else {
            System.out.println("Tipo de utilizador inválido!");
        }
    }

    /**
     * Função utilizada para mostrar o menu inicial da aplicação, onde o utilizador pode fazer login, registo ou sair da aplicação.
     * @throws Exception
     */
    public void menuInicial() throws Exception {
        boolean noGestores = controller.verificar_gestores();
        if (noGestores) {
            fazerRegisto("gestor");
        }

        int opcao = -1;
        do {
            menus.menuInicial();

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        fazerLogin(controller);
                        break;
                    case 2:
                        String tipo = null;
                        menus.tipoMenu();

                        int tipoUtilizador = scanner.nextInt();
                        scanner.nextLine();
                        switch (tipoUtilizador) {
                            case 1:
                                tipo = "fabricante";
                                break;
                            case 2:
                                tipo = "tecnico";
                                break;
                            case 0:
                                System.out.println("A sair...");
                                System.exit(0);
                                break;
                            default:
                                System.out.println("\033[31mTipo de utilizador inválido!\033[0m\n");
                        }

                        if (tipo != null) {
                            fazerRegisto(tipo);
                        }
                        break;
                    case 3:
                        alter_bd.configurarBD();
                        break;
                    case 0:
                        LocalDateTime data_fim_aplicacao = LocalDateTime.now();
                        System.out.println("A sair...");
                        Log log = new Log( "Sistema", "Utilizador saiu da aplicação!");
                        controller.enviarLog(log);
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

    /**
     * Função utilizada para mostrar o menu do utilizador consoante o tipo de utilizador que fez login.
     * É utilizado um switch case para mostrar o menu consoante o tipo de utilizador, onde cada opção do menu executa uma função diferente.
     *
     * @param utilizador
     * @throws Exception
     */
    public void menuUtilizador(Utilizador utilizador) throws Exception {
        int opcao = -1;

        if (utilizador.getType().equals("gestor")) {
            do {
                menus.menuGestor();
                try {
                    opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1:
                            fazerRegisto("gestor");
                            break;
                        case 2:
                            controller.listarUtilizadores();
                            break;
                        case 3:
                            System.out.println("DIGITE O NOME/USERNAME/TIPO DO UTILIZADOR QUE DESEJA: ");
                            String nome_pesquisar = scanner.nextLine();
                            controller.pesquisarUtilizadores(nome_pesquisar);
                            break;
                        case 4:
                            controller.listarNotificacoes("Gestores");
                            break;
                        case 5:
                            controller.listarCertificacaoesData();
                            System.out.println("FILTROS: 1- POR NÚMERO, 2- POR DATA, 3- NÃO FINALIZADAS");
                            int opcao_listar = scanner.nextInt();

                            if (opcao_listar == 1) {
                                controller.listarCertificacaoesNumero();
                            } else if (opcao_listar == 2) {
                                controller.listarCertificacaoesData();
                            } else if (opcao_listar == 3) {
                                controller.listarCertificacaoesPorFinalizar();
                            } else {
                                logger.log(Level.WARNING, "Opção Inválida");
                                System.out.println("\033[31mOpção inválida!\033[0m");
                            }
                            break;
                        case 6:
                            System.out.println("1- PESQUISAR POR DATA");
                            System.out.println("2- PESQUISAR POR NÚMERO/ESTADO/FABRICANTE");

                            String escolha = scanner.nextLine();

                            if (escolha.equals("1")) {
                                System.out.println("DIGITE A DATA DE INICIO (FORMATO: yyyy-mm-dd): ");
                                String data_inicio = scanner.nextLine();
                                System.out.println("DIGITE A DATA DE FIM (FORMATO: yyyy-mm-dd): ");
                                String data_fim = scanner.nextLine();
                                controller.pesquisarPedidosData(data_inicio, data_fim);

                            } else if (escolha.equals("2")) {
                                System.out.println("DIGITE O NÚMERO/ESTADO/FABRICANTE: ");
                                String info_pedido = scanner.nextLine();
                                controller.pesquisarPedidos(info_pedido);

                            } else {
                                System.out.println("Opção inválida. Por favor, escolha 1 ou 2.");
                            }
                            break;
                        case 7:
                            System.out.println("DIGITE O NÚMERO DA CERTIFICAÇÃO: ");
                            int numero_certificacao = scanner.nextInt();
                            controller.verEstadoCertificacaoGestor(numero_certificacao);
                            break;
                        case 8:
                            System.out.println("DIGITE A MARCA/CÓDIGO DO EQUIPAMENTO: ");
                            String info_equipamento = scanner.nextLine();
                            controller.pesquisarEquipamentos(info_equipamento);
                            break;
                        case 9:
                            System.out.println("DESEJA ACEITAR OU RECUSAR UMA CONTA?");
                            System.out.println("1- ACEITAR");
                            System.out.println("2- RECUSAR");
                            int resposta = scanner.nextInt();

                            if (resposta == 1) {
                                controller.listarUtilizadores();
                                System.out.println("SELECIONE O UTILIZADOR QUE DESEJA ATIVAR: ");
                                int user_id = scanner.nextInt();
                                controller.ativarUtilizador(user_id);
                            } else if (resposta == 2) {
                                controller.listarUtilizadores();
                                System.out.println("SELECIONE O UTILIZADOR QUE DESEJA RECUSAR: ");
                                int user_id_remover = scanner.nextInt();
                                System.out.println("\033[32mA Conta do utilizador" + user_id_remover + " foi removida com sucesso!\033[0m");
                            } else {
                                System.out.println("\033[31mOpção inválida!\033[0m");
                            }
                            break;
                        case 10:
                            System.out.println("Selecione o Utilizador que deseja remover: ");
                            int user_id_remover = scanner.nextInt();
                            boolean sucesso = controller.removerContas(user_id_remover);
                            controller.removerContas(user_id_remover);
                            if (sucesso) {
                                logger.log(Level.INFO, "Conta do utilizador" + user_id_remover + " removida com sucesso");
                                System.out.println("\033[32mConta removida com sucesso!\033[0m");
                            } else {
                                logger.log(Level.SEVERE, "Erro ao tentar remover conta do utilizador " + user_id_remover);
                                System.out.println("\033[31mErro ao remover conta!\033[0m");
                            }
                            break;
                        case 11:
                            System.out.println("QUE PEDIDO DESEJA ACEITAR? DIGITE O ID DA CERTIFICAÇÃO/ID EQUIPAMENTO: ");
                            int id_certificacao = scanner.nextInt();
                            System.out.println("A QUE TÉCNICO DESEJA ATRIBUIR O EQUIPAMENTO PARA CERTIFICAÇÃO?: ");
                            int id_tecnico = scanner.nextInt();
                            controller.enviarNotificacao(id_tecnico, "Em espera de certeficação - " + id_certificacao, "tecnico", "Técnicos");
                            boolean sucesso_certificacao = controller.aceitarPedidoCerteficacao(id_certificacao, "Aceite", id_tecnico);

                            if(sucesso_certificacao){
                                System.out.println("\033[32mPedido aceite com sucesso!\033[0m");
                            }else{
                                System.out.println("\033[31mErro ao aceitar pedido!\033[0m");
                            }

                            break;
                        case 12:
                            System.out.println("DIGITE O NOME DA LICENÇA: ");
                            String nome_licenca = scanner.nextLine();
                            System.out.println("DIGITE O NÚMERO DA LICENÇA: ");
                            int numero_licenca = scanner.nextInt();
                            controller.adicionarLicenca(nome_licenca, numero_licenca);
                            System.out.println("\033[32mLicença adicionada com sucesso!\033[0m");
                            break;
                        case 13:
                            System.out.println("DIGITE O NÚMERO DA LICENÇA: ");
                            int numero_licenca_atribuir = scanner.nextInt();
                            System.out.println("DIGITE O NÚMERO DA CERTIFICAÇÃO: ");
                            int numero_certificacao_atribuir = scanner.nextInt();
                            controller.atribuirLicenca(numero_licenca_atribuir, numero_certificacao_atribuir);
                            System.out.println("\033[32mLicença atribuída com sucesso!\033[0m");
                            break;

                        case 14:
                            System.out.println("DIGITE O ID DO UTILIZADOR QUE DESEJA: ");
                            int id_utilizador = scanner.nextInt();
                            controller.listarUtilizadoresUnico(id_utilizador);
                            int campo_alterar = controller.listarCamposUtilizador(id_utilizador);
                            controller.alterarCampoPedido(campo_alterar, id_utilizador);
                            break;
                        case 15:
                            controller.listarLogs();
                            break;
                        case 0:
                            System.out.println("Adeus " + utilizador.getName() + "...");
                            break;
                        default:
                            System.out.println("\033[31mOpção inválida, selecione uma nova opção!\033[0m");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("\033[31mOpção inválida, insira um número válido!\033[0m\n");
                    scanner.nextLine();
                }
            } while (opcao != 0);

        } else if (utilizador.getType().equals("fabricante")) {
            do {
                menus.menuFabricante();

                try {
                    opcao = scanner.nextInt();
                    scanner.nextLine();


                    switch (opcao) {
                        case 1:
                            //Registar um fabricante
                            fazerRegisto("fabricante");
                            break;
                        case 2:
                            //Adicionar um equipoamento por um fabricante
                            System.out.println("==========ADICIONAR EQUIPAMENTO==========");
                            System.out.print("Marca do equipamento: ");
                            String marca = scanner.nextLine();
                            System.out.print("Modelo do equipamento: ");
                            String modelo = scanner.nextLine();
                            System.out.print("Setor Comercial: ");
                            String setor_comercial = scanner.nextLine();
                            System.out.print("Potência: ");
                            int potencia = scanner.nextInt();
                            System.out.print("Amperagem: ");
                            int amperagem = scanner.nextInt();
                            System.out.print("Número de modelo: ");
                            int numero_modelo = scanner.nextInt();
                            int id_user = controller.getIDbyusername(utilizador.getUsername());

                            Equipamento equipamento = new Equipamento(id_user, marca, modelo, setor_comercial, potencia, amperagem, numero_modelo);
                            boolean sucesso = controller.adicionarEquipamentos(equipamento);
                            if (sucesso) {
                                String fabricante_username = controller.getUsernamebyID(id_user);
                                Log log = new Log(fabricante_username, "Equipamento " + equipamento.getMarca() + " foi inserido na aplicação!");
                                controller.enviarLog(log);
                                System.out.println("\033[32mEquipamento adicionado com sucesso!\033[0m");
                            } else {
                                logger.log(Level.SEVERE, "Erro ao tentar adiconar equipamento " + numero_modelo);
                                System.out.println("\033[31mErro ao adicionar equipamento.\033[0m");
                            }

                            break;
                        case 3:
                            //Realizar pedido de certeficação de um equipamento pelo fabricante
                            controller.listarEquipamentos();
                            id_user = controller.getIDbyusername(utilizador.getUsername());
                            String user_username = controller.getUsernamebyID(id_user);
                            System.out.println("SELECIONE O EQUIPAMENTO QUE DESEJA CERTEFICAR: ");
                            int id_equipamento = scanner.nextInt();
                            Certificacao certificacao = new Certificacao(id_user, id_equipamento, "Iniciada");
                            controller.enviarCerteficacao(certificacao);
                            controller.setNumero_certificacao();
                            controller.enviarNotificacao(id_user, "Certeficação de Equipamento - " + id_equipamento, "fabricante", "Gestores");
                            Log log = new Log(user_username, "Pedido de certificação para o equipamento " + id_equipamento + " enviado!");
                            controller.enviarLog(log);
                            System.out.println("\033[32mPedido de certificação enviado com sucesso!\033[0m");
                            break;
                        case 4:
                            id_user = controller.getIDbyusername(utilizador.getUsername());
                            controller.listarEquipamentosFabricante(id_user);
                            System.out.println("FILTROS: 1- POR MARCA, 2- POR CÓDIGO");
                            int opcao_listar_equipamentos = scanner.nextInt();
                            if (opcao_listar_equipamentos == 1) {
                                controller.listarEquipamentosFabricanteOrder(id_user, "marca");
                            } else if (opcao_listar_equipamentos == 2) {
                                controller.listarEquipamentosFabricanteOrder(id_user, "codigo");
                            } else {
                                System.out.println("\033[31mOpção inválida!\033[0m");
                            }

                            break;
                        case 5:
                            id_user = controller.getIDbyusername(utilizador.getUsername());
                            controller.listarCertificacaoFabricante(id_user);
                            System.out.println("FILTROS: 1- POR NÚMERO, 2- POR DATA");
                            int opcao_listar = scanner.nextInt();
                            if (opcao_listar == 1) {
                                controller.listarCertificacaoFabricanteOrder(id_user, "numero");
                            } else if (opcao_listar == 2) {
                                controller.listarCertificacaoFabricanteOrder(id_user, "data");
                            } else {
                                System.out.println("\033[31mOpção inválida!\033[0m");
                            }
                            break;
                        case 6:
                            id_user = controller.getIDbyusername(utilizador.getUsername());
                            System.out.println("DIGITE A MARCA/CÓDIGO DO EQUIPAMENTO: ");
                            String info_equipamento = scanner.nextLine();
                            controller.pesquisarEquipamentosFabricante(info_equipamento, id_user);
                            break;
                        case 7:
                            id_user = controller.getIDbyusername(utilizador.getUsername());
                            System.out.println("DIGITE O NÚMERO/ESTADO DO PEDIDO: ");
                            String info_pedido = scanner.nextLine();
                            controller.pesquisarPedidosCert(info_pedido, id_user);
                            break;
                        case 8:
                            id_user = controller.getIDbyusername(utilizador.getUsername());
                            System.out.println("DIGITE O NÚMERO DA CERTIFICAÇÃO: ");
                            int numero_certificacao = scanner.nextInt();
                            controller.verEstadoCertificacaoFabricante(id_user, numero_certificacao);
                            break;

                        case 9:
                            System.out.println("TEM A CERTEZA QUE DESEJA REMOVER A SUA CONTA?");
                            System.out.println("1- SIM");
                            System.out.println("2- NÃO");
                            int resposta = scanner.nextInt();
                            if (resposta == 1) {
                                id_user = controller.getIDbyusername(utilizador.getUsername());
                                System.out.println("Uma notificação foi enviada para o suporte!");
                                Log log_remove = new Log(utilizador.getUsername(), "Fabricante " + id_user + " realizou um pedido de remoção de conta!");
                                controller.enviarLog(log_remove);
                                controller.enviarNotificacao(id_user, "Remoção de  Conta", "fabricante", "Gestores");
                            } else if (resposta == 2) {
                                System.out.println("Operação cancelada!");
                                menuUtilizador(utilizador);
                            } else {
                                System.out.println("\033[31mOpção inválida!\033[0m");
                            }
                            break;
                        case 10:
                            id_user = controller.getIDbyusername(utilizador.getUsername());
                            controller.listarUtilizadoresUnico(id_user);
                            System.out.println("ALTERAR INFORMAÇÕES");
                            int campo_alterar = controller.listarCamposUtilizador(id_user);
                            controller.alterarCampoPedido(campo_alterar, id_user);
                            break;
                        case 0:
                            System.out.println("Adeus " + utilizador.getName() + "...");
                            break;
                        default:
                            System.out.println("\033[31mOpção inválida, selecione uma nova opção!\033[0m");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("\033[31mOpção inválida, insira um número válido!\033[0m\n");
                    scanner.nextLine();
                }
            } while (opcao != 0);

        } else if (utilizador.getType().equals("tecnico")) {
            do {
                menus.menuTecnico();

                try {
                    opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1:

                            fazerRegisto("tecnico");
                            break;
                        case 2:
                            int id_user = controller.getIDbyusername(utilizador.getUsername());

                            controller.verNotificacoesTecnicos("Técnicos", id_user);
                            break;
                        case 3:

                            System.out.println("Tem a certeza de que deseja remover a sua conta?");
                            System.out.println("1- Sim");
                            System.out.println("2- Não");
                            int resposta = scanner.nextInt();
                            if (resposta == 1) {
                                id_user = controller.getIDbyusername(utilizador.getUsername());
                                System.out.println("Uma notificação foi enviada para o suporte!");
                                Log log_remove = new Log(utilizador.getUsername(), "Técnico " + id_user + " realizou um pedido de remoção de conta!");
                                controller.enviarLog(log_remove);
                                controller.enviarNotificacao(id_user, "Remoção de  Conta", "tecnico", "Gestores");
                            } else if (resposta == 2) {
                                System.out.println("Operação cancelada!");
                                menuUtilizador(utilizador);
                            } else {
                                System.out.println("\033[31mOpção inválida!\033[0m");
                            }
                            break;
                        case 4:
                            //Ver certificações de um técnico
                            controller.listarCertificacaoesNumero();
                            break;
                        case 5:
                            //Inspecionar equipamento
                            System.out.println("SELECIONA O EQUIPAMENTO QUE DESEJA INSPECIONAR: ");
                            int id_certeficar = scanner.nextInt();
                            controller.verEquipamentosCertificacao(id_certeficar);
                            break;
                        case 6:
                            System.out.println("DESEJA ACEITAR OU RECUSAR A CERTIFICAÇÃO DE UM EQUIPAMENTO?");
                            System.out.println("1- ACEITAR");
                            System.out.println("2- RECUSAR");
                            int resposta_certificacao = scanner.nextInt();
                            scanner.nextLine();

                            if (resposta_certificacao == 1) {
                                System.out.println("SELECIONA O EQUIPAMENTO QUE DESEJAS CERTIFICAR: ");
                                int id_equipamento = scanner.nextInt();
                                scanner.nextLine();

                                System.out.println("TEM A CERTEZA QUE DESEJA CERTIFICAR ESTE EQUIPAMENTO?");
                                System.out.println("1- SIM");
                                System.out.println("2- NÃO");
                                int resposta_certeza = scanner.nextInt();
                                scanner.nextLine();

                                if (resposta_certeza == 1) {
                                    controller.estadoCerteficacao(id_equipamento, "A Decorrer");
                                    boolean finalizarCertificacao = false;

                                    while (!finalizarCertificacao) {
                                        System.out.println("==============CERTIFICAÇÃO===========");
                                        System.out.println("==1- REALIZAR TESTES AO EQUIPAMENTO==");
                                        System.out.println("==2- FINALIZAR CERTIFICAÇÃO        ==");
                                        System.out.println("=====================================");
                                        int opcao_certeza = scanner.nextInt();
                                        scanner.nextLine();

                                        if (opcao_certeza == 1) {
                                            System.out.print("DESIGNAÇÃO DO TESTE: ");
                                            String designacao = scanner.nextLine();

                                            System.out.print("DESCRIÇÃO DO TESTE: ");
                                            String descricao = scanner.nextLine();

                                            System.out.print("VALOR MEDIDO: ");
                                            int valor_medido = scanner.nextInt();
                                            scanner.nextLine();

                                            Teste teste = new Teste(id_equipamento, designacao, descricao, valor_medido);
                                            controller.adicionarTeste(teste);

                                            logger.log(Level.INFO, "Teste ao equipamento " + id_equipamento + " foi realizado com sucesso");
                                            System.out.println("Teste realizado com sucesso!");

                                        } else if (opcao_certeza == 2) {
                                            System.out.print("SELECIONA O CUSTO DESTA CERTIFICAÇÃO: ");
                                            int custo = scanner.nextInt();
                                            scanner.nextLine();
                                            controller.aceitarCerteficacaoTecnico(id_equipamento, "Finalizado", custo);
                                            controller.certeficarEquipamento(id_equipamento);
                                            System.out.println("Certificação finalizada com sucesso!");
                                            finalizarCertificacao = true;
                                        } else {
                                            System.out.println("Opção inválida! Tente novamente.");
                                        }
                                    }
                                } else if (resposta_certeza == 2) {
                                    System.out.println("Operação cancelada!");
                                    menuUtilizador(utilizador);
                                } else {
                                    System.out.println("Opção inválida! Operação cancelada.");
                                }
                            } else if (resposta_certificacao == 2) {
                                id_user = controller.getIDbyusername(utilizador.getUsername());
                                System.out.println("SELECIONA O EQUIPAMENTO QUE DESEJAS RECUSAR: ");
                                int id_equipamento = scanner.nextInt();
                                controller.estadoCerteficacao(id_equipamento, "Recusado");
                                System.out.println("Equipamento recusado com sucesso!");
                                controller.enviarNotificacao(id_user, "Certeficação de Equipamento - " + id_equipamento + " recusada", "fabricante", "Gestores");
                            } else {
                                System.out.println("Opção inválida! Tente novamente.");
                            }
                            break;
                        case 7:
                            id_user = controller.getIDbyusername(utilizador.getUsername());
                            controller.listarUtilizadoresUnico(id_user);
                            int campo_alterar = controller.listarCamposUtilizador(id_user);
                            controller.alterarCampoPedido(campo_alterar, id_user);
                            break;
                        case 8:
                            System.out.println("DIGITE O NÚMERO DA CERTIFICAÇÃO QUE DESEJA CANCELAR: ");
                            int id_certificacao = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println("INDIQUE A RAZÃO DO CANCELAMENTO: ");
                            String razao = scanner.nextLine();
                            Log log = new Log(utilizador.getUsername(), "Certificação " + id_certificacao + " cancelada! Devido a: " + razao);
                            controller.enviarLog(log);
                            controller.CancelarCertificacao(id_certificacao, "Cancelado");
                            break;
                        case 0:
                            System.out.println("Adeus " + utilizador.getName() + "...");
                            break;
                        default:
                            System.out.println("\033[31mOpção inválida, selecione uma nova opção!\033[0m");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("\033[31mOpção inválida, insira um número válido!\033[0m\n");
                    scanner.nextLine();
                }
            } while (opcao != 0);
        }

    }
}
