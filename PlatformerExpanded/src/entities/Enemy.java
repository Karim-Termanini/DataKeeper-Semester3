package entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import utils.Constants;

public class Enemy extends GameCharacter {
    // === ثوابت المستوى ===
    private static final int LEVEL_WIDTH = 2000;

    // === متغيرات الضرر ===
    private boolean isHit = false;
    private int hitCooldown = 0;
    private final int HIT_COOLDOWN_MAX = 30;

    // === متغيرات الهجوم ===
    private boolean isAttacking = false;
    protected int attackCooldown = 0;
    private final int ATTACK_COOLDOWN_MAX = 60;
    private final int ATTACK_DAMAGE = 10;
    private final int ATTACK_FRAME = 2;

    // === الصحة والهدف ===
    private int health = 100;
    private final Player target;

    public Enemy(int startX, Player target) {
        this.target = target;
        loadAnimations();
        x = startX;
        y = 560;
        facingRight = false;
        animationSpeed = 12;
    }

    // تحميل الرسوميات والأنيميشن
    @Override
    public void loadAnimations() {
        animations = new BufferedImage[5][];
        animations[Constants.Actions.IDLE] = loadFrames("idle", 8);
        animations[Constants.Actions.WALK] = loadFrames("run", 8);
        animations[Constants.Actions.FIGHT] = loadFrames("fight", 5);
        animations[Constants.Actions.DEATH] = loadFrames("death", 6);
        animations[Constants.Actions.HIT] = loadFrames("hit", 4);
    }

    private BufferedImage[] loadFrames(String name, int count) {
        BufferedImage[] frames = new BufferedImage[count];
        int loadedCount = 0;

        for (int i = 0; i < count; i++) {
            String path = String.format("/Enemy/glitsoul/%s/shardsoul_%s_%04d.png", name, name, i + 1);
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    frames[i] = ImageIO.read(is);
                    loadedCount++;
                    System.out.println("✅ Enemy: " + path);
                } else {
                    System.err.println("❌ Enemy not found: " + path);
                }
            } catch (Exception e) {
                System.err.println("❌ Error loading enemy: " + path);
            }
        }

        System.out.println("📊 Enemy " + name + ": " + loadedCount + "/" + count + " frames");
        return frames;
    }

    // الذكاء الاصطناعي وتتبع اللاعب
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

        // 🔥 إصلاح: تحديث حالة الهجوم حتى لو كان مصاباً
        if (!isHit) {
            followPlayer();
        } else {
            // حتى أثناء الإصابة، يمكنه محاولة الهجوم إذا كان في المدى
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
                drawFallbackEnemy(g);
            }
        } else {
            drawFallbackEnemy(g);
        }
    }

