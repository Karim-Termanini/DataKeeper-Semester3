package entities;

import utils.AnimationManager;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Player extends GameCharacter {
    // === Level Constants ===
    private static final int LEVEL_WIDTH = Constants.LEVEL_WIDTH;
    private static final int LEVEL_HEIGHT = 1000;

    private final int maxHealth = 200;
    private int health = maxHealth;

    // === Physics & Movement ===
    private final float gravity = 0.4f;
    private final float jumpSpeed = -14.0f;
    private static final int GROUND_LEVEL = Constants.GROUND_LEVEL;
    private float airSpeed = 0;
    private int jumpsLeft = 2;

    // === Dash System (MODIFIED) ===
    private boolean isDashing = false;
    private final float dashSpeed = 30.0f;
    private final int dashDuration = 15;
    private int dashCounter = 0;

    // === Protection Systems ===
    private boolean isJumpingOverEnemy = false;
    private int jumpOverProtectionFrames = 0;
    private final int JUMP_OVER_PROTECTION_DURATION = 40;
    private boolean isAttackingProtected = false;
    private int attackProtectionFrames = 0;
    private final int ATTACK_PROTECTION_DURATION = 15;

    // === Combo System ===
    private int comboCount = 0;
    private long lastAttackTime = 0;
    private final int COMBO_TIME_WINDOW = 800;
    private boolean comboHitEnemy = false;

    public Player() {
        loadAnimations();
        x = 100;
        y = GROUND_LEVEL;
        animationSpeed = 10;
    }

    @Override
    public void loadAnimations() {
        animations = new BufferedImage[9][];
        animations[Constants.PlayerActions.IDLE] = loadAnimationFrames("idle", 8);
        animations[Constants.PlayerActions.RUN] = loadAnimationFrames("run", 8);
        animations[Constants.PlayerActions.JUMP] = loadAnimationFrames("jump", 5);
        animations[Constants.PlayerActions.HIT] = loadAnimationFrames("hit", 4);
        animations[Constants.PlayerActions.DEATH] = loadAnimationFrames("death", 10);
        animations[Constants.PlayerActions.AIR_ATTACK] = loadAnimationFrames("air_attack", 6);
        animations[Constants.PlayerActions.COMBO] = loadAnimationFrames("combo", 19);
        animations[Constants.PlayerActions.DASH] = loadAnimationFrames("dash", 6);
        animations[Constants.PlayerActions.SLIDE] = loadAnimationFrames("slide", 12);
    }

    private BufferedImage[] loadAnimationFrames(String folderName, int frameCount) {
        BufferedImage[] frames = new BufferedImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            String filePath = String.format("/Fighter sprites/%s/fighter_%s_%04d.png", folderName, folderName, i + 1);
            try (InputStream is = getClass().getResourceAsStream(filePath)) {
                if (is != null) {
                    frames[i] = ImageIO.read(is);
                } else {
                    System.err.println("❌ Player animation not found: " + filePath);
                }
            } catch (Exception e) {
                System.err.println("❌ Error loading player animation: " + filePath);
            }
        }
        return frames;
    }

    @Override
    public void update() {
        updatePlayerState();
        updatePosition();
        updateAnimation();
    }

    private void updatePlayerState() {
        if (isDashing || isAttacking() || currentAction == Constants.PlayerActions.DEATH) {
            return;
        }

        int newState = Constants.PlayerActions.IDLE;
        if (!isGrounded) {
            newState = Constants.PlayerActions.JUMP;
        } else if (movingLeft || movingRight) {
            newState = Constants.PlayerActions.RUN;
        }
        setAction(newState);
    }

    private void updatePosition() {
        if (isDashing) {
            if (facingRight) x += dashSpeed;
            else x -= dashSpeed;
            dashCounter++;
            if (dashCounter >= dashDuration) isDashing = false;
        } else if (currentAction == Constants.PlayerActions.SLIDE) {
            if (facingRight) x += 10f;
            else x -= 10f;
        } else {
            if (movingLeft) x -= getSpeed();
            if (movingRight) x += getSpeed();
        }

        if (jumpOverProtectionFrames > 0) {
            jumpOverProtectionFrames--;
            if (jumpOverProtectionFrames == 0) isJumpingOverEnemy = false;
        }
        if (attackProtectionFrames > 0) {
            attackProtectionFrames--;
            if (attackProtectionFrames == 0) isAttackingProtected = false;
        }

        if (!isGrounded) {
            y += airSpeed;
            airSpeed += gravity;
            if (y >= GROUND_LEVEL) {
                y = GROUND_LEVEL;
                isGrounded = true;
                jumpsLeft = 2;
                airSpeed = 0;
                isJumpingOverEnemy = false;
            }
        }
        
        x = Math.max(0, Math.min(x, LEVEL_WIDTH - getWidth()));
        y = Math.max(0, Math.min(y, LEVEL_HEIGHT - getHeight()));
    }

    @Override
    public void render(Graphics g) {
        BufferedImage currentFrame = getCurrentFrame();
        if (currentFrame != null) {
            BufferedImage sub = AnimationManager.cropImage(currentFrame, 120, 160, 250, 250);
            if (!facingRight) {
                sub = flipImageHorizontally(sub);
            }
            g.drawImage(sub, (int)x, (int)y, 140, 140, null);
            drawHealthBar(g);
        } else {
            drawFallbackPlayer(g);
        }
    }

    @Override
    public void attack() {
        if (isAlive && !attacking && isGrounded) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttackTime < COMBO_TIME_WINDOW && comboHitEnemy) {
                comboCount++;
            } else {
                comboCount = 1;
            }
            lastAttackTime = currentTime;
            comboHitEnemy = false;
            attacking = true;
            isAttackingProtected = true;
            attackProtectionFrames = ATTACK_PROTECTION_DURATION;
            setAction(Constants.PlayerActions.COMBO);
        }
    }

    @Override
    public void specialAttack() {
        if (isAlive && !attacking && !isGrounded) {
            attacking = true;
            setAction(Constants.PlayerActions.AIR_ATTACK);
        }
    }

    @Override
    public void jump() {
        if (jumpsLeft > 0 && isAlive) {
            airSpeed = jumpSpeed;
            jumpsLeft--;
            isGrounded = false;
            setAction(Constants.PlayerActions.JUMP);
        }
    }

    public void dash() {
        if (isAlive && !attacking && !isDashing) {
            isDashing = true;
            dashCounter = 0;
            setAction(Constants.PlayerActions.DASH);
        }
    }

    public void slide() {
        if (isAlive && isGrounded && !attacking && currentAction != Constants.PlayerActions.SLIDE) {
            setAction(Constants.PlayerActions.SLIDE);
        }
    }

    @Override
    public void moveLeft() { movingLeft = true; facingRight = false; }
    @Override
    public void moveRight() { movingRight = true; facingRight = true; }
    @Override
    public void stop() { movingLeft = false; movingRight = false; }

    @Override
    protected void handleAnimationCompletion() {
        switch (currentAction) {
            case Constants.PlayerActions.COMBO,
                 Constants.PlayerActions.AIR_ATTACK -> {
                attacking = false;
                setAction(Constants.PlayerActions.IDLE);
            }
            case Constants.PlayerActions.DASH -> {
                isDashing = false;
                setAction(Constants.PlayerActions.IDLE);
            }
            case Constants.PlayerActions.SLIDE -> {
                if (movingLeft || movingRight) setAction(Constants.PlayerActions.RUN);
                else setAction(Constants.PlayerActions.IDLE);
            }
            case Constants.PlayerActions.DEATH -> {
                animationIndex = animations[Constants.PlayerActions.DEATH].length - 1;
                isAlive = false;
            }
            default -> animationIndex = 0;
        }
    }

    @Override
    public void setAction(int action) {
        if (this.currentAction == action) return;
        this.currentAction = action;
        this.animationIndex = 0;
        this.animationTick = 0;

        animationSpeed = switch (action) {
            case Constants.PlayerActions.COMBO, Constants.PlayerActions.DASH -> 5;
            case Constants.PlayerActions.SLIDE -> 7;
            case Constants.PlayerActions.JUMP -> 12;
            case Constants.PlayerActions.IDLE -> 25;
            case Constants.PlayerActions.DEATH -> 15;
            default -> 10; // RUN, HIT, AIR_ATTACK
        };
    }

    @Override
    public void takeDamage(int damage) {
        if (!isAlive || isJumpingOverEnemy || isAttackingProtected) return;
        health -= Math.max(1, damage - 5);
        audio.SoundManager.getInstance().playSound("hurt"); // SOUND ADDED

        if (health > 0) {
            setAction(Constants.PlayerActions.HIT);
        } else {
            if (isAlive) { // Play death sound only once
                health = 0;
                isAlive = false;
                setAction(Constants.PlayerActions.DEATH);
                audio.SoundManager.getInstance().playSound("player_death"); // SOUND ADDED
            }
        }
    }

    public void heal(int amount) {
        if (!isAlive) return;
        health = Math.min(health + amount, maxHealth);
    }

    public Rectangle getAttackHitbox() {
        int attackRange = 100;
        int attackHeight = 100;
        if (facingRight) {
            return new Rectangle((int)x + getWidth() - 20, (int)y + 20, attackRange, attackHeight);
        } else {
            return new Rectangle((int)x - attackRange + 20, (int)y + 20, attackRange, attackHeight);
        }
    }

    public boolean isCollidingWith(Enemy enemy) {
        return getHitbox().intersects(enemy.getHitbox());
    }

    public boolean isAttackingProtected() {
        return isAttackingProtected;
    }
    
    public boolean isAttackingEnemy(Enemy enemy) {
        if (!isAttacking()) return false;
        boolean isHitting = getAttackHitbox().intersects(enemy.getHitbox());
        if (isHitting) comboHitEnemy = true;
        return isHitting;
    }

    public void activateJumpOverProtection() {
        isJumpingOverEnemy = true;
        jumpOverProtectionFrames = JUMP_OVER_PROTECTION_DURATION;
    }

    public boolean isAboveEnemy(Enemy enemy) {
        return getHitbox().y + getHitbox().height < enemy.getHitbox().y + 60 && getHitbox().intersects(enemy.getHitbox());
    }

    public int getCurrentAction() { return currentAction; }
    public boolean hasJumpOverProtection() { return isJumpingOverEnemy && jumpOverProtectionFrames > 0; }
    
    @Override public float getSpeed() { return 7.0f; } // Increased speed

    @Override public boolean isMoving() { return movingLeft || movingRight; }
    @Override public int getWidth() { return 140; }
    @Override public int getHeight() { return 140; }
    @Override public int getHealth() { return health; }
    @Override public boolean isAttacking() { return attacking || currentAction == Constants.PlayerActions.COMBO || currentAction == Constants.PlayerActions.AIR_ATTACK || currentAction == Constants.PlayerActions.SLIDE; }
    @Override public int getAttackDamage() {
        return switch (currentAction) {
            case Constants.PlayerActions.COMBO -> (comboCount >= 3) ? 35 : ((comboCount == 2) ? 25 : 20);
            case Constants.PlayerActions.AIR_ATTACK -> 30;
            case Constants.PlayerActions.SLIDE -> 10;
            default -> 5;
        };
    }
    @Override public Rectangle getHitbox() { return new Rectangle((int)x + 30, (int)y + 20, getWidth() - 60, getHeight() - 40); }

    private void drawHealthBar(Graphics g) {
        int barWidth = 150;
        int barHeight = 12;
        int healthWidth = (barWidth * health) / maxHealth;
        if (health > maxHealth * 0.6) g.setColor(Color.GREEN);
        else if (health > maxHealth * 0.3) g.setColor(Color.YELLOW);
        else g.setColor(Color.RED);
        g.fillRect(getX(), getY() - 15, healthWidth, barHeight);
        g.setColor(Color.WHITE);
        g.drawRect(getX(), getY() - 15, barWidth, barHeight);
    }

    private void drawFallbackPlayer(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect((int)x, (int)y, 140, 140);
        g.setColor(Color.WHITE);
        g.drawString("PLAYER", (int)x + 40, (int)y + 70);
        drawHealthBar(g);
    }
}
