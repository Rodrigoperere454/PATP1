package controller;
import model.Utilizador;
import model.Fabricante;
import model.Equipamento;
import java.sql.*;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.*;

public class DBController {
    private Connection conexao;
    Scanner scanner = new Scanner(System.in);
    public DBController(Connection conexao) {
        this.conexao = conexao;
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
     * Função para validar o login do utilizador através do username e password. Verifica se existe alguem na base de dados com os dados inseridos. Se existir
     * @param username
     * @param password
     * @return
     */
    public Fabricante loginUtilizadorFabricanteClient(String username, String password) {
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



    /**
     * Função para pesquisar um equipamento de um fabricante através de um código SKU e id do fabricante fornecido.
     * @param codigo_sku
     * @param id_fabricante
     * @return um array com equipamentos
     */
    public String[] pesquisarEquipamentosFabricanteClient(String codigo_sku, int id_fabricante) {
        String sql = "SELECT * FROM equipamentos WHERE id_fabricante = ? AND codigo_Sku = ?";
        String[] info_equip = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_fabricante);

            try {
                long codigoSku = Long.parseLong(codigo_sku);
                stmt.setLong(2, codigoSku);
            } catch (NumberFormatException e) {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                info_equip = new String[] {
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("setor_comercial"),
                        rs.getString("potencia"),
                        rs.getString("amperagem"),
                        rs.getString("codigo_Sku"),
                        rs.getString("numero_modelo"),
                        rs.getString("data_submissao"),
                        rs.getString("data_certeficacao"),
                };
            } else {
                System.out.println("\033[31mEquipamento com a informação dada não foi encontrado!\033[0m");
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao pesquisar equipamentos: \033[0m" + e.getMessage());
        }

        return info_equip;
    }


    /**
     * Função para pesquisar uma certificação de um fabricante através de um número de certificação e id do fabricante fornecido.
     * @param numero_cert
     * @param id_fabricante
     * @return um array com as certificações
     */
    public String[] pesquisarCertificacaoFabricanteClient(String numero_cert, int id_fabricante) {
        String sql = "SELECT * FROM certificacoes WHERE id_fabricante = ? AND numero_certificacao ILIKE ?";
        String[] info_cert = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_fabricante);
            stmt.setString(2, numero_cert);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                info_cert = new String[] {
                        rs.getString("id_tecnico"),
                        rs.getString("data_realizacao"),
                        rs.getString("tempo_decorrido"),
                        rs.getString("custo"),
                        rs.getString("estado"),
                };
            } else {
                System.out.println("\033[31mEquipamento com a informação dada não foi encontrado!\033[0m");
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao pesquisar equipamentos: \033[0m" + e.getMessage());
        }

        return info_cert;
    }


    /**
     * Função para listar todos os equipamentos de um fabricante. Recebe o id do fabricante..
     * @param id_fabricante
     * @return um array com os equipamentos
     */
    public String[] listarEquipamentosFabricanteClient(int id_fabricante){
        String sql = "SELECT * FROM equipamentos WHERE id_fabricante = ?";
        List<String> listaEquips = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_fabricante);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                listaEquips.add(rs.getString("marca"));
                listaEquips.add(rs.getString("modelo"));
                listaEquips.add(rs.getString("setor_comercial"));
                listaEquips.add(rs.getString("potencia"));
                listaEquips.add(rs.getString("amperagem"));
                listaEquips.add(rs.getString("codigo_Sku"));
                listaEquips.add(rs.getString("numero_modelo"));
                listaEquips.add(rs.getString("data_submissao"));
                listaEquips.add(rs.getString("data_certeficacao"));
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao listar equipamentos: \033[0m" + e.getMessage());
        }
        String[] ListaEquipamento = new String[listaEquips.size()];
        listaEquips.toArray(ListaEquipamento);

        return ListaEquipamento;
    }

    /**
     * Função para listar todas as certificações de um fabricante. Recebe o id do fabricante.
     * @param id_fabricante
     * @return um array com as certificações
     */
    public String[] listarCertificacoesFabricanteClient(int id_fabricante){
        String sql = "SELECT * FROM certificacoes WHERE id_fabricante = ?";
        List<String> listaCert = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_fabricante);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                listaCert.add(rs.getString("id_certificacao"));
                listaCert.add(rs.getString("id_equipamento"));
                listaCert.add(rs.getString("id_tecnico"));
                listaCert.add(rs.getString("id_fabricante"));
                listaCert.add(rs.getString("data_realizacao"));
                listaCert.add(rs.getString("tempo_decorrido"));
                listaCert.add(rs.getString("custo"));
                listaCert.add(rs.getString("estado"));
                listaCert.add(rs.getString("numero_certificacao"));
                listaCert.add(rs.getString("numero_licenca"));
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao listar certificacoes: \033[0m" + e.getMessage());
        }
        String[] ListaCertificacoes = new String[listaCert.size()];
        listaCert.toArray(ListaCertificacoes);

        return ListaCertificacoes;
    }



    /**
     * Função para inserir um fabricante na base de dados.
     * @param fabricante
     * @return true or false
     */
    public boolean inserirFabricanteClient(Fabricante fabricante) {
        String sql = "INSERT INTO utilizadores (nome, username, password,estado, email, tipo, nif, telefone, morada, sector_comercial, data_inicio) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, fabricante.getName());
            stmt.setString(2, fabricante.getUsername());
            stmt.setString(3, fabricante.getPassword());
            stmt.setString(4, "ativo");
            stmt.setString(5, fabricante.getEmail());
            stmt.setString(6, fabricante.getType());
            stmt.setString(7, fabricante.getNif());
            stmt.setString(8, fabricante.getTelefone());
            stmt.setString(9, fabricante.getMorada());
            stmt.setString(10, fabricante.getSector_comercial());
            stmt.setDate(11, java.sql.Date.valueOf(fabricante.getData()));

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
     * Função para alterar os dados de um cliente. Recebe o id do utilizador e os novos dados.
     * @param $nome
     * @param $email
     * @param $pass
     * @param $telefone
     * @param $morada
     * @param $nif
     * @param id_utilizador
     * @return true or false
     */
    public boolean alterarDadosClient(String $nome, String $email, String $pass,   String $telefone, String $morada, String $nif, int id_utilizador) {
        String sql = "UPDATE utilizadores SET password = ?, nome = ?, email = ?, nif = ?, telefone = ?, morada = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, $pass);
            stmt.setString(2, $nome);
            stmt.setString(3, $email);
            stmt.setString(4, $nif);
            stmt.setString(5, $telefone);
            stmt.setString(6, $morada);
            stmt.setInt(7, id_utilizador);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("\033[31mErro ao alterar dados: \033[0m" + e.getMessage());
            return false;
        }
    }

    /**
     * Função para buscar os dados de um fabricante através do id do fabricante.
     * @param id_fabricante
     * @return um array com os dados do fabricante
     */
    public String[] buscarDadosFabricanteClient(int id_fabricante){
        String sql = "SELECT * FROM utilizadores WHERE id = ?";
        String[] info_fabricante = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id_fabricante);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                info_fabricante = new String[] {
                        rs.getString("nome"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("nif"),
                        rs.getString("telefone"),
                        rs.getString("morada"),
                        rs.getString("sector_comercial"),
                        rs.getString("data_inicio")
                };
            } else {
                System.out.println("\033[31mFabricante com a informação dada não foi encontrado!\033[0m");
            }
        } catch (SQLException e) {
            System.err.println("\033[31mErro ao buscar dados do fabricante: \033[0m" + e.getMessage());
        }

        return info_fabricante;
    }



}