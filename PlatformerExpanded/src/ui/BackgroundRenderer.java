/*
package ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class BackgroundRenderer {
    private BufferedImage[] flowFrames;
    private int currentFrame;
    private int animationTick;
    private int animationSpeed;
    private boolean loaded;

    public BackgroundRenderer() {
        this.currentFrame = 0;
        this.animationTick = 0;
        this.animationSpeed = 8;
        this.loaded = false;
        loadBackgroundFrames();
    }

    private void loadBackgroundFrames() {
        flowFrames = new BufferedImage[6];
        int loadedCount = 0;
        for (int i = 1; i <= 6; i++) {
            String path = String.format("/backgrounds/2/Flow%02d.png", i);
            flowFrames[i - 1] = ImageUtils.loadImage(path, getClass());
            if (flowFrames[i - 1] != null) {
                loadedCount++;
                System.out.println("✅ Loaded background: " + path);
            } else {
                System.err.println("❌ Background not found: " + path);
            }
        }

        loaded = (loadedCount > 0);
        if (!loaded) {
            System.err.println("⚠️ No background frames loaded, using solid color");
        }
    }

    public void update() {
        if (!loaded) return;

        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            currentFrame++;
            if (currentFrame >= flowFrames.length) {
                currentFrame = 0;
            }
        }
    }

    public void render(Graphics g, int width, int height) {
        if (!loaded || flowFrames[currentFrame] == null) {
            // Fallback to solid color
            g.setColor(new Color(20, 20, 40));
            g.fillRect(0, 0, width, height);
            return;
        }

        // Draw tiled background
        BufferedImage frame = flowFrames[currentFrame];
        int imgWidth = frame.getWidth();
        int imgHeight = frame.getHeight();

        // Tile the background to cover the entire area
        for (int y = 0; y < height; y += imgHeight) {
            for (int x = 0; x < width; x += imgWidth) {
                g.drawImage(frame, x, y, null);
            }
        }
    }

    public boolean isLoaded() {
        return loaded;
    }
}
*/
