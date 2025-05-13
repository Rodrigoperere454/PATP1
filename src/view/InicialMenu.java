package view;

import javax.swing.*;
import java.awt.*;

public class InicialMenu extends JFrame {
    private Container cont;

    public void InicialMenu(){
        cont = getContentPane();
        cont.setLayout(new FlowLayout());
        JButton botao1 = new JButton("Botão 1");
        cont.add(botao1);
    }
}