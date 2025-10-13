package entities;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

public interface Character {
    void update();
    void render(Graphics g);
    void moveLeft();
    void moveRight();
    void jump();
    void stop();
    float getSpeed();
    void attack();
    void specialAttack();
    boolean isAttacking();
    int getAttackDamage();
    void takeDamage(int damage);
    void loadAnimations();
    void setAction(int action);
    void updateAnimation();
    BufferedImage getCurrentFrame();
    boolean isAlive();
    int getHealth();
    int getX();
    int getY();
    void setPosition(int x, int y);
    int getWidth();
    int getHeight();
}