//    public int getAttackCooldown() {
//        return attackCooldown;
//    }

    @Override
    public void updateAnimation() {
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;

            if (currentAction == Constants.Actions.FIGHT && animationIndex == ATTACK_FRAME) {
                performAttack();
            }

            if (animationIndex >= getCurrentAnimationLength()) {
                handleAnimationCompletion();
            }
        }
    }

    private void performAttack() {
        if (target == null || !target.isAlive()) return;

        // منع الهجوم إذا اللاعب فوق العدو
        if (target.isAboveEnemy(this)) {
            System.out.println("🛡️ العدو لا يهاجم - اللاعب فوقه!");
            return;
        }

        // منع الهجوم إذا اللاعب يهاجم أو ينزلق
        if (target.isAttacking() || target.getCurrentAction() == Player.SLIDE) {
            return;
        }

        // تنفيذ الضرر فقط إذا كان هناك تصادم
        if (getHitbox().intersects(target.getHitbox())) {
            target.takeDamage(ATTACK_DAMAGE);
        }
    }

    private void followPlayer() {
        if (target == null || !target.isAlive()) return;

        int distance = Math.abs(getX() - target.getX());

        // 🔥 زيادة مدى الرؤية أكثر
        if (distance < 600) { // 🔥 زد المدى من 500 إلى 600
            setAction(Constants.Actions.WALK);

            // يتبع اللاعب بشكل طبيعي
            if (target.getX() < getX()) {
                facingRight = false;
                x -= 3.0f; // 🔥 زد السرعة من 2.0 إلى 3.0
            } else {
                facingRight = true;
                x += 3.0f; // 🔥 زد السرعة من 2.0 إلى 3.0
            }

            // 🔥 محاولة الهجوم عندما يكون قريباً بما يكفي
            if (distance < 200 && attackCooldown == 0) { // 🔥 زد من 150 إلى 200
                attack();
            }
        } else {
            setAction(Constants.Actions.IDLE);
        }
    }


    @Override
    public void setAction(int action) {
        if (this.currentAction != action) {
            this.currentAction = action;
            animationIndex = 0;
            animationTick = 0;

            switch (action) {
                case Constants.Actions.FIGHT:
                    animationSpeed = 30; // ← فقط الهجوم: جرّب 15، 18، أو 20
                    break;
                case Constants.Actions.WALK:
                    animationSpeed = 10;  // ← طبيعي (أسرع من 30!)
                    break;
                case Constants.Actions.IDLE:
                    animationSpeed = 15; // ← طبيعي
                    break;
                case Constants.Actions.HIT:
                    animationSpeed = 10;  // ← سريع شوي عشان يرجع لوضعه
                    break;
                case Constants.Actions.DEATH:
                    animationSpeed = 10;
                    break;
                default:
                    animationSpeed = 12;
                    break;
            }
        }
    }

    private void updatePosition() {
        y = 560;
        if (x < 0) x = 0;
        if (x > LEVEL_WIDTH - getWidth()) x = LEVEL_WIDTH - getWidth();
    }

    public boolean isInAttackRange() {
        if (target == null) return false;

        Rectangle enemyHitbox = getHitbox();
        Rectangle playerHitbox = target.getHitbox();

        // 🔥 زيادة كبيرة في المدى
        boolean isIntersecting = enemyHitbox.intersects(playerHitbox);
        int horizontalDistance = Math.abs(getX() - target.getX());

        // 🔥 زد المدى من 120 إلى 200 بكسل
        boolean inDistance = horizontalDistance < 200;

        System.out.println("🎯 مدى الهجوم: تداخل=" + isIntersecting +
                ", مسافة=" + horizontalDistance +
                ", في المدى=" + inDistance);

        return isIntersecting || inDistance;
    }

    // النظام القتالي والضرر - 🔥 دالة takeDamage واحدة فقط
    @Override
    public void takeDamage(int damage) {
        if (!isAlive || isHit || hitCooldown > 0) return;

        health -= damage;
        isHit = true;
        hitCooldown = HIT_COOLDOWN_MAX;
        setAction(Constants.Actions.HIT);
        System.out.println("💥 العدو تأثر بضرر: " + damage + " | الصحة المتبقية: " + health);

        // 🔥 تأثير ارتداد
        if (target != null) {
            if (target.getX() < this.getX()) {
                x += 15; // دفع العدو لليمين
            } else {
                x -= 15; // دفع العدو لليسار
            }
        }

        if (health <= 0) {
            health = 0;
            setAction(Constants.Actions.DEATH);
            isAlive = false;
            System.out.println("💀 العدو مات!");
        }
    }

    @Override
    public void attack() {
        if (isAlive && isInAttackRange() && !isAttacking && attackCooldown == 0) {
            // 🔥 تحقق إضافي أن اللاعب ليس فوق العدو
            if (target != null && target.isAboveEnemy(this)) {
                System.out.println("🛡️ العدو لا يهاجم - اللاعب فوقه!");
                return;
            }

            isAttacking = true;
            attackCooldown = ATTACK_COOLDOWN_MAX;
            setAction(Constants.Actions.FIGHT);
            System.out.println("👊 العدو يهاجم اللاعب! المسافة: " + Math.abs(getX() - target.getX()));
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

    // الأنيميشن والعرض
    @Override
    protected void handleAnimationCompletion() {
        if (currentAction == Constants.Actions.DEATH) {
            isAlive = false;
        } else if (currentAction == Constants.Actions.HIT) {
            setAction(Constants.Actions.IDLE);
        } else if (currentAction == Constants.Actions.FIGHT) {
            setAction(Constants.Actions.IDLE);
        }
    }

    private void drawFallbackEnemy(Graphics g) {
        if (isAlive) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.GRAY);
        }
        g.fillRect(getX(), getY(), 140, 140);
        g.setColor(Color.WHITE);

        if (isAlive) {
            g.drawString("Enemy", getX() + 10, getY() + 45);
            drawHealthBar(g);
        } else {
            g.drawString("DEAD", getX() + 10, getY() + 45);
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

    // الدوال الأساسية
    @Override
    public void moveLeft() {
        if (isAlive) {
            facingRight = false;
            x -= 2;
            setAction(Constants.Actions.WALK);
        }
    }

    @Override
    public void moveRight() {
        if (isAlive) {
            facingRight = true;
            x += 2;
            setAction(Constants.Actions.WALK);
        }
    }

    @Override
    public void jump() {
        // الأعداء ما بقدرن يقفزوا - ترك فارغ
    }

    @Override
    public void stop() {
        if (isAlive) {
            setAction(Constants.Actions.IDLE);
        }
    }

    @Override
    public boolean isAttacking() {
        return isAttacking;
    }

    @Override
    public float getSpeed() {
        return 1.5f;
    }

    @Override
    public boolean isMoving() {
        return currentAction == Constants.Actions.WALK;
    }

    @Override
    public int getWidth() {
        return 140;
    }

    @Override
    public int getHeight() {
        return 140;
    }

    @Override
    public int getHealth() {
        return health;
    }

//    public boolean isAggressive() {
//        return health > 0;
//    }

    public Rectangle getHitbox() {
        return new Rectangle((int)x + 30, (int)y + 20, getWidth() - 60, getHeight() - 40);
    }

//    public int getHitCooldown() {
//        return hitCooldown;
//    }

//    public boolean isHit() {
//        return isHit;
//    }
}
