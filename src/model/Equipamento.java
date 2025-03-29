package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Equipamento {
    private int id;
    private int id_user;
    private String marca;
    private String modelo;
    private String setor_comercial;
    private int potencia;
    private int amperagem;
    private int codigo_sku;
    private int numero_modelo;

    public Equipamento(int id_user, String marca, String modelo, String setor_comercial, int potencia, int amperagem, int numero_modelo) {
        this.id_user = id_user;
        this.marca = marca;
        this.modelo = modelo;
        this.setor_comercial = setor_comercial;
        this.potencia = potencia;
        this.amperagem = amperagem;
        this.numero_modelo = numero_modelo;
    }


    public int getId_user() {
        return id_user;
    }


    public String getMarca() {
        return marca;
    }


    public String getModelo() {
        return modelo;
    }

    public String getSetor_comercial() {
        return setor_comercial;
    }



    public int getPotencia() {
        return potencia;
    }


    public int getAmperagem() {
        return amperagem;
    }



    public int getNumero_modelo() {
        return numero_modelo;
    }


}
