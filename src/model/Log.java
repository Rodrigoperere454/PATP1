package model;


public class Log {
    private String username;
    private String descricao;

    public Log(String username, String descricao) {
        this.username = username;
        this.descricao = descricao;
    }

    public String get_Username() {
        return username;
    }

    public String getDescricao() {
        return descricao;
    }
}
