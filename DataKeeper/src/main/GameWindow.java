package main;

import javax.swing.JFrame;
public class GameWindow {
    public GameWindow(GamePanel gamePanel){
        JFrame jFrame = new JFrame();
        jFrame.setTitle("DATA KEEPER");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(gamePanel);
        jFrame.setResizable(false);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
}
