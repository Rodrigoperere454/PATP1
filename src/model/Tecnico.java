package model;
import java.time.LocalDate;

public class Tecnico extends Utilizador {
    private String nif;
    private String telefone;
    private String morada;
    private String area_especializacao;
    private int nivel_certificacao;

    public Tecnico(String name, String username, String password, String email, String type,
                      String nif, String telefone, String morada, String area_especializacao, int nivel_certificacao){
        super(name, username, password, email, type);
        this.nif = nif;
        this.telefone = telefone;
        this.morada = morada;
        this.area_especializacao = area_especializacao;
        this.nivel_certificacao = nivel_certificacao;
    }

    // Getters e Setters


    public String getNif() {
        return nif;
    }


    public String getTelefone() {
        return telefone;
    }


    public String getMorada() {
        return morada;
    }


    public String getArea_especializacao() {
        return area_especializacao;
    }


    public int getNivel_certificacao() {
        return nivel_certificacao;
    }


}
