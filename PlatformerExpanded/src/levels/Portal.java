package levels;

import entities.Player;
import java.awt.*;

public class Portal {
    private int x, y;
    private int width;
    private int height;
    private boolean active;
    private int animationTick;
    private float glowIntensity;
    private boolean glowIncreasing;

    public Portal(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 150;
        this.active = false;
        this.animationTick = 0;
        this.glowIntensity = 0.3f;
        this.glowIncreasing = true;
    }

    public void activate() {
        active = true;
    }

//    public void deactivate() {
//        active = false;
//    }

    public void update() {
        if (!active) return;

        animationTick++;

        // Pulsing glow effect
        if (glowIncreasing) {
            glowIntensity += 0.02f;
            if (glowIntensity >= 1.0f) {
                glowIntensity = 1.0f;
                glowIncreasing = false;
            }
        } else {
            glowIntensity -= 0.02f;
            if (glowIntensity <= 0.3f) {
                glowIntensity = 0.3f;
                glowIncreasing = true;
            }
        }
    }

    public void render(Graphics g) {
        if (!active) return;

        Graphics2D g2d = (Graphics2D) g;

        // Outer glow layers
        for (int i = 5; i > 0; i--) {
            int alpha = (int)(50 * glowIntensity * (i / 5.0f));
            g2d.setColor(new java.awt.Color(100, 200, 255, alpha));
            int offset = i * 15;
            g2d.fillOval(x - offset, y - offset, width + offset * 2, height + offset * 2);
        }

        // Main portal body
        GradientPaint gradient = new GradientPaint(
            x, y, new java.awt.Color(50, 150, 255, 200),
            x, y + height, new java.awt.Color(150, 200, 255, 150)
        );
        g2d.setPaint(gradient);
        g2d.fillOval(x, y, width, height);

        // Inner bright core
        int coreAlpha = (int)(255 * glowIntensity);
        g2d.setColor(new java.awt.Color(200, 230, 255, coreAlpha));
        g2d.fillOval(x + 20, y + 30, width - 40, height - 60);

        // Swirling particles
        for (int i = 0; i < 8; i++) {
            double angle = (animationTick + i * 45) * 0.05;
            int radius = 40;
            int px = x + width / 2 + (int)(Math.cos(angle) * radius);
            int py = y + height / 2 + (int)(Math.sin(angle) * radius);
            int particleAlpha = (int)(150 * glowIntensity);
            g2d.setColor(new java.awt.Color(255, 255, 255, particleAlpha));
            g2d.fillOval(px - 3, py - 3, 6, 6);
        }

        // Text prompt
        g2d.setColor(java.awt.Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String text = "Press E to Enter";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, x + (width - textWidth) / 2, y + height + 25);
    }

    public boolean checkPlayerCollision(Player player) {
        if (!active) return false;

        Rectangle portalBounds = new Rectangle(x, y, width, height);
        Rectangle playerBounds = new Rectangle(
            player.getX() + 30,
            player.getY() + 20,
            player.getWidth() - 60,
            player.getHeight() - 40
        );

        return portalBounds.intersects(playerBounds);
    }

//    public boolean isActive() {
//        return active;
//    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

//    public void setPosition(int x, int y) {
//        this.x = x;
//        this.y = y;
//    }
}
