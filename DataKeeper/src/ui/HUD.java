package ui;

import entities.Player;
import gameplay.SurvivalTimer;
import levels.LevelConfig;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;

public class HUD {
    private final Player player;
    private SurvivalTimer timer;
    private int currentLevel;
    private int enemiesRemaining;
    private int sessionDefeated;

    public HUD(Player player, SurvivalTimer timer, LevelConfig config, int currentLevel) {
        this.player = player;
        this.timer = timer;
        this.currentLevel = currentLevel;
        this.enemiesRemaining = 0;
        this.sessionDefeated = 0;
    }
    public void update(int enemiesRemaining) {
        this.enemiesRemaining = enemiesRemaining;
    }
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawPlayerHealth(g2d);
        drawTimer(g2d);
        drawLevelInfo(g2d);
        drawEnemyCounter(g2d);
        drawSessionDefeated(g2d);
    }
    private void drawPlayerHealth(Graphics2D g) {
        int x = 20;
        int y = 20;
        int barWidth = 250;
        int barHeight = 30;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x - 5, y - 5, barWidth + 10, barHeight + 10, 10, 10);
        g.setColor(new Color(100, 0, 0));
        g.fillRect(x, y, barWidth, barHeight);
        int healthWidth = (int) ((barWidth * player.getHealth()) / 200.0);
        Color healthColor;
        if (player.getHealth() > 120) {
            healthColor = new Color(0, 200, 0);
        } else if (player.getHealth() > 60) {
            healthColor = new Color(255, 200, 0);
        } else {
            healthColor = new Color(255, 50, 0);
        }
        g.setColor(healthColor);
        g.fillRect(x, y, healthWidth, barHeight);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawRect(x, y, barWidth, barHeight);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String healthText = "HP: " + player.getHealth() + " / 200";
        g.drawString(healthText, x + 10, y + 20);
    }

    private void drawTimer(Graphics2D g) {
    int screenWidth = g.getClipBounds().width;
    int x = Math.max(0, screenWidth / 2 - 100);
        int y = 20;
        int width = 200;
        int height = 50;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(x, y, width, height, 15, 15);
        int remainingSeconds = timer.getRemainingSeconds();
        String timeText = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60);
        
        Color timeColor;
        if (remainingSeconds > 20) {
            timeColor = Color.WHITE;
        } else if (remainingSeconds > 10) {
            timeColor = new Color(255, 200, 0);
        } else {
            timeColor = new Color(255, 50, 50);
        }
        
        g.setColor(timeColor);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(timeText);
        g.drawString(timeText, x + (width - textWidth) / 2, y + 38);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, width, height, 15, 15);
    }

    private void drawLevelInfo(Graphics2D g) {
    int screenWidth = g.getClipBounds().width;
    int x = Math.max(0, screenWidth - 220);
        int y = 20;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x - 5, y - 5, 210, 40, 10, 10);
        g.setColor(new Color(255, 215, 0));
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String levelText = "LEVEL " + currentLevel;
        g.drawString(levelText, x, y + 28);
    }

    private void drawEnemyCounter(Graphics2D g) {
    int screenWidth = g.getClipBounds().width;
    int x = Math.max(0, screenWidth - 220);
        int y = 70;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x - 5, y - 5, 210, 35, 10, 10);
        g.setColor(Color.RED);
        g.fillOval(x, y + 5, 20, 20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        String enemyText = "Enemies: " + enemiesRemaining;
        g.drawString(enemyText, x + 30, y + 22);
    }

    private void drawSessionDefeated(Graphics2D g) {
        int screenWidth = g.getClipBounds().width;
        int x = Math.max(0, screenWidth - 220);
        int y = 110;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x - 5, y - 5, 210, 35, 10, 10);
        g.setColor(new Color(100, 200, 255));
        g.fillOval(x, y + 5, 20, 20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        String text = "Besiegt: " + sessionDefeated;
        g.drawString(text, x + 30, y + 22);
    }

    public void setTimer(SurvivalTimer timer) {
        this.timer = timer;
    }

    public void setConfig(LevelConfig config) {
        // This method is now empty as the config field was removed.
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    public void setSessionDefeated(int value) {
        this.sessionDefeated = value;
    }
}
