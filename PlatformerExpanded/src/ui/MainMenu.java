package ui;

import java.awt.*;

public class MainMenu {
    private int selectedOption;
    private final int OPTION_START = 0;
    private final int OPTION_EXIT = 1;
    private final int TOTAL_OPTIONS = 2;
    private int highestLevel;
    private int animationTick;
    private float glowIntensity;

    public MainMenu() {
        this.selectedOption = OPTION_START;
        this.highestLevel = loadHighestLevel();
        this.animationTick = 0;
        this.glowIntensity = 0.5f;
    }
    public void update() {
        animationTick++;
        glowIntensity = 0.5f + 0.3f * (float)Math.sin(animationTick * 0.05);
    }
    public void render(Graphics g, int screenWidth, int screenHeight) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(20, 20, 40));
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setFont(new Font("Arial", Font.BOLD, 72));
        g.setColor(new Color(100, 200, 255));
        String title = "ARENA FIGHTER";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (screenWidth - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 200);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.setColor(new Color(150, 150, 200));
        String subtitle = "Survival Mode";
        fm = g.getFontMetrics();
        int subtitleX = (screenWidth - fm.stringWidth(subtitle)) / 2;
        g.drawString(subtitle, subtitleX, 250);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.setColor(new Color(255, 215, 0));
        String highScore = "Highest Level: " + highestLevel;
        fm = g.getFontMetrics();
        int scoreX = (screenWidth - fm.stringWidth(highScore)) / 2;
        g.drawString(highScore, scoreX, 350);
        int menuY = 500;
        int spacing = 80;
        drawOption(g, "START GAME", screenWidth / 2, menuY, selectedOption == OPTION_START);
        drawOption(g, "EXIT", screenWidth / 2, menuY + spacing, selectedOption == OPTION_EXIT);

        // Controls hint
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(new Color(150, 150, 150));
        String hint = "Use W/S to navigate, ENTER to select";
        fm = g.getFontMetrics();
        int hintX = (screenWidth - fm.stringWidth(hint)) / 2;
        g.drawString(hint, hintX, screenHeight - 50);
    }

    private void drawOption(Graphics g, String text, int centerX, int y, boolean selected) {
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = centerX - textWidth / 2;

        if (selected) {
            int glowAlpha = (int)(100 * glowIntensity);
            g2d.setColor(new Color(100, 200, 255, glowAlpha));
            g2d.fillRoundRect(textX - 30, y - 40, textWidth + 60, 60, 20, 20);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(new Color(255, 255, 255));
            
            g.drawString(">", textX - 50, y);
            g.drawString("<", textX + textWidth + 30, y);
        } else {
            g.setFont(new Font("Arial", Font.PLAIN, 32));
            g.setColor(new Color(180, 180, 180));
        }

        g.drawString(text, textX, y);
    }
    public void moveUp() {
        selectedOption--;
        if (selectedOption < 0) {
            selectedOption = TOTAL_OPTIONS - 1;
        }
    }

    public void moveDown() {
        selectedOption++;
        if (selectedOption >= TOTAL_OPTIONS) {
            selectedOption = 0;
        }
    }
    public boolean isStartSelected() {
        return selectedOption == OPTION_START;
    }

    public boolean isExitSelected() {
        return selectedOption == OPTION_EXIT;
    }

    public void updateHighestLevel(int level) {
        if (level > highestLevel) {
            highestLevel = level;
            saveHighestLevel(level);
        }
    }
    private int loadHighestLevel() {
        try {
            java.io.File file = new java.io.File("highscore.dat");
            if (file.exists()) {
                java.util.Scanner scanner = new java.util.Scanner(file);
                if (scanner.hasNextInt()) {
                    int level = scanner.nextInt();
                    scanner.close();
                    return level;
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.err.println("Error loading high score: " + e.getMessage());
        }
        return 1;
    }

    private void saveHighestLevel(int level) {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter("highscore.dat");
            writer.println(level);
            writer.close();
        } catch (Exception e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }
}
