package view;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Panel;

public class  InicialMenuPanel extends JPanel {

    public void paint(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        g.drawString("MENU INICIAL", width/2, 20);

    }
}
