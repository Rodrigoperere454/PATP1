package view;

public class AppMenus {

    /**
     * Função para mostrar o menu inicial
     */
    public void menuInicial(){
        System.out.println("============= MENU INICIAL ============");
        System.out.println("== 1- LOGIN DE UTILIZADOR            ==");
        System.out.println("== 2- REGISTAR UTILIZADOR            ==");
        System.out.println("== 3- ALTERAR DADOS DA BASE DE DADOS ==");
        System.out.println("== 4- ENTRAR EM MODO SERVIDOR        ==");
        System.out.println("== 0- SAIR                           ==");
        System.out.println("=======================================");
        System.out.print("=>");
    }

    /**
     * Função para mostrar o menu de utilizador
     */
    public void menuGestor(){
        System.out.println("================= MENU GESTOR =================");
        System.out.println("== 1- REGISTAR GESTOR                        ==");
        System.out.println("==                                           ==");
        System.out.println("== 2- LISTAR UTILIZADORES                    ==");
        System.out.println("==                                           ==");
        System.out.println("== 3- PESQUISAR UTILIZADORES                 ==");
        System.out.println("==                                           ==");
        System.out.println("== 4- VER NOTIFICAÇÕES                       ==");
        System.out.println("==                                           ==");
        System.out.println("== 5- VER CERTEFICAÇÕES                      ==");
        System.out.println("==                                           ==");
        System.out.println("== 6- PESQUISAR PEDIDOS                      ==");
        System.out.println("==                                           ==");
        System.out.println("== 7- VER ESTADO DE CERTIFICAÇÃO             ==");
        System.out.println("==                                           ==");
        System.out.println("== 8- PESQUISAR EQUIPAMENTOS                 ==");
        System.out.println("==                                           ==");
        System.out.println("== 9- ACEITAR/RECUSAR UTILIZADOR             ==");
        System.out.println("==                                           ==");
        System.out.println("== 10- REMOVER CONTA DE UTILIZADOR           ==");
        System.out.println("==                                           ==");
        System.out.println("== 11- ACEITAR PEDIDO CERTEFICAÇÃO           ==");
        System.out.println("==                                           ==");
        System.out.println("== 12- ADICIONAR LICENÇA EM APLICAÇÃO        ==");
        System.out.println("==                                           ==");
        System.out.println("== 13- ATRIBUIR LICENSA A CERTIFICAÇÃO       ==");
        System.out.println("==                                           ==");
        System.out.println("== 14- ALTERAR INFORMAÇÕES DE UTILIZADORES   ==");
        System.out.println("==                                           ==");
        System.out.println("== 15- VER LOGS DA APLICAÇÃO                 ==");
        System.out.println("==                                           ==");
        System.out.println("== 0- SAIR                                   ==");
        System.out.println("===============================================");
        System.out.print("=>");
    }

    /**
     *
     */
    public void menuFabricante(){
        System.out.println("============== MENU FABRICANTE ==============");
        System.out.println("== 1- REGISTAR FABRICANTE                  ==");
        System.out.println("==                                         ==");
        System.out.println("== 2- ADICIONAR EQUIPAMENTO                ==");
        System.out.println("==                                         ==");
        System.out.println("== 3- PEDIR CERTEFICACAO PARA EQUIPAMENTO  ==");
        System.out.println("==                                         ==");
        System.out.println("== 4- LISTAR EQUIPAMENTOS                  ==");
        System.out.println("==                                         ==");
        System.out.println("== 5- LISTAR PEDIDOS FEITOS                ==");
        System.out.println("==                                         ==");
        System.out.println("== 6- PESQUISAR EQUIPAMENTOS               ==");
        System.out.println("==                                         ==");
        System.out.println("== 7- PESQUISAR PEDIDOS CERTIFICACAO       ==");
        System.out.println("==                                         ==");
        System.out.println("== 8- VER ESTADO DE UMA CERTIFICAÇÃO       ==");
        System.out.println("==                                         ==");
        System.out.println("== 9- REMOVER CONTA                        ==");
        System.out.println("==                                         ==");
        System.out.println("== 10- ALTERAR MINHAS INFOS                ==");
        System.out.println("==                                         ==");
        System.out.println("== 0- SAIR                                 ==");
        System.out.println("=============================================");
        System.out.print("=>");
    }


    /**
     * Função para mostrar o menu de técnico
     */
    public void menuTecnico(){
        System.out.println("====================== MENU TÉCNICO ======================");
        System.out.println("== 1- REGISTAR TÉCNICO                                  ==");
        System.out.println("==                                                      ==");
        System.out.println("== 2- VER NOTIFICAÇÕES                                  ==");
        System.out.println("==                                                      ==");
        System.out.println("== 3- REMOVER CONTA                                     ==");
        System.out.println("==                                                      ==");
        System.out.println("== 4- VER CERTIFICAÇÕES                                 ==");
        System.out.println("==                                                      ==");
        System.out.println("== 5- INSPECIONAR EQUIPAMENTO                           ==");
        System.out.println("==                                                      ==");
        System.out.println("== 6- ACEITAR/NEGAR CERTIFICAÇÃO DE EQUIPAMENTO         ==");
        System.out.println("==                                                      ==");
        System.out.println("== 7- ALTERAR MINHAS INFOS                              ==");
        System.out.println("==                                                      ==");
        System.out.println("== 8- CANCELAR CERTIFICAÇÃO                             ==");
        System.out.println("==                                                      ==");
        System.out.println("== 0- SAIR                                              ==");
        System.out.println("==========================================================");
        System.out.print("=>");
    }

    /**
     * Função para mostrar o menu de tipo
     */
    public void tipoMenu(){
        System.out.println(" == SELECIONE O SEU TIPO: ==");
        System.out.println(" == 1- Fabricante         ==");
        System.out.println(" == 2- Técnico            ==");
        System.out.println(" == 0- Sair               ==");
        System.out.println(" ===========================");
        System.out.print("=>");
    }

    public void clientMenu(){
        System.out.println("============== CLIENT MENU ==============");
        System.out.println("== 1- LOGIN                             ==");
        System.out.println("==                                      ==");
        System.out.println("== 2- RECEIVE MESSAGE                   ==");
        System.out.println("==                                      ==");
        System.out.println("== 3- EXIT                              ==");
        System.out.println("==========================================");
        System.out.print("=>");
    }



}
