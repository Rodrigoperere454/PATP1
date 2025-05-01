package view;

public class AppMenus {

    /**
     * Função para mostrar o menu inicial
     */
    public void menuInicial(){
        System.out.println("=============== MENU INICIAL ==============");
        System.out.println("== 1- LOGIN DE UTILIZADOR                ==");
        System.out.println("== 2- REGISTAR UTILIZADOR                ==");
        System.out.println("== 3- ALTERAR DADOS DA BASE DE DADOS     ==");
        System.out.println("== 4- CONECTAR COM O SERVIDOR            ==");
        System.out.println("== 0- SAIR                               ==");
        System.out.println("===========================================");
        System.out.print("=>");
    }



    public void menuFabricanteCliente(){
        System.out.println("============== MENU FABRICANTE CLIENT ==============");
        System.out.println("== 1- REGISTAR FABRICANTE                         ==");
        System.out.println("==                                                ==");
        System.out.println("== 2- VER INFORMAÇÃO                              ==");
        System.out.println("==                                                ==");
        System.out.println("== 3- ALTERAR DADOS PRÓPRIOS                      ==");
        System.out.println("==                                                ==");
        System.out.println("== 4- ADICIONAR EQUIPAMENTO                       ==");
        System.out.println("==                                                ==");
        System.out.println("== 5- VER INFO DE UM EQUIPAMENTO                  ==");
        System.out.println("==                                                ==");
        System.out.println("== 6- LISTAR MEUS EQUIPAMENTOS                    ==");
        System.out.println("==                                                ==");
        System.out.println("== 7- LISTAR MEUS PEDIDOS DE CERTIFICAÇÃO         ==");
        System.out.println("==                                                ==");
        System.out.println("== 8- VER INFO DE UMA CERTIFICACAO                ==");
        System.out.println("==                                                ==");
        System.out.println("== 0- SAIR                                        ==");
        System.out.println("====================================================");
        System.out.print("FACRICANTE (CLIENT)=>");
    }


    public void clientMenu(){
        System.out.println("============== CLIENT MENU ===============");
        System.out.println("== 1- LOGIN                             ==");
        System.out.println("==                                      ==");
        System.out.println("== 2- EXIT                              ==");
        System.out.println("==========================================");
        System.out.print("=>");
    }



}
