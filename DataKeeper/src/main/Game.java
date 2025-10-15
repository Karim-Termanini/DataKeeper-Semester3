package main;

public class Game implements Runnable {

    private final GamePanel gamePanel;

    public Game(){
        gamePanel = new GamePanel();
        new GameWindow(gamePanel);
        gamePanel.requestFocus();
        startGameLoop();
    }

    private void startGameLoop(){
        Thread gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
    public void run() {
        final int FPS_SET = 120;
        double timePerFrame = 1000000000.0 / FPS_SET;
        long lastFrame = System.nanoTime();
        long now;
        int frames = 0;
        long lastCheck = System.currentTimeMillis();
        while(true){
            now = System.nanoTime();
            if (now - lastFrame >= timePerFrame){
                gamePanel.updateGame();
                gamePanel.repaint();
                lastFrame =  now;
                if (utils.Constants.DEBUG_LOGS) {
                    frames++;
                }
            } else {
                // Brief sleep to reduce CPU spinning and smooth out stutter
                try { Thread.sleep(1); } catch (InterruptedException ignored) {}
            }
            if(System.currentTimeMillis() - lastCheck >= 1000){
                lastCheck = System.currentTimeMillis();
                if (utils.Constants.DEBUG_LOGS) {
                    System.out.println("FPS:" +  frames);
                }
                frames = 0;
            }

        }
    }
}
