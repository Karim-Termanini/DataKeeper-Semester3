package entities;

import utils.AnimationManager;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameCharacter implements Character {
    protected BufferedImage[][] animations;
    protected int currentAction = 0;
    protected int animationIndex = 0;
    protected int animationTick = 0;
    protected int animationSpeed = 10;
    protected boolean facingRight = true;

    protected boolean isGrounded = true;
    protected boolean attacking = false;
    protected boolean movingLeft = false;
    protected boolean movingRight = false;
    protected boolean isAlive = true;

    protected float x, y;
    // CORRECTED: Added the target variable to the highest-level abstract class
    protected Player target;

    @Override
    public abstract void loadAnimations();

    @Override
    public abstract void attack();

    @Override
    public abstract void specialAttack();

    @Override
    public abstract void moveLeft();

    @Override
    public abstract void moveRight();

    @Override
    public abstract void jump();

    @Override
    public abstract void stop();

    @Override
    public abstract float getSpeed();

    public abstract boolean isMoving();

    @Override
    public abstract boolean isAttacking();

    @Override
    public abstract int getAttackDamage();

    @Override
    public abstract void takeDamage(int damage);

    @Override
    public abstract int getWidth();

    @Override
    public abstract int getHeight();

    @Override
    public abstract int getHealth();

    @Override
    public void update() {}

    @Override
    public void render(Graphics g) {
    }

    @Override
    public void setAction(int action) {
        if (this.currentAction != action) {
            this.currentAction = action;
            animationIndex = 0;
            animationTick = 0;
        }
    }

    @Override
    public void updateAnimation() {
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= getCurrentAnimationLength()) {
                handleAnimationCompletion();
            }
        }
    }

    @Override
    public BufferedImage getCurrentFrame() {
        if (animations == null || animations[currentAction] == null) {
            return null;
        }
        if (animationIndex >= animations[currentAction].length) {
            animationIndex = 0;
        }
        return animations[currentAction][animationIndex];
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public int getX() {
        return (int)x;
    }

    @Override
    public int getY() {
        return (int)y;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    protected void handleAnimationCompletion() {
        animationIndex = 0;
    }

    protected int getCurrentAnimationLength() {
        if (animations == null || animations[currentAction] == null) {
            return 0;
        }
        return animations[currentAction].length;
    }

    protected BufferedImage flipImageHorizontally(BufferedImage image) {
        return AnimationManager.flipImageHorizontally(image);
    }
    public abstract Rectangle getHitbox();
}
