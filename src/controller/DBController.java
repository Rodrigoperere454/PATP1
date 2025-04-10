package controller;
import model.Utilizador;
import model.Fabricante;
import model.Equipamento;
import model.Tecnico;
import model.Certificacao;
import model.Teste;
import model.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Random;

public class DBController {
    private Connection conexao;
    Scanner scanner = new Scanner(System.in);
    public DBController(Connection conexao) {
        this.conexao = conexao;
    }


    /**
     * Função para adicionar um teste á base de dados. Este teste é executado por um técnino na hora de validar uma certificação.
     * @param teste
     * @return true or false
     */
    public boolean adicionarTeste(Teste teste){
        String sql = "INSERT INTO testes (id_equipamento, designacao, descricao, valor_medido) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, teste.getId_equipamento());
            stmt.setString(2, teste.getDesignacao());
            stmt.setString(3, teste.getDescricao());
            stmt.setInt(4, teste.getValor_medido());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao inserir teste: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para enivar um log para a base de dados. Recebe um objeto do tipo Log com a data, id do utilizador e uma descrição e insere na tabela logs.
     * @param log
     * @return true or false
     */
    public boolean enviarLog(Log log){
        String sql = "INSERT INTO logs (user_username, acao) VALUES (?, ?)";
        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, log.get_Username());
            stmt.setString(2, log.getDescricao());

            stmt.executeUpdate();
            return true;
        }catch (SQLException e){
            System.err.println("\033[31mErro ao enviar log: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para adicionar um equipameto á base de dados. Recebe um objeto do tipo Equipamento e insere na tabela equipamentos.
     * @param equipamento
     * @return true or false
     */
    public boolean adicionarEquipamentos(Equipamento equipamento) {
        String sql = "INSERT INTO equipamentos (id_fabricante, marca, modelo, setor_comercial, potencia, amperagem, codigo_sku, numero_modelo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sql_sku = "SELECT codigo_sku FROM equipamentos";

        Random random = new Random();
        long codigo_sku_random = random.nextInt(1000000);
        boolean skuExiste;

        try (PreparedStatement stmt = conexao.prepareStatement(sql_sku);
             ResultSet rs = stmt.executeQuery()) {

            do {
                skuExiste = false;
                while (rs.next()) {
                    if (rs.getInt("codigo_sku") == codigo_sku_random) {
                        codigo_sku_random = random.nextInt(1000000);
                        skuExiste = true;
                    }
                }
            } while (skuExiste);

        } catch (SQLException e) {
            System.err.println("\033[31mErro ao verificar código SKU: \033[0m" + e.getMessage());
            return false;
        }

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, equipamento.getId_user());
            stmt.setString(2, equipamento.getMarca());
            stmt.setString(3, equipamento.getModelo());
            stmt.setString(4, equipamento.getSetor_comercial());
            stmt.setInt(5, equipamento.getPotencia());
            stmt.setInt(6, equipamento.getAmperagem());
            stmt.setLong(7, codigo_sku_random);
            stmt.setInt(8, equipamento.getNumero_modelo());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("\033[31mErro ao inserir equipamento: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para verificar se existe algum gestor na base de dados. Usado no inicio do programa para verificar se é necessário registar um gestor.
     * @return true or false
     */
    public boolean verificar_gestores(){
        String sql = "SELECT COUNT(*) FROM utilizadores WHERE tipo = 'gestor'";
        boolean noGestores = false;
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                if (rs.getInt(1) == 0){
                    System.out.println("Não existem gestores registados. Por favor, registe um gestor.");
                    noGestores = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao verificar gestores: \033[0m" + e.getMessage());
        }
        return noGestores;
    }

    /**
     * Função para encriptar a password (Message Digest) do utilizador.
     * @param password
     * @return
     * @throws Exception
     */
    public String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(password.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * Função para enviar uma notificação para a bd. Recebe o id do utilizador, a descrição, o tipo e o encarregado e insere na tabela notificações.
     * @param idUtilizador
     * @param descricao
     * @param tipo
     * @param encarregado
     * @return true or false
     */
    public boolean enviarNotificacao(int idUtilizador, String descricao, String tipo, String encarregado){
        String sql = "INSERT INTO notificacoes (id_utilizador, descricao, tipo, encarregado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUtilizador);
            stmt.setString(2, descricao);
            stmt.setString(3, tipo);
            stmt.setString(4, encarregado);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        }catch (SQLException e) {
            System.err.println("\033[31mErro ao inserir utilizador: \033[0m" + e.getMessage());
            return false;
        }
    }


    /**
     * Função para listar todas as notificações por determiados encarregados
     * @param encarregado
     */
    public void listarNotificacoes(String encarregado) {
        String sql = "SELECT * FROM notificacoes WHERE encarregado = ?";
        System.out.println("NOTIFICAÇÕES PARA: " + encarregado);
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, encarregado);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("--------------------------------------------------------------------------------------------------");
                    System.out.println("ID do Utilizador: " + rs.getInt("id_utilizador") +
                            " - " + rs.getString("tipo") +
                            " " + rs.getTimestamp("data_hora") +
                            " Descrição: " + rs.getString("descricao"));
                }
            }
        } catch (Exception e) {
            System.out.println("\033[31mErro ao listar notificações: \033[0m" + e.getMessage());
        }
    }

