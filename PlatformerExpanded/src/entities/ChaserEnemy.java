package entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import utils.Constants;

public class ChaserEnemy extends Enemy {
    private static final int LEVEL_WIDTH = Constants.LEVEL_WIDTH;
    private static final int GROUND_LEVEL = Constants.GROUND_LEVEL;

    public static final int IDLE = 0;
    public static final int WALK = 1;
    public static final int DEATH = 2;
    public static final int HIT = 3;
    public static final int FIGHT = 4;

    private boolean isHit = false;
    private int hitCooldown = 0;

    private boolean isAttacking = false;
    protected int attackCooldown = 0;
    private final int ATTACK_DAMAGE = 10;

    private int health = 100;
    private final int maxHealth = 100;
    private final Player target;
    private final float speed = 2.0f;

    public ChaserEnemy(int startX, Player target) {
        super(startX, target); // Call the Enemy constructor
        this.target = target;
        loadAnimations();
        x = startX;
        y = GROUND_LEVEL;
        facingRight = (target.getX() > startX);
        animationSpeed = 10;
    }


    @Override
    public void loadAnimations() {
        animations = new BufferedImage[5][];
        animations[IDLE] = loadFrames("idle", 8);
        animations[WALK] = loadFrames("run", 8);
        animations[DEATH] = loadFrames("death", 6);
        animations[HIT] = loadFrames("hit", 4);
        animations[FIGHT] = loadFrames("fight", 5);
    }

