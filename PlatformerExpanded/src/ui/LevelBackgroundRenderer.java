package ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class LevelBackgroundRenderer {
    private BufferedImage level1Background;
    private BufferedImage level2Background;
    private BufferedImage level3Background;
    private int currentLevel;
    private boolean loaded;

    public LevelBackgroundRenderer() {
        this.currentLevel = 1;
        this.loaded = false;
        loadBackgrounds();
    }

    private void loadBackgrounds() {
        String level1Path = "/level1-Backgrounds-new/level1.png";
        level1Background = ImageUtils.loadImage(level1Path, getClass());
        if (level1Background != null) {
            System.out.println("✅ Loaded level 1 background");
        } else {
            System.err.println("❌ Level 1 background not found: " + level1Path);
        }

        String level2Path = "/level2-Backgrounds-new/level2.png";
        level2Background = ImageUtils.loadImage(level2Path, getClass());
        if (level2Background != null) {
            System.out.println("✅ Loaded level 2 background");
        } else {
            System.err.println("❌ Level 2 background not found: " + level2Path);
        }

        String level3Path = "/level3-Backgrounds-new/level3.png";
        level3Background = ImageUtils.loadImage(level3Path, getClass());
        if (level3Background != null) {
            System.out.println("✅ Loaded level 3 background");
        } else {
            System.err.println("❌ Level 3 background not found: " + level3Path);
        }

        loaded = (level1Background != null || level2Background != null || level3Background != null);
    }

    public void setLevel(int level) {
        this.currentLevel = level;
    }

    public void render(Graphics g, int width, int height, int cameraX, int cameraY) {
        BufferedImage bg = null;
        
        if (currentLevel == 1) {
            bg = level1Background;
        } else if (currentLevel == 2) {
            bg = level2Background;
        } else if (currentLevel == 3) {
            bg = level3Background;
        }

        if (bg == null) {
            g.setColor(new Color(20, 20, 40));
            g.fillRect(0, 0, width, height);
            return;
        }

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
