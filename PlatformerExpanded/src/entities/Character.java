package entities;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

public interface Character {
    // القسم الأول: دوال التحديث والعرض
    void update();
    void render(Graphics g);

    // === الحركة والتحكم ===
    void moveLeft();
    void moveRight();
    void jump();
    void stop();
    float getSpeed();
//    boolean isMoving();

    // === النظام القتالي ===
    void attack();
    void specialAttack();
    boolean isAttacking();
    int getAttackDamage();
    void takeDamage(int damage); // 🔥 أضف هذه السطر

    // === الأنيميشن ===
    void loadAnimations();
    void setAction(int action);
    void updateAnimation();
    BufferedImage getCurrentFrame();

    // === الحالة والمعلومات ===
//    boolean isGrounded();
//    boolean isFacingRight();
    boolean isAlive();
    int getHealth();

    // === الموقع والأبعاد ===
    int getX();
    int getY();
    void setPosition(int x, int y);
    int getWidth();
    int getHeight();
}