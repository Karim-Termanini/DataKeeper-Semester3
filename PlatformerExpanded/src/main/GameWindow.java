package main;

import javax.swing.*;
/*
 * hier wird immer dem Fenster gesteuert
 *  */
public class GameWindow {

    public GameWindow(GamePanel gamePanel){
        JFrame jFrame = new JFrame();

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(gamePanel);

        // um die Fenster zu zentrieren
        jFrame.setResizable(false);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
}
