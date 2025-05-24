package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InicialMenuFrame extends JFrame implements ActionListener {
    private Container cont;
    private JButton botao1, botao2, botao3;
    private int width = getWidth();
    private int height = getHeight();

    public InicialMenuFrame(){
        setTitle("Menu Inicial");
        cont = getContentPane();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        Dimension buttonSize = new Dimension(200, 40);

        JLabel titulo = new JLabel("Menu Principal");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        cont.add(titulo);
        cont.add(Box.createRigidArea(new Dimension(0, 30)));


        botao1 = new JButton("Login");
        botao1.setPreferredSize(buttonSize);
        botao1.setMaximumSize(buttonSize);
        botao1.setMinimumSize(buttonSize);
        botao1.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao1.addActionListener(this);
        cont.add(botao1);
        cont.add(Box.createRigidArea(new Dimension(0, 20)));
        botao2 = new JButton("Registar Utilizador");
        botao2.setPreferredSize(buttonSize);
        botao2.setMaximumSize(buttonSize);
        botao2.setMinimumSize(buttonSize);
        botao2.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cont.add(botao2);
        cont.add(Box.createRigidArea(new Dimension(0, 20)));
        botao3 = new JButton("Alterar Dados da Base de Dados");
        botao3.setPreferredSize(buttonSize);
        botao3.setMaximumSize(buttonSize);
        botao3.setMinimumSize(buttonSize);
        botao3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao3.setAlignmentX(Component.CENTER_ALIGNMENT);
        cont.add(botao3);

    }


    public void actionPerformed(ActionEvent e){
        System.out.println("Evento acionado:");
        if (e.getSource() == botao1) {
            LoginDialog loginDialog = new LoginDialog(this);
            loginDialog.setVisible(true);
        } else if (e.getSource() == botao2) {
            System.out.println("Registar Utilizador clicked");
        } else if (e.getSource() == botao3) {
            System.out.println("Alterar Dados da Base de Dados clicked");
        }

    }
}
