package model;

public class Certificacao {
    private int id_equipamento;
    private int id_tecnico;
    private int id_fabricante;
    private int custo;
    private String estado;

    public Certificacao(int id_fabricante, int id_equipamento, String estado) {
        this.id_fabricante = id_fabricante;
        this.id_equipamento = id_equipamento;
        this.estado = estado;
    }

    public int getId_fabricante() {
        return id_fabricante;
    }

    public void setId_fabricante(int id_fabricante) {
        this.id_fabricante = id_fabricante;
    }

    public int getId_equipamento() {
        return id_equipamento;
    }

    public void setId_equipamento(int id_equipamento) {
        this.id_equipamento = id_equipamento;
    }

    public int getId_tecnico() {
        return id_tecnico;
    }

    public void setId_tecnico(int id_tecnico) {
        this.id_tecnico = id_tecnico;
    }

    public int getCusto() {
        return custo;
    }

    public void setCusto(int custo) {
        this.custo = custo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
