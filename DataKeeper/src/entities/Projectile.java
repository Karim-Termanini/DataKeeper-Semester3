package entities;

import java.awt.*;

public class Projectile {
    private float x, y;
    private final float velocityX, velocityY;
    private final int damage;
    private int lifetime;
    private final int maxLifetime;
    private boolean active;
    private final int radius;

    public Projectile(float startX, float startY, float targetX, float targetY, int damage) {
        this.x = startX;
        this.y = startY;
        this.damage = damage;
        this.maxLifetime = 180; // 1.5 seconds at 120 FPS
        this.lifetime = 0;
        this.active = true;
        this.radius = 8;

        // Calculate direction vector
        float dx = targetX - startX;
        float dy = targetY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Normalize and set speed
        float speed = 8.0f;
        if (distance > 0) {
            this.velocityX = (dx / distance) * speed;
            this.velocityY = (dy / distance) * speed;
        } else {
            this.velocityX = speed;
            this.velocityY = 0;
        }
    }

    public void update() {
        if (!active) return;

        x += velocityX;
        y += velocityY;
        lifetime++;

        if (lifetime >= maxLifetime) {
            active = false;
        }

        // Check bounds
        if (x < 0 || x > 2000 || y < 0 || y > 1000) {
            active = false;
        }
    }

    public void render(Graphics g) {
        if (!active) return;

        // Draw projectile as glowing orb
        Graphics2D g2d = (Graphics2D) g;
        
        // Outer glow
        g2d.setColor(new Color(255, 100, 0, 100));
        g2d.fillOval((int)x - radius - 4, (int)y - radius - 4, (radius + 4) * 2, (radius + 4) * 2);
        
        // Middle layer
        g2d.setColor(new Color(255, 150, 0, 180));
        g2d.fillOval((int)x - radius - 2, (int)y - radius - 2, (radius + 2) * 2, (radius + 2) * 2);
        
        // Core
        g2d.setColor(new Color(255, 200, 0, 255));
        g2d.fillOval((int)x - radius, (int)y - radius, radius * 2, radius * 2);
        
        // Bright center
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)x - radius/2, (int)y - radius/2, radius, radius);
    }

//    public Rectangle getHitbox() {
//        return new Rectangle((int)x - radius, (int)y - radius, radius * 2, radius * 2);
//    }

//    public boolean isActive() {
//        return active;
//    }

//    public void deactivate() {
//        active = false;
//    }

    public int getDamage() {
        return damage;
    }

    public int getX() {
        return (int)x;
    }

    public int getY() {
        return (int)y;
    }
}
