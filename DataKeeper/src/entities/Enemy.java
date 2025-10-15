package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import utils.Constants;

// FINAL VERSION: A single, concrete Enemy class using the user's proven logic and requests.
public class Enemy extends GameCharacter {

    private int health = 100;
    private boolean isHit = false;
    private int hitCooldown = 0;
    public int attackCooldown = 0;
    private final int BASE_ATTACK_DAMAGE = 10;
    private final float BASE_SPEED = 1.5f;
    private boolean isInvincible = false;
    private int invincibilityFrames = 0;
    private final int INVINCIBILITY_DURATION = 30; // 0.5 seconds

    public Enemy(int startX, Player target) {
        super();
        this.target = target;
        this.x = startX;
        this.y = Constants.GROUND_LEVEL;
        this.isAlive = true;
        this.facingRight = (target != null) ? (target.getX() > this.x) : true;
        loadAnimations();
    }

    // Reset all runtime state so pooled enemies spawn as fresh units
    public void reset(int startX, Player target) {
        this.x = startX;
        this.y = Constants.GROUND_LEVEL;
        this.target = target;
        this.isAlive = true;
        this.health = utils.Constants.Enemy.HEALTH;
        this.currentAction = utils.Constants.EnemyActions.IDLE;
        this.animationIndex = 0;
        this.animationTick = 0;
        this.animationSpeed = 15;
        this.attacking = false;
        this.isGrounded = true;
        this.movingLeft = false;
        this.movingRight = false;
        this.isHit = false;
        this.hitCooldown = 0;
        this.attackCooldown = 0;
        this.isInvincible = false;
        this.invincibilityFrames = 0;
        this.facingRight = (target != null) ? (target.getX() > this.x) : true;
    }

    @Override
    public void loadAnimations() {
        animations = new BufferedImage[5][];
        animations[Constants.EnemyActions.IDLE] = loadFrames("idle", 8);
        animations[Constants.EnemyActions.WALK] = loadFrames("run", 8);
        animations[Constants.EnemyActions.FIGHT] = loadFrames("fight", 5);
        animations[Constants.EnemyActions.DEATH] = loadFrames("death", 6);
        animations[Constants.EnemyActions.HIT] = loadFrames("hit", 4);
    }

