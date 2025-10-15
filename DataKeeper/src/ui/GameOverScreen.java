package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RadialGradientPaint;
import java.awt.Point;
import java.awt.BasicStroke;

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
    int centerX = screenWidth / 2;
    int centerY = screenHeight / 2;
    // Dark vignette background
    g2d.setColor(new Color(10, 0, 0));
    g2d.fillRect(0, 0, screenWidth, screenHeight);
    g2d.setPaint(new RadialGradientPaint(new Point(centerX, centerY), Math.max(screenWidth, screenHeight) / 2f,
        new float[]{0f, 1f}, new Color[]{new Color(40, 0, 0, 160), new Color(0, 0, 0, 240)}));
    g2d.fillRect(0, 0, screenWidth, screenHeight);
        
        float glowAlpha = (float)(Math.sin(animationTick * 0.03) * 0.3 + 0.4);
        g2d.setColor(new Color(255, 0, 0, (int)(80 * glowAlpha)));
        g2d.fillOval(centerX - 300, centerY - 300, 600, 600);
    g2d.setColor(new Color(255, 70, 70));
    g2d.setFont(new Font("Arial", Font.BOLD, 66));
    String title = "SPIEL VORBEI";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, centerX - titleWidth / 2, centerY - 120);
        int boxWidth = 500;
    int boxHeight = 220;
        int boxX = centerX - boxWidth / 2;
        int boxY = centerY - 30;
    g2d.setColor(new Color(30, 0, 0, 200));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
    g2d.setColor(new Color(255, 50, 50));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
    String levelText = "Erreichtes Level: " + finalLevel;
        int levelWidth = g2d.getFontMetrics().stringWidth(levelText);
    g2d.drawString(levelText, centerX - levelWidth / 2, boxY + 60);
    String enemyText = "Besiegte Gegner (Sitzung): " + enemiesDefeated;
        int enemyWidth = g2d.getFontMetrics().stringWidth(enemyText);
    g2d.drawString(enemyText, centerX - enemyWidth / 2, boxY + 110);
    String tip = "Tipp: Übe Ausweichmanöver und nutze das Portal rechtzeitig!";
    g2d.setFont(new Font("Arial", Font.PLAIN, 18));
    int tipWidth = g2d.getFontMetrics().stringWidth(tip);
    g2d.drawString(tip, centerX - tipWidth / 2, boxY + 150);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        float promptAlpha = (float)(Math.sin(animationTick * 0.1) * 0.5 + 0.5);
        g2d.setColor(new Color(255, 255, 255, (int)(255 * promptAlpha)));
    String prompt = "Drücke R zum Neustart oder ESC fürs Menü";
        int promptWidth = g2d.getFontMetrics().stringWidth(prompt);
        g2d.drawString(prompt, centerX - promptWidth / 2, boxY + boxHeight + 60);
    }
}

