package model;

public class Teste {
    String Designacao;
    String Descricao;
    int Valor_medido;
    int id_equipamento;

    public Teste(int id_equipamento, String Designacao, String Descricao, int Valor_medido){
        this.id_equipamento = id_equipamento;
        this.Designacao = Designacao;
        this.Descricao = Descricao;
        this.Valor_medido = Valor_medido;
    }

    public int getId_equipamento() {
        return id_equipamento;
    }

    public void setId_equipamento(int id_equipamento) {
        this.id_equipamento = id_equipamento;
    }

    public String getDesignacao() {
        return Designacao;
    }

    public void setDesignacao(String designacao) {
        Designacao = designacao;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public int getValor_medido() {
        return Valor_medido;
    }

    public void setValor_medido(int valor_medido) {
        Valor_medido = valor_medido;
    }
}
