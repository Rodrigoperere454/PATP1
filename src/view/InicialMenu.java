package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InicialMenu extends JFrame implements ActionListener {
    private Container cont;
    private JButton botao1, botao2, botao3;

    public InicialMenu(){
        cont = getContentPane();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        JButton botao1 = new JButton("Login");
        botao1.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao1.addActionListener(this);
        cont.add(botao1);
        JButton botao2 = new JButton("Registar Utilizador");
        botao2.setAlignmentX(Component.CENTER_ALIGNMENT);
        cont.add(botao2);
        JButton botao3 = new JButton("Alterar Dados da Base de Dados");
        cont.add(botao3);
        botao3.setAlignmentX(Component.CENTER_ALIGNMENT);

    }

    public void actionPerformed(ActionEvent e){
        if (e.getSource() == botao1) {
            System.out.println("Login clicked");
        } else if (e.getSource() == botao2) {
            System.out.println("Registar Utilizador clicked");
        } else if (e.getSource() == botao3) {
            System.out.println("Alterar Dados da Base de Dados clicked");
        }

    }
}
