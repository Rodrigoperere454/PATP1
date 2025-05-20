package view;

import javax.swing.*;
import java.awt.*;

public class InicialMenu extends JFrame {
    private Container cont;

    public InicialMenu(){
        cont = getContentPane();
        cont.setLayout(new FlowLayout());
        JButton botao1 = new JButton("Botão 1");
        cont.add(botao1);
        JButton botao2 = new JButton("Botão 2");
        cont.add(botao2);
        JButton botao3 = new JButton("Botão 3");
        cont.add(botao3);
    }
}