    /**
     * Função para listar todas as notificações por determiados encarregados e id do técnico
     * @param encarregado
     * @param id_tecnico
     */
    public void verNotificacoesTecnicos(String encarregado, int id_tecnico) {
        String sql = "SELECT * FROM notificacoes WHERE encarregado = ? AND id_utilizador = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, encarregado);
            stmt.setInt(2, id_tecnico);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("---------------------------------------------------------------");

                    System.out.println("ID do Utilizador: " + rs.getInt("id_utilizador") +
                            " - " + rs.getString("tipo") +
                            " " + rs.getTimestamp("data_hora") +
                            " Descrição: " + rs.getString("descricao"));
                }
            }
        } catch (Exception e) {
            System.out.println("\033[31mErro ao listar notificações: \033[0m" + e.getMessage());
        }
    }

    /**
     * Função para listar todos os equipamentos de um determinado fabricante
     * @param id_equipamento
     */
    public void verEquipamentosCertificacao(int id_equipamento) {
        String sql = "SELECT * FROM equipamentos WHERE id_equipamento = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_equipamento);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n==============================================");
                    System.out.println("              DETALHES DO EQUIPAMENTO         ");
                    System.out.println("==============================================");
                    System.out.printf(" ID Equipamento    = %d\n", rs.getInt("id_equipamento"));
                    System.out.printf(" ID Fabricante     = %d\n", rs.getInt("id_fabricante"));
                    System.out.printf(" Marca             = %s\n", rs.getString("marca"));
                    System.out.printf(" Modelo            = %s\n", rs.getString("modelo"));
                    System.out.printf(" Setor Comercial   = %s\n", rs.getString("setor_comercial"));
                    System.out.printf(" Potência (W)      = %d\n", rs.getInt("potencia"));
                    System.out.printf(" Amperagem (A)     = %d\n", rs.getInt("amperagem"));
                    System.out.printf(" Código SKU        = %d\n", rs.getInt("codigo_sku"));
                    System.out.printf(" Número do Modelo  = %d\n", rs.getInt("numero_modelo"));
                    System.out.println("==============================================\n");
                } else {
                    System.out.println("\n Nenhum equipamento encontrado com o ID: " + id_equipamento + "\n");
                }
            }
        }catch(Exception e){
            System.out.println("\033[31mErro ao listar equipamentos: \033[0m" + e.getMessage());
        }
    }


    /**
     * Função para listar todos os equipamentos de um determinado fabricante onde ainda não tem pedido de certificação
     */
    public void listarEquipamentos(){
        String sql = "SELECT * FROM equipamentos WHERE data_certeficacao IS NULL";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                System.out.println("-----------------------------------------");
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("ID Fabricante: " + rs.getInt("id_fabricante"));
                System.out.println("Marca: " + rs.getString("marca"));
                System.out.println("Modelo: " + rs.getString("modelo"));
                System.out.println("Número do Modelo: " + rs.getInt("numero_modelo"));
                System.out.println("-----------------------------------------");
            }
        }catch (Exception e){
            System.out.println("\033[31mErro ao listar equipamentos: \033[0m" + e.getMessage());
        }
    }


    /**
     * Função para ir buscar o id do utilizador através do username
     * @param username
     * @return id do utilizador
     */
    public int getIDbyusername(String username){
        int id = 0;
        String sql = "SELECT id FROM utilizadores WHERE username = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                id = rs.getInt("id");
            }
        }catch (Exception e){
            System.out.println("\033[31mErro ao obter id do utilizador: \033[0m" + e.getMessage());
        }
        return id;
    }

    /**
     * Função para ir buscar o username através do id do utilizador
     * @param id
     * @return o username do utilizador
     */
    public String getUsernamebyID(int id){
        String username = "";
        String sql = "SELECT username FROM utilizadores WHERE id = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                username = rs.getString("username");
            }
        }catch (Exception e){
            System.out.println("\033[31mErro ao obter username do utilizador: \033[0m" + e.getMessage());
        }
        return username;
    }

    /**
     * Função para listar todos os logs da aplicação
     */
    public void listarLogs(){
        String sql = "SELECT * FROM logs";
        System.out.println("============================================================================");
        try(PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                System.out.println("Username: " + rs.getString("user_username") + " - Descrição: " + rs.getString("acao"));
                System.out.println("____________________________________________________________________________");
            }
            System.out.println("============================================================================");
        }catch(SQLException e){
            System.err.println("\033[31mErro ao listar logs: \033[0m" + e.getMessage());
        }
    }


    /**
     * Função para validar o login do utilizador através do username e password. Verifica se existe alguem na base de dados com os dados inseridos. Se existir
     * Devolve um objeto utilizador com os dados do utilizador.
     * @param username
     * @param password
     * @return
     */
    public Utilizador loginUtilizador(String username, String password) {
        String sql = "SELECT * FROM utilizadores WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String hashedPassword = hashPassword(password);

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String tipo = rs.getString("tipo");
                String estado = rs.getString("estado");

                if(estado.equals("desativo")){
                    System.out.println("\033[31mConta inativa. Por favor, contacte um gestor.\033[0m");
                    return null;
                }
                return new Utilizador(nome, username, hashedPassword, email, tipo);

            } else {
                System.out.println("\033[31mDados inválidos! Username ou senha incorretos.\033[0m");
                return null;
            }
        } catch (Exception e) {
            System.err.println("\033[31mErro ao logar utilizador: \033[0m" + e.getMessage());
            return null;
        }
    }

    public Utilizador loginUtilizadorFabricanteClient(String username, String password) {
        String sql = "SELECT * FROM utilizadores WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String hashedPassword = hashPassword(password);

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String tipo = rs.getString("tipo");
                String estado = rs.getString("estado");
                String nif = rs.getString("nif");
                String telefone = rs.getString("telefone");
                String morada = rs.getString("morada");
                String sector_comercial = rs.getString("sector_comercial");
                LocalDate data_inicio = rs.getDate("data_inicio").toLocalDate();

                if(estado.equals("desativo")){
                    System.out.println("\033[31mConta inativa. Por favor, contacte um gestor.\033[0m");
                    return null;
                }
                return new Fabricante(nome, username, hashedPassword, email, tipo, nif, telefone, morada, sector_comercial, data_inicio);

            } else {
                System.out.println("\033[31mDados inválidos! Username ou senha incorretos.\033[0m");
                return null;
            }
        } catch (Exception e) {
            System.err.println("\033[31mErro ao logar utilizador: \033[0m" + e.getMessage());
            return null;
        }
    }

    /**
     * Função para ativar a conta de um utilizador quando este se regista na aplicação
     * @param user_id
     * @return true or false
     */
    public boolean ativarUtilizador(int user_id){
        String sql = "UPDATE utilizadores SET estado = 'ativo' WHERE id = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, user_id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao ativar utilizador: \033[0m" + e.getMessage());
            return false;
        }
    }


    /**
     * Função para pesquisar por equipamentos por marca ou número de modelo. Recebe uma string com a informação a pesquisar e faz a query na base de dados.
     * @param info_equipamento
     */
    public void pesquisarEquipamentos(String info_equipamento) {
        String sql = "SELECT * FROM equipamentos WHERE marca ILIKE ? OR numero_modelo = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + info_equipamento + "%");

            try {
                long numeroModelo = Long.parseLong(info_equipamento);
                stmt.setLong(2, numeroModelo);
            } catch (NumberFormatException e) {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("-----------------------------------------");
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("ID Fabricante: " + rs.getInt("id_fabricante"));
                System.out.println("Marca: " + rs.getString("marca"));
                System.out.println("Modelo: " + rs.getString("modelo"));
                System.out.println("Número do Modelo: " + rs.getLong("numero_modelo"));
                System.out.println("-----------------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao pesquisar equipamentos: \033[0m" + e.getMessage());
        }
    }

    /**
     * Função para pesquisar pedidos de certificação por data. são fornecidos duas datas queriado um intervalo que vai ser usado
     * para verificar se existe alguma certificação com aquela data.
     * @param data_inicio
     * @param data_fim
     */
    public void pesquisarPedidosData(String data_inicio, String data_fim) {
        String sql = "SELECT * FROM certificacoes WHERE data_realizacao BETWEEN ? AND ?";
        boolean encontrado = false;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            Date dataInicio = Date.valueOf(data_inicio);
            Date dataFim = Date.valueOf(data_fim);

            stmt.setDate(1, dataInicio);
            stmt.setDate(2, dataFim);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                encontrado = true;
                System.out.println("-----------------------------------------");
                System.out.println("ID Certificação: " + rs.getInt("id_certificacao"));
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("ID Técnico: " + rs.getInt("id_tecnico"));
                System.out.println("Custo: " + rs.getInt("custo"));
                System.out.println("Estado: " + rs.getString("estado"));
                System.out.println("-----------------------------------------");
            }

            if (!encontrado) {
                System.out.println("\033[31mPedido com a informação dada não foi encontrado!\033[0m");
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao pesquisar pedidos: \033[0m" + e.getMessage());
        }
    }

    /**
     * Função para pesquisar pedidos de certificação por id ou por estado ou por id do fabricante.
     * @param info_pedido
     */
    public void pesquisarPedidos(String info_pedido) {
        String sql = "SELECT * FROM certificacoes WHERE id_certificacao = ? OR estado ILIKE ? OR id_fabricante = ?";
        boolean encontrado = false;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            try {
                int idCertificacao = Integer.parseInt(info_pedido);
                stmt.setInt(1, idCertificacao);
            } catch (NumberFormatException e) {
                stmt.setInt(1, -1);
            }

            stmt.setString(2, "%" + info_pedido + "%");

            try {
                int idFabricante = Integer.parseInt(info_pedido);
                stmt.setInt(3, idFabricante);
            } catch (NumberFormatException e) {
                stmt.setInt(3, -1);
            }


            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                encontrado = true;
                System.out.println("-----------------------------------------");
                System.out.println("ID Certificação: " + rs.getInt("id_certificacao"));
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("ID Técnico: " + rs.getInt("id_tecnico"));
                System.out.println("Custo: " + rs.getInt("custo"));
                System.out.println("Estado: " + rs.getString("estado"));
                System.out.println("-----------------------------------------");
            }

            if (!encontrado) {
                System.out.println("\033[31mPedido com a informação dada não foi encontrado!\033[0m");
            }

        } catch (SQLException e) {
            System.err.println("\033[31mErro ao pesquisar pedidos: \033[0m" + e.getMessage());
        }
    }


    /**
     * Função para ordenar e listar certificações por id de certificação
     * @return true or false
     */
    public boolean listarCertificacaoesNumero(){
        String sql = "SELECT * FROM certificacoes ORDER BY id_certificacao";

        try(PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                System.out.println("ID Certificação: " + rs.getInt("id_certificacao"));
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("ID Técnico: " + rs.getInt("id_tecnico"));
                System.out.println("Custo: " + rs.getInt("custo"));
                System.out.println("Estado: " + rs.getString("estado"));
                System.out.println("-----------------------------------------");
            }
            return true;
        }catch(SQLException e){
            System.err.println("\033[31mErro ao listar certeficações: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para ordenar e listar certificações por data de realização
     * @return true or false
     */
    public boolean listarCertificacaoesData(){
        String sql = "SELECT * FROM certificacoes ORDER BY data_realizacao";

        try(PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                System.out.println("ID Certificação: " + rs.getInt("id_certificacao"));
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("ID Técnico: " + rs.getInt("id_tecnico"));
                System.out.println("Custo: " + rs.getInt("custo"));
                System.out.println("Estado: " + rs.getString("estado"));
                System.out.println("-----------------------------------------");
            }
            return true;
        }catch(SQLException e){
            System.err.println("\033[31mErro ao listar certeficações: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para listar todas as certificações que ainda não foram finalizadas.
     * @return true or false
     */
    public boolean listarCertificacaoesPorFinalizar(){
        String sql = "SELECT * FROM certificacoes WHERE estado != 'Finalizado'";

        try(PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                System.out.println("ID Certificação: " + rs.getInt("id_certificacao"));
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("ID Técnico: " + rs.getInt("id_tecnico"));
                System.out.println("Custo: " + rs.getInt("custo"));
                System.out.println("Estado: " + rs.getString("estado"));
                System.out.println("-----------------------------------------");
            }
            return true;
        }catch(SQLException e){
            System.err.println("\033[31mErro ao listar certeficações: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para certificar um equipamento com o id do mesmo.
     * @param equipamento_id
     * @return true or false
     */
    public boolean certeficarEquipamento(int equipamento_id){
        String sql = "UPDATE equipamentos SET data_certeficacao = now() WHERE id_equipamento = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, equipamento_id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.err.println("\033[31mErro ao Certeficar Equipamento: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para enviar um pedido de uma certificação para um equipamento, recebe um objeto certificação e insere na tabela certificações.
     * @param certeficacao
     * @return true or false
     */
    public boolean enviarCerteficacao(Certificacao certeficacao){
        String sql = "INSERT INTO certificacoes (id_equipamento, id_tecnico,id_fabricante, custo, estado) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, certeficacao.getId_equipamento());
            stmt.setInt(2, certeficacao.getId_tecnico());
            stmt.setInt(3, certeficacao.getId_fabricante());
            stmt.setInt(4, certeficacao.getCusto());
            stmt.setString(5, certeficacao.getEstado());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.err.println("\033[31mErro ao enviar certeficação: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para definir o número de certificação de um equipamento. Este número é gerado automaticamente com o id da certificação e a data de realização.
     * É chamado no momento de inserir uma certificação na base de dados.
     * @return true or false
     */
    public boolean setNumero_certificacao() {
        String sql = "UPDATE certificacoes SET numero_certificacao = CAST(id_certificacao AS VARCHAR) || ? WHERE id_certificacao = (SELECT MAX(id_certificacao) FROM certificacoes)";

        LocalDate agora = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String agoraFormatado = agora.format(formatter);
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, agoraFormatado);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("\033[31mErro ao definir número de certificação: \033[0m" + e.getMessage());
            return false;
        }
    }


    /**
     * Função para ver o estado de uma certificação de um fabricante. Recebe o id do fabricante e o id da certificação..
     * @param id_fabricante
     * @param id_certificacao
     */
    public void verEstadoCertificacaoFabricante(int id_fabricante, int id_certificacao) {
        String sql = "SELECT * FROM certificacoes WHERE id_fabricante = ? AND id_certificacao = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_fabricante);
            stmt.setInt(2, id_certificacao);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Estado da certificação de ID: " + id_certificacao + " é: " + rs.getString("estado"));
            } else {
                System.out.println("\033[31mCerteficação não encontrada!\033[0m");
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao ver estado da certeficação: \033[0m" + e.getMessage());
        }
    }

    /**
     * Função para ver o estado de uma certificação de um gestor por um id de certificação.
     * @param id_certificacao
     */
    public void verEstadoCertificacaoGestor(int id_certificacao) {
        String sql = "SELECT * FROM certificacoes WHERE id_certificacao = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_certificacao);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Estado da certificação de ID: " + id_certificacao + " é: " + rs.getString("estado"));
            } else {
                System.out.println("\033[31mCerteficação não encontrada!\033[0m");
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao ver estado da certeficação: \033[0m" + e.getMessage());
        }
    }

    /**
     * Função para listar todos os equipamentos de um determinado fabricante
     * @param id_fabricante
     */
    public void listarCertificacaoFabricante(int id_fabricante){
        String sql = "SELECT * FROM certificacoes WHERE id_fabricante = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, id_fabricante);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                System.out.println("-----------------------------------------");
                System.out.println("ID Certificação: " + rs.getInt("id_certificacao"));
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("Data de pedido: " + rs.getDate("data_realizacao"));
                System.out.println("-----------------------------------------");
            }
        }catch(SQLException e){
            System.err.println("\033[31mErro ao listar certeficações: \033[0m" + e.getMessage());
        }
    }


    /**
     * Função para listar equipamentos de um determinado fabricante por ordem de marca ou de código SKU
     * @param id_fabricante
     * @param order
     */
    public void listarEquipamentosFabricanteOrder(int id_fabricante, String order){
        String sql;
        if(order.equals("marca")){
            sql = "SELECT * FROM equipamentos WHERE id_fabricante = ? ORDER BY marca";
        }else if(order.equals("codigo")){
            sql = "SELECT * FROM equipamentos WHERE id_fabricante = ? ORDER BY codigo_sku";
        }else{
            System.err.println("Parametro de ordem inválido: " + order);
            return;
        }

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, id_fabricante);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                System.out.println("-----------------------------------------");
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("Marca: " + rs.getString("marca"));
                System.out.println("Modelo: " + rs.getString("modelo"));
                System.out.println("Número do Modelo: " + rs.getInt("numero_modelo"));
                System.out.println("-----------------------------------------");
            }
        }catch(SQLException e){
            System.err.println("\033[31mErro ao listar equipamentos: \033[0m" + e.getMessage());
        }
    }

    /**
     * Função para pesquisar pedidos de certificação por fabricante e por numero de certificação ou estado de certificação.
     * @param cert_info
     * @param id_fabricante
     */
    public void pesquisarPedidosCert(String cert_info, int id_fabricante){
        String sql = "SELECT * FROM certificacoes WHERE id_fabricante = ? AND numero_certificacao = ? OR estado ILIKE ?";
        boolean encontrado = false;

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, id_fabricante);
            stmt.setString(2, cert_info);
            stmt.setString(3, "%" + cert_info + "%");


            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                encontrado = true;
                System.out.println("-----------------------------------------");
                System.out.println("ID Certificação: " + rs.getInt("id_certificacao"));
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("ID Técnico: " + rs.getInt("id_tecnico"));
                System.out.println("Custo: " + rs.getInt("custo"));
                System.out.println("Estado: " + rs.getString("estado"));
                System.out.println("-----------------------------------------");
            }

            if(!encontrado){
                System.out.println("\033[31mPedido com a informação dada não foi encontrado!\033[0m");
            }
    }catch(SQLException e){
        System.err.println("\033[31mErro ao pesquisar pedidos: \033[0m" + e.getMessage());
    }
    }

    /**
     * Função para pesquisar equipamentos de um fabricante por marca ou por código SKU
     * @param search_info
     * @param id_fabricante
     */
    public void pesquisarEquipamentosFabricante(String search_info, int id_fabricante){
        String sql = "SELECT * FROM equipamentos WHERE id_fabricante = ? AND marca ILIKE ? OR codigo_Sku = ?";
        boolean encontrado = false;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_fabricante);
            stmt.setString(2, "%" + search_info + "%");

            try {
                long codigoSku = Long.parseLong(search_info);
                stmt.setLong(3, codigoSku);
            } catch (NumberFormatException e) {
                stmt.setNull(3, java.sql.Types.BIGINT);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                encontrado = true;
                System.out.println("-----------------------------------------");
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("Marca: " + rs.getString("marca"));
                System.out.println("Modelo: " + rs.getString("modelo"));
                System.out.println("Número do Modelo: " + rs.getLong("numero_modelo"));
                System.out.println("-----------------------------------------");
            }

            if (!encontrado) {
                System.out.println("\033[31mEquipamento com a informação dada não foi encontrado!\033[0m");
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao pesquisar equipamentos: \033[0m" + e.getMessage());
        }
    }

    /**
     * Função para listar certificações de um fabricante por ordem de data ou por ordem de id da certificação
     * @param id_fabricante
     * @param order
     */
    public void listarCertificacaoFabricanteOrder(int id_fabricante, String order) {
        String sql;

        if (order.equals("data")) {
            sql = "SELECT * FROM certificacoes WHERE id_fabricante = ? ORDER BY data_realizacao";
        } else if (order.equals("numero")) {
            sql = "SELECT * FROM certificacoes WHERE id_fabricante = ? ORDER BY id_certificacao";
        } else {
            System.err.println("Parametro de ordem inválido: " + order);
            return;
        }

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_fabricante);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("-----------------------------------------");
                System.out.println("ID Certificação: " + rs.getInt("id_certificacao"));
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("Data de pedido: " + rs.getDate("data_realizacao"));
                System.out.println("-----------------------------------------");
            }
        } catch (SQLException e) {

            System.err.println("\033[31mErro ao listar certificações: \033[0m" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Função para listar equipamentos de um fabricante.
     * @param id_fabricante
     */
    public void listarEquipamentosFabricante(int id_fabricante){
        String sql = "SELECT * FROM equipamentos WHERE id_fabricante = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, id_fabricante);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                System.out.println("-----------------------------------------");
                System.out.println("ID Equipamento: " + rs.getInt("id_equipamento"));
                System.out.println("Marca: " + rs.getString("marca"));
                System.out.println("Modelo: " + rs.getString("modelo"));
                System.out.println("Número do Modelo: " + rs.getInt("numero_modelo"));
                System.out.println("-----------------------------------------");
            }
        }catch(SQLException e){
            System.err.println("\033[31mErro ao listar equipamentos: \033[0m" + e.getMessage());
        }
    }


    /**
     * Função para aceitar um pedido de certificação de um equipamento. Recebe o id da certificação, o estado e o id do técnico que vai realizar a certificação.
     * Coloca o estado com aceite.
     * @param id_certificacao
     * @param estado
     * @param id_tecnico
     * @return true or false
     */
    public boolean aceitarPedidoCerteficacao(int id_certificacao, String estado, int id_tecnico){
        String sql = "UPDATE certificacoes SET estado = ?, id_tecnico = ? WHERE id_equipamento = ? OR id_certificacao = ? AND estado = 'Iniciada'";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, estado);
            stmt.setInt(2, id_tecnico);
            stmt.setInt(3, id_certificacao);
            stmt.setInt(4, id_certificacao);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        }catch(SQLException e){
            System.err.println("\033[31mErro ao aceitar pedido de certeficação: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para alterar o estado de uma certificação de um equipamento.
     * Usada ao longo da aplicação para fazer alteração do estado dependendo da ação que é realizada.
     * @param id_equipamento
     * @param estado
     * @return true or false
     */
    public boolean estadoCerteficacao(int id_equipamento, String estado) {
        String sql = "UPDATE certificacoes SET estado = ? WHERE id_equipamento = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setInt(2, id_equipamento);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao alterar estado da certeficação: \033[0m" + e.getMessage());
            return false;
        }
    }


    /**
     * Função para aceitar uma certificação de um equipamento. Processo final de uma certificação onde é definido o custo e alterado o estado para finalizado.
     * Por um técnino.
     * @param id_equipamento
     * @param estado
     * @param custo
     * @return true or false
     */
    public boolean aceitarCerteficacaoTecnico(int id_equipamento, String estado, int custo){
        String sql = "UPDATE certificacoes SET estado = ?, custo = ? WHERE id_equipamento = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, estado);
            stmt.setInt(2, custo);
            stmt.setInt(3, id_equipamento);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.err.println("\033[31mErro ao aceitar certeficação: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para remover um utilizador da base de dados. Recebe o id do utilizador e remove dos utilziadores assim como as suas notificações.
     * @param user_id
     * @return true or false
     */
    public boolean removerContas(int user_id) {
        String sqlNotificacoes = "DELETE FROM notificacoes WHERE id_utilizador = ?";
        String sqlUtilizador = "DELETE FROM utilizadores WHERE id = ?";

        try (
                PreparedStatement stmtNotificacoes = conexao.prepareStatement(sqlNotificacoes);
                PreparedStatement stmtUtilizador = conexao.prepareStatement(sqlUtilizador)
        ) {
            stmtNotificacoes.setInt(1, user_id);
            stmtNotificacoes.executeUpdate();
            stmtUtilizador.setInt(1, user_id);
            int linhasAfetadas = stmtUtilizador.executeUpdate();

            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao remover utilizador: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para inserir um utilizador na base de dados.
     * @param utilizador
     * @return true or false
     */
    public boolean inserirUtilizador(Utilizador utilizador) {
        String sql = "INSERT INTO utilizadores (nome, username, password, email, tipo) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, utilizador.getName());
            stmt.setString(2, utilizador.getUsername());
            stmt.setString(3, utilizador.getPassword());
            stmt.setString(4, utilizador.getEmail());
            stmt.setString(5, utilizador.getType());

        } catch (SQLException e) {
            System.err.println("\033[31mErro ao inserir utilizador: \033[0m" + e.getMessage());
        }
        return false;
    }

    public boolean atribuirLicenca(int licensa_atribuir, int certificacao_atribuir){
        String sql = "UPDATE certificacoes SET numero_licenca = ? WHERE id_certificacao = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, licensa_atribuir);
            stmt.setInt(2, certificacao_atribuir);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }catch(SQLException e){
            System.err.println("\033[31mErro ao atribuir licença: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para cancelar uma certificação em que é realizada uma query para buscar a data de inicio e então calcular a diferença com a data de hoje e devolver
     * em horas. Depois é realizada uma query para atualizar o estado da certificação para cancelado e o tempo decorrido.
     * @param id_certificacao
     * @param estado
     * @return true ou false
     */
    public boolean CancelarCertificacao(int id_certificacao, String estado){
        String data_inicio = "";
        String sql_data = "SELECT data_realizacao FROM certificacoes WHERE id_certificacao = ?";
        String sql_cancel = "UPDATE certificacoes SET estado = ?, tempo_decorrido = ? WHERE id_certificacao = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql_data)){
            stmt.setInt(1, id_certificacao);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                data_inicio = rs.getString("data_realizacao");
            }else{
                return false;
            }

        }catch(SQLException e){
            System.err.println("\033[31mErro ao buscar data: \033[0m" + e.getMessage());
            return false;
        }

        DateTimeFormatter formatar = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime dataOriginal = LocalDateTime.parse(data_inicio, formatar);

        LocalDateTime agora = LocalDateTime.now();

        long diferencahoras = Duration.between(dataOriginal, agora).toHours();

        try(PreparedStatement stmt = conexao.prepareStatement(sql_cancel)){
            stmt.setString(1, estado);
            stmt.setInt(2, (int)diferencahoras);
            stmt.setInt(3, id_certificacao);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        }catch(SQLException e){
            System.err.println("\033[31mErro ao cancelar certificacao: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para inserir um fabricante na base de dados.
     * @param fabricante
     * @return true or false
     */
    public boolean inserirFabricante(Fabricante fabricante) {
        String sql = "INSERT INTO utilizadores (nome, username, password, email, tipo, nif, telefone, morada, sector_comercial, data_inicio) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, fabricante.getName());
            stmt.setString(2, fabricante.getUsername());
            stmt.setString(3, fabricante.getPassword());
            stmt.setString(4, fabricante.getEmail());
            stmt.setString(5, fabricante.getType());
            stmt.setString(6, fabricante.getNif());
            stmt.setString(7, fabricante.getTelefone());
            stmt.setString(8, fabricante.getMorada());
            stmt.setString(9, fabricante.getSector_comercial());
            stmt.setDate(10, java.sql.Date.valueOf(fabricante.getData()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int idGerado = rs.getInt("id");
                fabricante.setId(idGerado);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao inserir fabricante: \033[0m" + e.getMessage());
        }
        return false;
    }

    /**
     * Função para inserir um técnico na base de dados.
     * @param tecnico
     * @return true or false
     */
    public boolean inserirTecnico(Tecnico tecnico){
        String sql = "INSERT INTO utilizadores (nome, username, password, email, tipo, nif, telefone, morada, area_especializacao, nivel_certificacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, tecnico.getName());
            stmt.setString(2, tecnico.getUsername());
            stmt.setString(3, tecnico.getPassword());
            stmt.setString(4, tecnico.getEmail());
            stmt.setString(5, tecnico.getType());
            stmt.setString(6, tecnico.getNif());
            stmt.setString(7, tecnico.getTelefone());
            stmt.setString(8, tecnico.getMorada());
            stmt.setString(9, tecnico.getArea_especializacao());
            stmt.setInt(10, tecnico.getNivel_certificacao());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("\033[31mErro ao inserir técnico: \033[0m" + e.getMessage());
        }
        return false;
    }

    /**
     * Função para pesquisar utilizadores po nome, username ou tipo.
     * @param info_user
     */
    public void pesquisarUtilizadores(String info_user) {
        String sql = "SELECT * FROM utilizadores WHERE nome ILIKE ? OR username ILIKE ? OR tipo ILIKE ?";
        boolean encontrado = false;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + info_user + "%");
            stmt.setString(2, "%" + info_user + "%");
            stmt.setString(3, "%" + info_user + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                encontrado = true;
                System.out.println("-----------------------------------------");
                System.out.println("ID Utilizador: " + rs.getInt("id"));
                System.out.println("Nome: " + rs.getString("nome"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Tipo: " + rs.getString("tipo"));
                System.out.println("-----------------------------------------");
            }
            if (!encontrado) {
                System.out.println("\033[31mUtilizador com a informação dada não foi encontrado!\033[0m");
            }
        }catch (SQLException e){
            System.err.println("\033[31mErro ao pesquisar utilizadores: \033[0m" + e.getMessage());
        }
    }

    /**
     * Função para inserir uma licensa criada pelo gestor na base de dados.
     * @param nome_licensa
     * @param numero_licenca
     */
    public void adicionarLicenca(String nome_licensa, int numero_licenca){
        String sql = "INSERT INTO licencas (nome_licenca, numero_licenca, data_expiracao) VALUES (?, ?, ?)";
        LocalDate hoje = LocalDate.now();
        LocalDate expiracao = hoje.plusYears(1);
        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, nome_licensa);
            stmt.setInt(2, numero_licenca);
            stmt.setDate(3, java.sql.Date.valueOf(expiracao));
            stmt.executeUpdate();

            System.out.println("\033[32mLicença adicionada com sucesso!\033[0m");
        }catch(SQLException e){
            System.err.println("\033[31mErro ao adicionar licença: \033[0m" + e.getMessage());
        }
    }

    /**
     * Função para listar todos os utilizadores.
     */
    public void listarUtilizadores() {
        String sql = "SELECT * FROM utilizadores ORDER BY nome";
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n========= LISTA DE UTILIZADORES =========\n");

            while (rs.next()) {
                System.out.println("=========================================");
                System.out.printf("ID            = %d%n", rs.getInt("id"));
                System.out.printf("Nome          = %s%n", rs.getString("nome"));
                System.out.printf("Username      = %s%n", rs.getString("username"));
                System.out.printf("Estado        = %s%n", rs.getString("estado"));
                System.out.printf("Email         = %s%n", rs.getString("email"));
                System.out.printf("Tipo          = %s%n", rs.getString("tipo"));

                if (rs.getString("nif") != null) {
                    System.out.printf("NIF           = %s%n", rs.getString("nif"));
                }
                if (rs.getString("telefone") != null) {
                    System.out.printf("Telefone      = %s%n", rs.getString("telefone"));
                }
                if (rs.getString("morada") != null) {
                    System.out.printf("Morada        = %s%n", rs.getString("morada"));
                }
                if (rs.getString("sector_comercial") != null) {
                    System.out.printf("Setor Comercial = %s%n", rs.getString("sector_comercial"));
                }
                if (rs.getString("data_inicio") != null) {
                    System.out.printf("Data Início   = %s%n", rs.getDate("data_inicio"));
                }
                if (rs.getString("area_especializacao") != null) {
                    System.out.printf("Área Especialização = %s%n", rs.getString("area_especializacao"));
                }
                if (rs.getString("nivel_certificacao") != null) {
                    System.out.printf("Nível Certificação = %d%n", rs.getInt("nivel_certificacao"));
                }

                System.out.println("=========================================\n");
            }

        } catch (SQLException e) {
            System.err.println("\033[31mErro ao listar utilizadores:\033[0m " + e.getMessage());
        }
    }

    /**
     * Função para listar um utilizador por id.
     * @param id_utilizador
     */
    public void listarUtilizadoresUnico(int id_utilizador) {
        String sql = "SELECT * FROM utilizadores WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_utilizador);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n========= LISTA DE UTILIZADORES =========\n");
                while (rs.next()) {
                    System.out.println("=========================================");
                    System.out.printf("ID            = %d%n", rs.getInt("id"));
                    System.out.printf("Nome          = %s%n", rs.getString("nome"));
                    System.out.printf("Username      = %s%n", rs.getString("username"));
                    System.out.printf("Estado        = %s%n", rs.getString("estado"));
                    System.out.printf("Email         = %s%n", rs.getString("email"));
                    System.out.printf("Tipo          = %s%n", rs.getString("tipo"));

                    if (rs.getString("nif") != null) {
                        System.out.printf("NIF           = %s%n", rs.getString("nif"));
                    }
                    if (rs.getString("telefone") != null) {
                        System.out.printf("Telefone      = %s%n", rs.getString("telefone"));
                    }
                    if (rs.getString("morada") != null) {
                        System.out.printf("Morada        = %s%n", rs.getString("morada"));
                    }
                    if (rs.getString("sector_comercial") != null) {
                        System.out.printf("Setor Comercial = %s%n", rs.getString("sector_comercial"));
                    }
                    if (rs.getString("data_inicio") != null) {
                        System.out.printf("Data Início   = %s%n", rs.getDate("data_inicio"));
                    }
                    if (rs.getString("area_especializacao") != null) {
                        System.out.printf("Área Especialização = %s%n", rs.getString("area_especializacao"));
                    }
                    if (rs.getString("nivel_certificacao") != null) {
                        System.out.printf("Nível Certificação = %d%n", rs.getInt("nivel_certificacao"));
                    }

                    System.out.println("=========================================\n");
                }
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao listar utilizadores:\033[0m " + e.getMessage());
        }
    }

    /**
     * Função para listar todos os campos para alterar dados de um utilizador
     * @param id_utilizador
     * @return campo a ser alterado
     */
    public int listarCamposUtilizador(int id_utilizador) {
        String sql = "SELECT * FROM utilizadores WHERE id = ?";

        int campo = 0;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_utilizador);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("======ALTERAR INFORMAÇÕES DE %s ======%n", rs.getString("username"));
                    System.out.println("1- ALTRERAR NOME ");
                    System.out.println("2- ALTERAR USERNAME ");
                    System.out.println("3- ALTERAR EMAIL ");

                    if (rs.getString("nif") != null) {
                        System.out.println("4- ALTERAR NIF ");
                    }
                    if (rs.getString("telefone") != null) {
                        System.out.println("5- ALTERAR TELEFONE ");
                    }
                    if (rs.getString("morada") != null) {
                        System.out.println("6- ALTERAR MORADA ");
                    }
                    if (rs.getString("sector_comercial") != null) {
                        System.out.println("7- ALTERAR SETOR COMERCIAL ");
                    }
                    if (rs.getString("area_especializacao") != null) {
                        System.out.println("8- ALTERAR ÁREA ESPECIALIZAÇÃO ");
                    }
                    if (rs.getString("nivel_certificacao") != null) {
                        System.out.println("9- ALTERAR NÍVEL CERTIFICAÇÃO ");
                    }

                    System.out.println("=========================================\n");
                }

            }
            campo = scanner.nextInt();

        } catch (SQLException e) {
            System.err.println("\033[31mErro ao listar utilizadores:\033[0m " + e.getMessage());
        }
        return campo;
    }

    /**
     * Função para alterar o campo escolhido de um utilizador.
     * @param campo
     * @param id_utilizador
     */
    public void alterarCampoPedido(int campo, int id_utilizador){
        String sql = "";
        String novoValor = "";
        switch (campo){
            case 1:
                System.out.println("Insira o novo nome: ");
                novoValor = scanner.next();
                sql = "UPDATE utilizadores SET nome = ? WHERE id = ?";
                break;
            case 2:
                System.out.println("Insira o novo username: ");
                novoValor = scanner.next();
                sql = "UPDATE utilizadores SET username = ? WHERE id = ?";
                break;
            case 3:
                System.out.println("Insira o novo email: ");
                novoValor = scanner.next();
                sql = "UPDATE utilizadores SET email = ? WHERE id = ?";
                break;
            case 4:
                System.out.println("Insira o novo NIF: ");
                novoValor = scanner.next();
                sql = "UPDATE utilizadores SET nif = ? WHERE id = ?";
                break;
            case 5:
                System.out.println("Insira o novo telefone: ");
                novoValor = scanner.next();
                sql = "UPDATE utilizadores SET telefone = ? WHERE id = ?";
                break;
            case 6:
                System.out.println("Insira a nova morada: ");
                novoValor = scanner.next();
                sql = "UPDATE utilizadores SET morada = ? WHERE id = ?";
                break;
            case 7:
                System.out.println("Insira o novo setor comercial: ");
                novoValor = scanner.next();
                sql = "UPDATE utilizadores SET sector_comercial = ? WHERE id = ?";
                break;
            case 8:
                System.out.println("Insira a nova área de especialização: ");
                novoValor = scanner.next();
                sql = "UPDATE utilizadores SET area_especializacao = ? WHERE id = ?";
                break;
            case 9:
                System.out.println("Insira o novo nível de certificação: ");
                novoValor = scanner.next();
                sql = "UPDATE utilizadores SET nivel_certificacao = ? WHERE id = ?";
                break;
            default:
                System.out.println("\033[31mCampo inválido!\033[0m");
                return;
        }

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, novoValor);
            stmt.setInt(2, id_utilizador);
            stmt.executeUpdate();
            System.out.println("\033[32mCampo alterado com sucesso!\033[0m");
        }catch(SQLException e){
            System.err.println("\033[31mErro ao alterar campo: \033[0m" + e.getMessage());
        }
    }


}
