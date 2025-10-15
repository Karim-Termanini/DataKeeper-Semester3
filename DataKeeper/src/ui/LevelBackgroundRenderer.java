package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Font;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class LevelBackgroundRenderer {
    private BufferedImage level1Background;
    private BufferedImage level2Background;
    private BufferedImage level3Background;
    private BufferedImage bossBackground;
    private int currentLevel;
    private boolean loaded;
    // animation tick for procedural backgrounds
    private int tick = 0;

    public LevelBackgroundRenderer() {
        this.currentLevel = 1;
        this.loaded = false;
        loadBackgrounds();
    }

    private void loadBackgrounds() {
        String level1Path = "/level1-Backgrounds-new/level1.png";
        level1Background = ImageUtils.loadImage(level1Path, getClass());
        if (level1Background != null) {
            if (utils.Constants.DEBUG_LOGS) System.out.println("✅ Loaded level 1 background");
        } else {
            if (utils.Constants.DEBUG_LOGS) System.err.println("❌ Level 1 background not found: " + level1Path);
        }

        String level2Path = "/level2-Backgrounds-new/level2.png";
        level2Background = ImageUtils.loadImage(level2Path, getClass());
        if (level2Background != null) {
            if (utils.Constants.DEBUG_LOGS) System.out.println("✅ Loaded level 2 background");
        } else {
            if (utils.Constants.DEBUG_LOGS) System.err.println("❌ Level 2 background not found: " + level2Path);
        }

        String level3Path = "/level3-Backgrounds-new/level3.png";
        level3Background = ImageUtils.loadImage(level3Path, getClass());
        if (level3Background != null) {
            if (utils.Constants.DEBUG_LOGS) System.out.println("✅ Loaded level 3 background");
        } else {
            if (utils.Constants.DEBUG_LOGS) System.err.println("❌ Level 3 background not found: " + level3Path);
        }

        String bossPath = "/level4-Backgrounds/boss.png";
        bossBackground = ImageUtils.loadImage(bossPath, getClass());
        if (bossBackground != null) {
            if (utils.Constants.DEBUG_LOGS) System.out.println("✅ Loaded boss background");
        } else {
            if (utils.Constants.DEBUG_LOGS) System.err.println("❌ Boss background not found: " + bossPath);
        }

        loaded = (level1Background != null || level2Background != null || level3Background != null);
    }

    public void setLevel(int level) {
        this.currentLevel = level;
    }

    public void render(Graphics g, int width, int height, int cameraX, int cameraY) {
        tick++;
        int level = currentLevel;
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage bg = switch (level) {
            case 1 -> level1Background;
            case 2 -> level2Background;
            case 3 -> level3Background;
            case 4 -> bossBackground;
            default -> level3Background;
        };
        if (bg != null) {
            g2.drawImage(bg, 0, 0, width, height, null);
        } else if (level == 4) {
            // Boss fallback background: digital-themed procedural render
            Paint old = g2.getPaint();
            // 1) Dark gradient base
            java.awt.geom.Point2D center = new java.awt.geom.Point2D.Float(width/2f, height/2f);
            float[] dist = {0f, 1f};
            Color[] colors = {new Color(6,8,12), new Color(1,2,4)};
            java.awt.RadialGradientPaint rgp = new java.awt.RadialGradientPaint(center, Math.max(width,height)/1.1f, dist, colors);
            g2.setPaint(rgp);
            g2.fillRect(0,0,width,height);
            g2.setPaint(old);

            // 2) Subtle grid
            g2.setColor(new Color(30, 60, 90, 60));
            int grid = 40;
            for (int x = 0; x < width; x += grid) g2.drawLine(x, 0, x, height);
            for (int y = 0; y < height; y += grid) g2.drawLine(0, y, width, y);

            // 3) Scanlines
            g2.setColor(new Color(0, 255, 180, 20));
            int scanOffset = (tick / 6) % 3; // slower
            for (int y = scanOffset; y < height; y += 3) g2.drawLine(0, y, width, y);

            // 4) Code rain columns
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            String hex = "0123456789ABCDEF";
            int speed = 1; // 1 px per frame
            for (int col = 0; col < width; col += 140) {
                int startX = col + 20 + (col % 60);
                int len = 10;
                int baseY = (height + (tick * speed) + (col % 200)) % height;
                for (int i = 0; i < len; i++) {
                    int y = (baseY + i * 18) % height;
                    int alpha = 140 - i * 10;
                    if (alpha < 0) alpha = 0;
                    g2.setColor(new Color(120, 255, 200, alpha));
                    char c = hex.charAt((i + (tick / 10) + (col / 140)) % hex.length());
                    g2.drawString(String.valueOf(c), startX, y);
                }
            }
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0,0,width,height);
        }

    if (bg == null) return; // already drew fallback

        // Parallax scrolling effect (background moves slower than camera)
        int bgX = -(cameraX / 3);
        int bgY = -(cameraY / 3);

        g.drawImage(bg, bgX, bgY, width, height, null);
    }
    public boolean isLoaded() {
        return loaded;
    }
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
    public BufferedImage getLevel1Background() {
        return level1Background;
    }
    public BufferedImage getLevel2Background() {
        return level2Background;
    }
    public BufferedImage getLevel3Background() {
        return level3Background;
    }
    public int getCurrentLevel() {
        return currentLevel;
    }
    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
    public void setLevel1Background(BufferedImage level1Background) {
        this.level1Background = level1Background;
    }
    public void setLevel2Background(BufferedImage level2Background) {
        this.level2Background = level2Background;
    }
    public void setLevel3Background(BufferedImage level3Background) {
        this.level3Background = level3Background;
    }
}