    private BufferedImage[] loadFrames(String name, int count) {
        BufferedImage[] frames = new BufferedImage[count];

        for (int i = 0; i < count; i++) {
            String path = String.format("/Enemy/glitsoul/%s/shardsoul_%s_%04d.png", name, name, i + 1);
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    frames[i] = ImageIO.read(is);
                }
            } catch (Exception e) {
                System.err.println("Error loading: " + path);
            }
        }
        return frames;
    }

    @Override
    public void update() {
        if (!isAlive) return;

        updateAnimation();

        if (hitCooldown > 0) {
            hitCooldown--;
            if (hitCooldown == 0) {
                isHit = false;
            }
        }

        if (attackCooldown > 0) {
            attackCooldown--;
            if (attackCooldown == 0) {
                isAttacking = false;
            }
        }

        if (!isHit) {
            followPlayer();
        } else {
            if (isInAttackRange() && attackCooldown == 0) {
                attack();
            }
        }

        updatePosition();
    }

    @Override
    public void render(Graphics g) {
        if (!isAlive) return;

        BufferedImage frame = getCurrentFrame();
        if (isInAttackRange() && attackCooldown == 0 && !isAttacking) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillOval(getX() + 30, getY() + getHeight() - 10, 80, 10);
        }
        if (frame != null) {
            try {
                BufferedImage displayImage = frame;
                if (!facingRight) {
                    displayImage = flipImageHorizontally(frame);
                }
                g.drawImage(displayImage, getX(), getY(), 140, 140, null);

                if (isAlive) {
                    drawHealthBar(g);
                }
            } catch (Exception e) {
                System.err.println("❌ Error rendering enemy: " + e.getMessage());
                drawFallback(g);
            }
        } else {
            drawFallback(g);
        }
    }

    @Override
    public void updateAnimation() {
        final int ATTACK_FRAME = 2;
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;

            if (currentAction == FIGHT && animationIndex == ATTACK_FRAME) {
                performAttack();
            }

            if (animationIndex >= getCurrentAnimationLength()) {
                handleAnimationCompletion();
            }
        }
    }

    private void performAttack() {
        if (target == null || !target.isAlive()) return;

        if (target.isAboveEnemy(this)) {
            return;
        }

        if (target.isAttacking() || target.getCurrentAction() == Player.SLIDE) {
            return;
        }

        if (getHitbox().intersects(target.getHitbox())) {
            target.takeDamage(ATTACK_DAMAGE);
        }
    }

    private void followPlayer() {
        if (target == null || !target.isAlive()) return;
        int distance = Math.abs(getX() - target.getX());

        // مدى الرؤية الكبير
        if (distance < 600) {
            setAction(WALK);
            // يتبع اللاعب بشكل طبيعي
            if (target.getX() < getX()) {
                facingRight = false;
                x -= 3.0f;
            } else {
                facingRight = true;
                x += 3.0f;
            }
            // محاولة الهجوم عندما يكون قريباً بما يكفي
            if (distance < Constants.ATTACK_RANGE && attackCooldown == 0) {
                attack();
            }
        } else {
            setAction(IDLE);
        }
        // حدود الخريطة
        if (x < 0) x = 0;
        if (x > LEVEL_WIDTH - getWidth()) x = LEVEL_WIDTH - getWidth();
    }

    @Override
    public void setAction(int action) {
        if (this.currentAction != action) {
            this.currentAction = action;
            animationIndex = 0;
            animationTick = 0;

            switch (action) {
                case FIGHT: animationSpeed = 40; break;  // Slower (was 30)
                case WALK: animationSpeed = 15; break;   // Slower (was 10)
                case IDLE: animationSpeed = 20; break;   // Slower (was 15)
                case HIT:
                case DEATH: animationSpeed = 15; break;  // Slower (was 10)
                default: animationSpeed = 18; break;
            }
        }
    }

    private void updatePosition() {
        y = GROUND_LEVEL;
        if (x < 0) x = 0;
        if (x > LEVEL_WIDTH - getWidth()) x = LEVEL_WIDTH - getWidth();
    }

    public boolean isInAttackRange() {
        if (target == null) return false;

        Rectangle enemyHitbox = getHitbox();
        Rectangle playerHitbox = target.getHitbox();

        boolean isIntersecting = enemyHitbox.intersects(playerHitbox);
        int horizontalDistance = Math.abs(getX() - target.getX());

        boolean inDistance = horizontalDistance < 150; // Reduced from 200

        return isIntersecting || inDistance;
    }

    @Override
    public void takeDamage(int damage) {
        final int HIT_COOLDOWN_MAX = Constants.HIT_COOLDOWN;
        health -= damage;
        isHit = true;
        hitCooldown = HIT_COOLDOWN_MAX;
        setAction(HIT);
        // تشغيل صوت الضربة دائماً
        audio.SoundManager.getInstance().playSound("hit");
        if (target != null) {
            if (target.getX() < this.getX()) {
                x += 15;
            } else {
                x -= 15;
            }
        }
        if (health <= 0) {
            health = 0;
            setAction(DEATH);
            isAlive = false;
            audio.SoundManager.getInstance().playSound("enemy_death");
        }
    }

    @Override
    public void attack() {
        if (isAlive && isInAttackRange() && !isAttacking && attackCooldown == 0) {
            if (target != null && target.isAboveEnemy(this)) {
                return;
            }

            final int ATTACK_COOLDOWN_MAX = Constants.ATTACK_COOLDOWN;
            isAttacking = true;
            attackCooldown = ATTACK_COOLDOWN_MAX;
            setAction(FIGHT);
        }
    }

    @Override
    public void specialAttack() {
        attack();
    }

    @Override
    public int getAttackDamage() {
        return ATTACK_DAMAGE;
    }

    @Override
    protected void handleAnimationCompletion() {
        if (currentAction == DEATH) {
            isAlive = false;
        } else if (currentAction == HIT) {
            setAction(IDLE);
        } else if (currentAction == FIGHT) {
            setAction(IDLE);
        } else {
            animationIndex = 0;
        }
    }

    private void drawFallback(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(getX(), getY(), 140, 140);
        g.setColor(Color.WHITE);
        g.drawString("Chaser", getX() + 10, getY() + 45);
        drawHealthBar(g);
    }

    private void drawHealthBar(Graphics g) {
        int barWidth = 60;
        int barHeight = 5;
        int healthWidth = (int) ((barWidth * health) / (float)maxHealth);

        g.setColor(Color.RED);
        g.fillRect(getX(), getY() - 10, barWidth, barHeight);
        g.setColor(Color.GREEN);
        g.fillRect(getX(), getY() - 10, healthWidth, barHeight);
    }

    @Override
    public void moveLeft() {
        if (isAlive) {
            facingRight = false;
            x -= speed;
            setAction(WALK);
        }
    }

    @Override
    public void moveRight() {
        if (isAlive) {
            facingRight = true;
            x += speed;
            setAction(WALK);
        }
    }

    @Override
    public void jump() {
        // Chasers don't jump
    }

    @Override
    public void stop() {
        if (isAlive) {
            setAction(IDLE);
        }
    }

    @Override
    public boolean isAttacking() {
        return isAttacking;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public boolean isMoving() {
        return currentAction == WALK;
    }

    @Override
    public int getHealth() {
        return health;
    }

    public Rectangle getHitbox() {
        return new Rectangle((int)x + 30, (int)y + 20, getWidth() - 60, getHeight() - 40);
    }

//    public int getAttackCooldown() {
//        return attackCooldown;
//    }

//    public boolean isHit() {
//        return isHit;
//    }
}
