package ui;

import java.awt.*;

public class LevelCompleteScreen {
    private int levelCompleted;
    private int timeSurvived;
    private int enemiesDefeated;
    private int animationTick;

    public LevelCompleteScreen() {
        this.animationTick = 0;
    }

    public void show(int levelCompleted, int timeSurvived, int enemiesDefeated) {
        this.levelCompleted = levelCompleted;
        this.timeSurvived = timeSurvived;
        this.enemiesDefeated = enemiesDefeated;
        this.animationTick = 0;
    }

    public void update() {
        animationTick++;
    }

    public void render(Graphics g, int screenWidth, int screenHeight) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, screenWidth, screenHeight);

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // Animated glow effect
        float glowAlpha = (float)(Math.sin(animationTick * 0.05) * 0.3 + 0.5);
        g2d.setColor(new Color(255, 215, 0, (int)(100 * glowAlpha)));
        g2d.fillOval(centerX - 250, centerY - 250, 500, 500);

        // Main title
        g2d.setColor(new Color(255, 215, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 60));
        String title = "LEVEL " + levelCompleted + " COMPLETE!";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, centerX - titleWidth / 2, centerY - 100);

        // Stats box
        int boxWidth = 500;
        int boxHeight = 200;
        int boxX = centerX - boxWidth / 2;
        int boxY = centerY - 30;

        g2d.setColor(new Color(50, 50, 50, 220));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2d.setColor(new Color(255, 215, 0));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Stats text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        
        String timeText = "Time Survived: " + timeSurvived + "s";
        int timeWidth = g2d.getFontMetrics().stringWidth(timeText);
        g2d.drawString(timeText, centerX - timeWidth / 2, boxY + 60);

        String enemyText = "Enemies Defeated: " + enemiesDefeated;
        int enemyWidth = g2d.getFontMetrics().stringWidth(enemyText);
        g2d.drawString(enemyText, centerX - enemyWidth / 2, boxY + 110);

        // Continue prompt
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        float promptAlpha = (float)(Math.sin(animationTick * 0.1) * 0.5 + 0.5);
        g2d.setColor(new Color(255, 255, 255, (int)(255 * promptAlpha)));
        String prompt = "Press ENTER to continue";
        int promptWidth = g2d.getFontMetrics().stringWidth(prompt);
        g2d.drawString(prompt, centerX - promptWidth / 2, boxY + boxHeight + 60);
    }
}

