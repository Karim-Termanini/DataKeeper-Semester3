package entities;

import java.awt.*;
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
    private final int ATTACK_DAMAGE = 10;
    private final float speed = 1.5f;

    public Enemy(int startX, Player target) {
        super();
        this.target = target;
        this.x = startX;
        this.y = Constants.GROUND_LEVEL;
        this.isAlive = true;
        this.facingRight = (target.getX() > this.x);
        loadAnimations();
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
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    frames[i] = ImageIO.read(is);
                } else {
                    System.err.println("❌ Enemy animation not found: " + path);
                }
            } catch (Exception e) {
                System.err.println("❌ Error loading enemy animation: " + path);
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

        if (isHit) {
            setAction(Constants.EnemyActions.HIT);
        } else {
            followPlayer();
        }

        updateAnimation();
        updatePosition();
    }


    // CORRECTED AI LOGIC from user's working code: Move first, then check for attack.
    protected void followPlayer() {
        // 1. If we shouldn't be moving, stop and stand idle.
        if (target == null || !target.isAlive() || currentAction == Constants.EnemyActions.FIGHT) {
            setAction(Constants.EnemyActions.IDLE);
            return;
        }

        // 2. Determine direction based on player's position.
        boolean playerIsToTheLeft = target.getX() < this.getX();

        // 3. Set facing direction
        if (playerIsToTheLeft) {
            this.facingRight = false;
        } else {
            this.facingRight = true;
        }

        // 4. Check if we are in attack range.
        int distance = Math.abs(this.getX() - target.getX());
        if (distance < Constants.ATTACK_RANGE) {
            // If ready to attack, then attack.
            if (attackCooldown == 0) {
                attack(); // This will set the action to FIGHT
            } else {
                // If on cooldown, just stand idle.
                setAction(Constants.EnemyActions.IDLE);
            }
        } else {
            // 5. If not in attack range, move towards the player.
            if (playerIsToTheLeft) {
                this.x -= this.speed;
            } else {
                this.x += this.speed;
            }
            setAction(Constants.EnemyActions.WALK);
        }
    }

    private void updatePosition() {
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
        if (!isAlive) return;
        health -= damage;
        isHit = true;
        hitCooldown = Constants.HIT_COOLDOWN;
        setAction(Constants.EnemyActions.HIT);
        audio.SoundManager.getInstance().playSound("hit");

        if (health <= 0 && isAlive) {
            health = 0;
            isAlive = false;
            setAction(Constants.EnemyActions.DEATH);
            // ENEMY DEATH SOUND REMOVED AS REQUESTED
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
    @Override public int getAttackDamage() { return ATTACK_DAMAGE; }
    @Override public void specialAttack() { attack(); }
    @Override public void moveLeft() {}
    @Override public void moveRight() {}
    @Override public void jump() {}
    @Override public void stop() {}
    @Override public float getSpeed() { return speed; }
    @Override public boolean isMoving() { return currentAction == Constants.EnemyActions.WALK; }

    public void setHit(boolean hit) { isHit = hit; }
    public void setTarget(Player target) { this.target = target; }
    public Player getTarget() { return target; }
    public void setAction(int action) {
        if (this.currentAction == action) return;
        this.currentAction = action;
        this.animationIndex = 0;
        this.animationTick = 0;
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