    private BufferedImage[] loadFrames(String name, int count) {
        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            String path = String.format("/Enemy/glitsoul/%s/shardsoul_%s_%04d.png", name, name, i + 1);
            try {
                java.io.File file = new java.io.File("res" + path);
                if (file.exists()) {
                    frames[i] = ImageIO.read(file);
                } else {
                    try (InputStream is = getClass().getResourceAsStream(path)) {
                        if (is != null) {
                            frames[i] = ImageIO.read(is);
                        } else if (utils.Constants.DEBUG_LOGS) {
                            System.err.println("❌ Enemy animation not found: " + path);
                        }
                    }
                }
            } catch (Exception e) {
                if (utils.Constants.DEBUG_LOGS) {
                    System.err.println("❌ Error loading enemy animation: " + path);
                }
            }
        }
        return frames;
    }

    @Override
    public void update() {
        if (!isAlive) {
            if (currentAction == Constants.EnemyActions.DEATH) updateAnimation();
            return;
        }

        if (hitCooldown > 0) hitCooldown--;
        if (attackCooldown > 0) attackCooldown--;
        if (invincibilityFrames > 0) {
            invincibilityFrames--;
            if (invincibilityFrames == 0) isInvincible = false;
        }

        if (isHit) {
            setAction(Constants.EnemyActions.HIT);
        } else {
            followPlayer();
        }

        updateAnimation();
        updatePosition();
    }


    // CORRECTED AI LOGIC from user's proven logic and requests.
    protected void followPlayer() {
        // 1. If we shouldn't be moving, stop and stand idle.
        if (currentAction == Constants.EnemyActions.FIGHT) {
            return; // Don't interrupt an ongoing attack
        }
        if (target == null || !target.isAlive()) {
            setAction(Constants.EnemyActions.IDLE);
            return;
        }

        // 2. Calculate direction to player
        float dx = target.getX() - this.getX();
        float dir = Math.signum(dx);
        this.facingRight = dir > 0;

        // 3. Check if we are in attack range.
        float distance = Math.abs(dx);
        if (distance <= Constants.ATTACK_RANGE) {
            // If ready to attack, then attack.
            if (attackCooldown == 0) {
                attack(); // This will set the action to FIGHT
            } else {
                // If on cooldown, just stand idle.
                setAction(Constants.EnemyActions.IDLE);
            }
        } else {
            // 4. Move towards the player, but avoid going out of bounds.
            float newX = this.x + dir * this.getSpeed();
            if (newX >= 0 && newX <= Constants.LEVEL_WIDTH - getWidth()) {
                this.x = newX;
                setAction(Constants.EnemyActions.WALK);
            } else {
                // If movement would go out of bounds, stand idle.
                setAction(Constants.EnemyActions.IDLE);
            }
        }
    }

    private void updatePosition() {
        // Clamp position to level bounds (already handled in followPlayer, but keep as safety)
        if (x < 0) x = 0;
        if (x > Constants.LEVEL_WIDTH - getWidth()) x = Constants.LEVEL_WIDTH - getWidth();
    }

    @Override
    public void render(Graphics g) {
        if (currentAction == Constants.EnemyActions.DEATH && animationIndex >= animations[Constants.EnemyActions.DEATH].length - 1) return;

        BufferedImage frame = getCurrentFrame();
        if (frame != null) {
            BufferedImage displayImage = frame;
            if (!facingRight) displayImage = flipImageHorizontally(frame);
            g.drawImage(displayImage, getX(), getY(), 140, 140, null);
            if (isAlive) drawHealthBar(g);
        } else {
            drawFallback(g);
        }
    }

    @Override
    public void takeDamage(int damage) {
        if (!isAlive || isInvincible) return;
        health -= damage;
        isHit = true;
        isInvincible = true;
        invincibilityFrames = INVINCIBILITY_DURATION;
        hitCooldown = Constants.HIT_COOLDOWN;
        setAction(Constants.EnemyActions.HIT);
        audio.SoundManager.getInstance().playSound("hit");

        if (health <= 0 && isAlive) {
            health = 0;
            isAlive = false;
            setAction(Constants.EnemyActions.DEATH);
            audio.SoundManager.getInstance().playSound("enemy_death");
        }
    }

    @Override
    public void attack() {
        attackCooldown = Constants.ATTACK_COOLDOWN;
        setAction(Constants.EnemyActions.FIGHT);
    }

    @Override
    protected void handleAnimationCompletion() {
        if (currentAction == Constants.EnemyActions.FIGHT || currentAction == Constants.EnemyActions.HIT) {
            isHit = false;
            setAction(Constants.EnemyActions.IDLE);
        } else if (currentAction == Constants.EnemyActions.DEATH) {
            // Stays dead
        } else {
            animationIndex = 0;
        }
    }

    private void drawHealthBar(Graphics g) {
        int barWidth = 60;
        int barHeight = 5;
        int healthWidth = (int) ((barWidth * health) / 100.0);
        g.setColor(Color.RED);
        g.fillRect(getX(), getY() - 10, barWidth, barHeight);
        g.setColor(Color.GREEN);
        g.fillRect(getX(), getY() - 10, healthWidth, barHeight);
    }

    private void drawFallback(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y, 140, 140);
    }

    // Implementation of abstract/interface methods
    @Override public int getHealth() { return health; }
    @Override public int getWidth() { return 140; }
    @Override public int getHeight() { return 140; }
    @Override public boolean isAttacking() { return currentAction == Constants.EnemyActions.FIGHT; }
    @Override public Rectangle getHitbox() { return new Rectangle((int)x + 30, (int)y + 20, getWidth() - 60, getHeight() - 40); }
    /**
     * Liefert die Angriffs-Trefferbox des Gegners relativ zur aktuellen Blickrichtung.
     * Sie liegt direkt neben der Basis-Hitbox (links oder rechts), um Nahkampftreffer zu prüfen.
     * @return Rechteck der Angriffs-Hitbox
     */
    public Rectangle getAttackHitbox() {
        Rectangle attackBox = getHitbox(); // Start with the base hitbox
        if (facingRight) {
            attackBox.x += attackBox.width; // Place it just to the right of the hitbox
        } else {
            attackBox.x -= attackBox.width; // Place it just to the left
        }
        return attackBox;
    }

    @Override public int getAttackDamage() {
        int level = levels.LevelManager.getInstance().getCurrentLevelNumber();
        double mult = 1.0 + 0.15 * Math.max(0, level - 1);
        return (int)Math.round(BASE_ATTACK_DAMAGE * mult);
    }
    @Override public void specialAttack() { attack(); }
    @Override public void moveLeft() {}
    @Override public void moveRight() {}
    @Override public void jump() {}
    @Override public void stop() {}
    @Override public float getSpeed() {
        int level = levels.LevelManager.getInstance().getCurrentLevelNumber();
        double mult = 1.0 + 0.08 * Math.max(0, level - 1);
        return (float)(BASE_SPEED * mult);
    }
    @Override public boolean isMoving() { return currentAction == Constants.EnemyActions.WALK; }

    public void setHit(boolean hit) { isHit = hit; }
    public void setTarget(Player target) { this.target = target; }
    public Player getTarget() { return target; }
    public void setAction(int action) {
        if (this.currentAction == action) return;
        this.currentAction = action;
        this.animationIndex = 0;
        this.animationTick = 0;

        if (action == Constants.EnemyActions.FIGHT) {
            animationSpeed = 10; // Back to a normal speed
        } else {
            animationSpeed = 15; // A default for other actions
        }
    }
    public int getCurrentAction() { return currentAction; }
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    // In Enemy.java, change to match Character's interface:
    @Override
    public int getX() {
        return (int) x; // Explicit cast if you must use float internally
    }

    @Override
    public int getY() {
        return (int) y; // Explicit cast if you must use float internally
    }

    public void setFacingRight(boolean facingRight) { this.facingRight = facingRight; }
    public boolean isFacingRight() { return facingRight; }
    public void setHealth(int hp) { this.health = Math.max(1, hp); }
    public void setAnimations(BufferedImage[][] animations) { this.animations = animations; }
    public BufferedImage[][] getAnimations() { return animations; }
    public void setAnimationIndex(int animationIndex) { this.animationIndex = animationIndex; }
    public int getAnimationIndex() { return animationIndex; }
    public void setAnimationTick(int animationTick) { this.animationTick = animationTick; }
    public int getAnimationTick() { return animationTick; }
    public void setAnimationSpeed(int animationSpeed) { this.animationSpeed = animationSpeed; }
    public int getAnimationSpeed() { return animationSpeed; }
    // In Enemy.java
    public boolean isHit() {
        return isHit;
    }

}