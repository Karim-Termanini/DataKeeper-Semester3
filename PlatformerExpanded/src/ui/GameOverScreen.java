package ui;

import java.awt.*;

public class GameOverScreen {
    private int finalLevel;
    private int enemiesDefeated;
    private int animationTick;

    public GameOverScreen() {
        this.animationTick = 0;
    }

    public void show(int finalLevel, int enemiesDefeated) {
        this.finalLevel = finalLevel;
        this.enemiesDefeated = enemiesDefeated;
        this.animationTick = 0;
    }
    public void update() {
        animationTick++;
    }
    public void render(Graphics g, int screenWidth, int screenHeight) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(50, 0, 0, 220));
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        float glowAlpha = (float)(Math.sin(animationTick * 0.03) * 0.3 + 0.4);
        g2d.setColor(new Color(255, 0, 0, (int)(80 * glowAlpha)));
        g2d.fillOval(centerX - 300, centerY - 300, 600, 600);
        g2d.setColor(new Color(255, 50, 50));
        g2d.setFont(new Font("Arial", Font.BOLD, 70));
        String title = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, centerX - titleWidth / 2, centerY - 120);
        int boxWidth = 500;
        int boxHeight = 180;
        int boxX = centerX - boxWidth / 2;
        int boxY = centerY - 30;
        g2d.setColor(new Color(30, 0, 0, 220));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2d.setColor(new Color(255, 50, 50));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
        String levelText = "Reached Level: " + finalLevel;
        int levelWidth = g2d.getFontMetrics().stringWidth(levelText);
        g2d.drawString(levelText, centerX - levelWidth / 2, boxY + 60);
        String enemyText = "Total Enemies Defeated: " + enemiesDefeated;
        int enemyWidth = g2d.getFontMetrics().stringWidth(enemyText);
        g2d.drawString(enemyText, centerX - enemyWidth / 2, boxY + 110);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        float promptAlpha = (float)(Math.sin(animationTick * 0.1) * 0.5 + 0.5);
        g2d.setColor(new Color(255, 255, 255, (int)(255 * promptAlpha)));
        String prompt = "Press R to Restart";
        int promptWidth = g2d.getFontMetrics().stringWidth(prompt);
        g2d.drawString(prompt, centerX - promptWidth / 2, boxY + boxHeight + 60);
    }
}

