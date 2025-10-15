package entities;

import utils.AnimationManager;
import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Player extends GameCharacter {
    // === Level Constants ===
    private static final int LEVEL_WIDTH = Constants.LEVEL_WIDTH;
    private static final int LEVEL_HEIGHT = 1000;

    private final int maxHealth = Constants.Player.MAX_HEALTH;
    private int health = maxHealth;

    // === Physics & Movement ===
    private final float gravity = Constants.Player.GRAVITY;
    private final float jumpSpeed = Constants.Player.JUMP_SPEED;
    private static final int GROUND_LEVEL = Constants.GROUND_LEVEL;
    private float airSpeed = 0;
    private int jumpsLeft = Constants.Player.INITIAL_JUMPS;

    // === Dash System (MODIFIED) ===
    private boolean isDashing = false;
    private final float dashSpeed = Constants.Player.DASH_SPEED;
    private final int dashDuration = Constants.Player.DASH_DURATION_FRAMES;
    private int dashCounter = 0;

    // === Protection Systems ===
    private boolean isJumpingOverEnemy = false;
    private int jumpOverProtectionFrames = 0;
    private final int JUMP_OVER_PROTECTION_DURATION = Constants.Player.JUMP_OVER_PROTECTION_FRAMES;
    private boolean isAttackingProtected = false;
    private int attackProtectionFrames = 0;
    private final int ATTACK_PROTECTION_DURATION = Constants.Player.ATTACK_PROTECTION_FRAMES;
    private boolean isInvincible = false;
    private int invincibilityFrames = 0;
    private final int INVINCIBILITY_DURATION = Constants.Player.INVINCIBILITY_FRAMES;

    // === Combo System ===
    private int comboCount = 0;
    private long lastAttackTime = 0;
    private final int COMBO_TIME_WINDOW = Constants.Player.COMBO_TIME_WINDOW_MS;
    // Per-enemy short cooldown so multiple hits can register across a combo without multi-hitting every frame
    private final Map<Enemy, Integer> perEnemyHitCooldown = new HashMap<>();
    private int footstepTick = 0;
    private boolean footAlt = false;
    // Prevent jump-state flicker: require a few frames off-ground
    private int offGroundFrames = 0;

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
            try {
                java.io.File file = new java.io.File("res" + filePath);
                if (file.exists()) {
                    frames[i] = ImageIO.read(file);
                } else {
                    try (InputStream is = getClass().getResourceAsStream(filePath)) {
                        if (is != null) {
                            frames[i] = ImageIO.read(is);
                        } else if (utils.Constants.DEBUG_LOGS) {
                            System.err.println("❌ Player animation not found: " + filePath);
                        }
                    }
                }
            } catch (Exception e) {
                if (utils.Constants.DEBUG_LOGS) {
                    System.err.println("❌ Error loading player animation: " + filePath);
                }
            }
        }
        return frames;
    }

    @Override
    public void update() {
        updatePlayerState();
        updatePosition();
        updateAnimation();
        // Decrease hit cooldowns each frame
        if (!perEnemyHitCooldown.isEmpty()) {
            Iterator<Map.Entry<Enemy, Integer>> it = perEnemyHitCooldown.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Enemy, Integer> e = it.next();
                int v = e.getValue() - 1;
                if (v <= 0) it.remove(); else e.setValue(v);
            }
        }
    }

    private void updatePlayerState() {
        // Allow HIT/DEATH to control animation, but don't allow lingering JUMP when grounded
        if (currentAction == Constants.PlayerActions.DEATH) return;
        if (isDashing || isAttacking()) return;
    boolean effectivelyGrounded = isGrounded || offGroundFrames <= 3;
        int newState = Constants.PlayerActions.IDLE;
        if (!effectivelyGrounded) {
            newState = Constants.PlayerActions.JUMP;
        } else if (movingLeft || movingRight) {
            newState = Constants.PlayerActions.RUN;
        }
        // Avoid flipping back to JUMP from HIT unintentionally; PRIORITIZE HIT until cleared
        if (currentAction != Constants.PlayerActions.HIT) {
            setAction(newState);
        }
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
        if (invincibilityFrames > 0) {
            invincibilityFrames--;
            if (invincibilityFrames == 0) isInvincible = false;
        }

        if (!isGrounded) {
            y += airSpeed;
            airSpeed += gravity;
        }
        // Always reconcile ground contact after applying vertical motion
        if (y >= GROUND_LEVEL) {
            y = GROUND_LEVEL;
            if (!isGrounded) {
                jumpsLeft = Constants.Player.INITIAL_JUMPS;
                airSpeed = 0;
                isJumpingOverEnemy = false;
            }
            isGrounded = true;
            offGroundFrames = 0;
            if (currentAction == Constants.PlayerActions.JUMP && !attacking && !isDashing) {
                // Transition out of JUMP when we land
                setAction((movingLeft || movingRight) ? Constants.PlayerActions.RUN : Constants.PlayerActions.IDLE);
            }
            // Footsteps when running on ground
            if (currentAction == Constants.PlayerActions.RUN && (movingLeft || movingRight)) {
                footstepTick++;
                int interval = Math.max(8, 18 - (int)(Math.abs(getSpeed()) * 2));
                if (footstepTick >= interval) {
                    footstepTick = 0;
                    audio.SoundManager.getInstance().playSound(footAlt ? "footstep_hi" : "footstep_lo");
                    footAlt = !footAlt;
                }
            } else {
                footstepTick = 0;
            }
        } else {
            isGrounded = false;
            offGroundFrames++;
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
            g.drawImage(sub, (int)x, (int)y, getWidth(), getHeight(), null);
            drawHealthBar(g);
            drawComboCounter(g);
        } else {
            drawFallbackPlayer(g);
        }
    }

    @Override
    public void attack() {
        if (isAlive && !attacking && isGrounded) {
            // Start attack but don't start/advance combo until a hit lands
            perEnemyHitCooldown.clear();
            attacking = true;
            isAttackingProtected = true;
            attackProtectionFrames = ATTACK_PROTECTION_DURATION;
            setAction(Constants.PlayerActions.COMBO);
        }
    }

    @Override
    public void specialAttack() {
        if (isAlive && !attacking && !isGrounded) {
            perEnemyHitCooldown.clear();
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
            audio.SoundManager.getInstance().playSound("dash");
        }
    }

    public void slide() {
        if (isAlive && isGrounded && !attacking && currentAction != Constants.PlayerActions.SLIDE) {
            perEnemyHitCooldown.clear();
            setAction(Constants.PlayerActions.SLIDE);
            audio.SoundManager.getInstance().playSound("slide");
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
            case Constants.PlayerActions.COMBO -> {
                attacking = false;
                setAction(Constants.PlayerActions.IDLE);
                perEnemyHitCooldown.clear();
            }
            case Constants.PlayerActions.AIR_ATTACK -> {
                attacking = false;
                setAction(Constants.PlayerActions.IDLE);
                perEnemyHitCooldown.clear();
            }
            case Constants.PlayerActions.HIT -> {
                attacking = false; // Reset attacking state in case a combo was interrupted
                // If airborne, go to JUMP; if on ground, go to IDLE/RUN
                if (!isGrounded) setAction(Constants.PlayerActions.JUMP);
                else setAction((movingLeft || movingRight) ? Constants.PlayerActions.RUN : Constants.PlayerActions.IDLE);
                perEnemyHitCooldown.clear();
            }
            case Constants.PlayerActions.DASH -> {
                isDashing = false;
                // If airborne after dash, continue as jump; else idle/run
                if (!isGrounded) setAction(Constants.PlayerActions.JUMP);
                else setAction((movingLeft || movingRight) ? Constants.PlayerActions.RUN : Constants.PlayerActions.IDLE);
            }
            case Constants.PlayerActions.SLIDE -> {
                if (movingLeft || movingRight) setAction(Constants.PlayerActions.RUN);
                else setAction(Constants.PlayerActions.IDLE);
                perEnemyHitCooldown.clear();
            }
            case Constants.PlayerActions.DEATH -> {
                animationIndex = animations[Constants.PlayerActions.DEATH].length - 1;
                isAlive = false;
            }
            default -> animationIndex = 0;
        }
    }

    // Allow damage if the enemy isn't on a short per-enemy cooldown window
    public boolean canApplyDamageTo(Enemy enemy) {
        if (enemy == null) return false;
        int cd = perEnemyHitCooldown.getOrDefault(enemy, 0);
        if (cd > 0) return false;
        int windowFrames = switch (currentAction) {
            case Constants.PlayerActions.COMBO -> 8;
            case Constants.PlayerActions.AIR_ATTACK -> 10;
            case Constants.PlayerActions.SLIDE -> 12;
            default -> 8;
        };
        perEnemyHitCooldown.put(enemy, windowFrames);
        return true;
    }

    @Override
    public void setAction(int action) {
        if (this.currentAction == action) return;
        this.currentAction = action;
        this.animationIndex = 0;
        this.animationTick = 0;

        animationSpeed = switch (action) {
            case Constants.PlayerActions.COMBO -> 6;
            case Constants.PlayerActions.DASH -> 5;
            case Constants.PlayerActions.SLIDE -> 7;
            case Constants.PlayerActions.JUMP -> 12;
            case Constants.PlayerActions.IDLE -> 25;
            case Constants.PlayerActions.DEATH -> 15;
            default -> 10; // RUN, HIT, AIR_ATTACK
        };
    }

    @Override
    public void takeDamage(int damage) {
        if (!isAlive || isJumpingOverEnemy || isAttackingProtected || isInvincible) return;
        health -= damage;
        audio.SoundManager.getInstance().playSound("hurt"); // SOUND ADDED
        isInvincible = true;
        invincibilityFrames = INVINCIBILITY_DURATION;

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
        int attackRange = Constants.Player.ATTACK_RANGE + 10; // small forgiveness to avoid whiffs
        int attackHeight = Constants.Player.ATTACK_HEIGHT;
        int playerHitboxX = (int)x + Constants.Player.HITBOX_X_OFFSET;
        int playerHitboxWidth = getWidth() - Constants.Player.HITBOX_WIDTH_REDUCTION;
        int playerHitboxY = (int)y + Constants.Player.HITBOX_Y_OFFSET;

        if (facingRight) {
            // Hitbox starts at the right edge of the player's body hitbox
            int attackX = playerHitboxX + playerHitboxWidth;
            return new Rectangle(attackX, playerHitboxY, attackRange, attackHeight);
        } else {
            // Hitbox starts 'attackRange' pixels to the left of the player's body hitbox
            int attackX = playerHitboxX - attackRange;
            return new Rectangle(attackX, playerHitboxY, attackRange, attackHeight);
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
        if (isHitting) {
            audio.SoundManager.getInstance().playSound("hit");
        }
        return isHitting;
    }

    // Called by GamePanel when damage actually applied (after cooldown checks)
    public void onSuccessfulHit() {
        long now = System.currentTimeMillis();
        if (now - lastAttackTime < COMBO_TIME_WINDOW && comboCount > 0) {
            comboCount++;
        } else {
            comboCount = 1;
        }
        lastAttackTime = now;
        audio.SoundManager.getInstance().playComboSound(comboCount);
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
    
    @Override public float getSpeed() { return Constants.Player.SPEED; }

    @Override public boolean isMoving() { return movingLeft || movingRight; }
    @Override public int getWidth() { return Constants.Player.WIDTH; }
    @Override public int getHeight() { return Constants.Player.HEIGHT; }
    @Override public int getHealth() { return health; }
    @Override public boolean isAttacking() { return attacking || currentAction == Constants.PlayerActions.COMBO || currentAction == Constants.PlayerActions.AIR_ATTACK || currentAction == Constants.PlayerActions.SLIDE; }
    @Override public int getAttackDamage() {
        int level = levels.LevelManager.getInstance().getCurrentLevelNumber();
        int bonus = Constants.Player.DAMAGE_BONUS_BASE + (Math.max(0, level - 1) * Constants.Player.DAMAGE_BONUS_PER_LEVEL);
        return switch (currentAction) {
            case Constants.PlayerActions.COMBO -> ((comboCount >= 3) ? Constants.Player.COMBO_LVL3_DMG : ((comboCount == 2) ? Constants.Player.COMBO_LVL2_DMG : Constants.Player.COMBO_LVL1_DMG)) + bonus;
            case Constants.PlayerActions.AIR_ATTACK -> Constants.Player.AIR_ATTACK_DMG + bonus;
            case Constants.PlayerActions.SLIDE -> Constants.Player.SLIDE_DMG + bonus;
            default -> Constants.Player.DEFAULT_DMG + bonus;
        };
    }
    @Override public Rectangle getHitbox() { 
        return new Rectangle(
            (int)x + Constants.Player.HITBOX_X_OFFSET, 
            (int)y + Constants.Player.HITBOX_Y_OFFSET, 
            getWidth() - Constants.Player.HITBOX_WIDTH_REDUCTION, 
            getHeight() - Constants.Player.HITBOX_HEIGHT_REDUCTION
        ); 
    }

    private void drawHealthBar(Graphics g) {
        int barWidth = 150;
        int barHeight = 12;
        int healthWidth = (barWidth * health) / maxHealth;
        if (health > maxHealth * 0.6) g.setColor(Color.GREEN);
        else if (health > maxHealth * 0.3) g.setColor(Color.YELLOW);
        else g.setColor(Color.RED);
        g.fillRect((int)x, (int)y - 15, healthWidth, barHeight);
        g.setColor(Color.WHITE);
        g.drawRect((int)x, (int)y - 15, barWidth, barHeight);
    }

    private void drawFallbackPlayer(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect((int)x, (int)y, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.drawString("PLAYER", (int)x + 40, (int)y + 70);
        drawHealthBar(g);
        drawComboCounter(g);
    }

    private void drawComboCounter(Graphics g) {
    if (comboCount < 1) return; // show from x1
        long since = System.currentTimeMillis() - lastAttackTime;
        if (since > COMBO_TIME_WINDOW + 600) return; // fade after window + short tail
        float alpha = 1.0f - Math.min(1.0f, Math.max(0f, (since - COMBO_TIME_WINDOW) / 600f));
        int cx = (int) x + getWidth() / 2;
        int cy = (int) y - 24;
        Graphics2D g2 = (Graphics2D) g;
        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.25f, alpha)));
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String text = "x" + comboCount;
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(text);
        // Background chip
        g2.setColor(new Color(0,0,0,140));
        g2.fillRoundRect(cx - w/2 - 8, cy - fm.getAscent(), w + 16, fm.getAscent() + 8, 10, 10);
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(text, cx - w/2, cy);
        g2.setComposite(old);
    }
}