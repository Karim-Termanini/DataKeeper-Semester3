package entities;

import utils.AnimationManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Player extends GameCharacter {
    // === ثوابت المستوى ===
    private static final int LEVEL_WIDTH = 2000;
    private static final int LEVEL_HEIGHT = 1000;

    private final int maxHealth = 200;
    private int health = maxHealth;

    // === الثوابت الأساسية ===
    public static final int IDLE = 0;
    public static final int RUN = 1;
    public static final int JUMP = 2;
    public static final int HIT = 3;
    public static final int DEATH = 4;
    public static final int AIR_ATTACK = 5;
    public static final int COMBO = 6;
    public static final int DASH = 7;
    public static final int SLIDE = 8;

    // === الفيزياء والحركة ===
    private final float gravity = 0.4f;
    private final float jumpSpeed = -14.0f;
    private final int groundLevel = 560;
    private float airSpeed = 0;
    private int jumpsLeft = 2;

    // === نظام الداش ===
    private boolean isDashing = false;
    private final float dashSpeed = 20.0f;
    private final int dashDuration = 10;
    private int dashCounter = 0;

    private boolean isJumpingOverEnemy = false;
    private int jumpOverProtectionFrames = 0;
    private final int JUMP_OVER_PROTECTION_DURATION = 40;

    // 🔥 نظام Combos
    private int comboCount = 0;
    private long lastAttackTime = 0;
    private final int COMBO_TIME_WINDOW = 800;
    private boolean comboHitEnemy = false;

    // === نظام الحماية أثناء الهجوم ===
    private boolean isAttackingProtected = false;
    private int attackProtectionFrames = 0;
    private final int ATTACK_PROTECTION_DURATION = 15;

    public Player() {
        loadAnimations();
        x = 100;
        y = groundLevel;
        animationSpeed = 10;
    }

    // تحميل الرسوميات والأنيميشن
    @Override
    public void loadAnimations() {
        animations = new BufferedImage[9][];
        animations[IDLE] = loadAnimationFrames("idle", 8);
        animations[RUN] = loadAnimationFrames("run", 8);
        animations[JUMP] = loadAnimationFrames("jump", 5);
        animations[HIT] = loadAnimationFrames("hit", 4);
        animations[DEATH] = loadAnimationFrames("death", 10);
        animations[AIR_ATTACK] = loadAnimationFrames("air_attack", 6);
        animations[COMBO] = loadAnimationFrames("combo", 19);
        animations[DASH] = loadAnimationFrames("dash", 6);
        animations[SLIDE] = loadAnimationFrames("slide", 12);
    }

    private BufferedImage[] loadAnimationFrames(String folderName, int frameCount) {
        BufferedImage[] frames = new BufferedImage[frameCount];
        int loadedCount = 0;

        for (int i = 0; i < frameCount; i++) {
            String filePath = String.format("/Fighter sprites/%s/fighter_%s_%04d.png",
                    folderName, folderName, i + 1);

            try (InputStream is = getClass().getResourceAsStream(filePath)) {
                if (is != null) {
                    frames[i] = ImageIO.read(is);
                    loadedCount++;
                    System.out.println("✅ " + filePath);
                } else {
                    System.err.println("❌ غير موجود: " + filePath);
                }
            } catch (Exception e) {
                System.err.println("❌ خطأ في: " + filePath);
            }
        }

        System.out.println("📊 " + folderName + ": " + loadedCount + "/" + frameCount + " إطار");
        return frames;
    }

    // الحركة والفيزياء
    @Override
    public void update() {
        updateAnimation();
        updatePosition();
        updatePlayerState();
    }

    // 🔥 إضافة visual feedback للهجوم الناجح
    @Override
    public void render(Graphics g) {
        BufferedImage currentFrame = getCurrentFrame();
        if (currentFrame != null) {
            try {
                BufferedImage sub = AnimationManager.cropImage(currentFrame, 120, 160, 250, 250);
                if (!facingRight) {
                    sub = flipImageHorizontally(sub);
                }

                // 🔥 تأثيرات أثناء الانزلاق
                if (currentAction == SLIDE) {
                    g.setColor(new Color(200, 200, 200, 150));
                    if (facingRight) {
                        g.fillOval((int)x - 20, (int)y + 100, 40, 20);
                    } else {
                        g.fillOval((int)x + 120, (int)y + 100, 40, 20);
                    }
                }

                // 🔥 مؤثرات القفز المزدوج
                if (!isGrounded && jumpsLeft == 0) {
                    g.setColor(new Color(255, 255, 0, 100));
                    g.fillOval((int)x + 35, (int)y + 120, 70, 20);
                }

                // 🔥 مؤثرات الـ Combo أثناء الهجوم
                if (isAttacking() && comboCount > 1) {
                    g.setColor(new Color(255, 100, 0, 150));
                    int particleCount = comboCount * 3;
                    for (int i = 0; i < particleCount; i++) {
                        int offsetX = (int)(Math.random() * 80) - 40;
                        int offsetY = (int)(Math.random() * 60) - 30;
                        int size = 8 + comboCount;
                        g.fillOval((int)x + 70 + offsetX, (int)y + 70 + offsetY, size, size);
                    }
                }

                g.drawImage(sub, (int)x, (int)y, 140, 140, null);

                drawHealthBar(g);

                // 🔥 عرض عداد الـ Combo على الشاشة
                if (comboCount > 1) {
                    g.setColor(Color.YELLOW);
                    g.setFont(new Font("Arial", Font.BOLD, 18));
                    String comboText = "COMBO x" + comboCount;
                    // 🔥 تأثير للـ Combo العالي
                    if (comboCount >= 3) {
                        g.setColor(Color.ORANGE);
                        g.setFont(new Font("Arial", Font.BOLD, 20));
                    }
                    if (comboCount >= 5) {
                        g.setColor(Color.RED);
                        g.setFont(new Font("Arial", Font.BOLD, 22));
                    }
                    g.drawString(comboText, getX() + 20, getY() - 25);
                }

            } catch (Exception e) {
                drawFallbackPlayer(g);
            }
        } else {
            drawFallbackPlayer(g);
        }
    }

    private void updateFacingDirection() {
        if (movingLeft && !movingRight) {
            facingRight = false;
        } else if (movingRight && !movingLeft) {
            facingRight = true;
        }
    }

    private void updatePlayerState() {
        // إعادة تعيين الـ Combo إذا مر وقت طويل
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime > COMBO_TIME_WINDOW * 2 && comboCount > 0) {
            System.out.println("⏰ انتهى الـ Combo");
            comboCount = 0;
            comboHitEnemy = false;
        }

        if (isDashing || attacking ||
                currentAction == SLIDE ||
                currentAction == AIR_ATTACK || // ← هذا السطر الجديد
                currentAction == DEATH) {
            return;}

        int newState = IDLE;
        if (!isGrounded) {
            newState = JUMP;
        } else if (movingLeft || movingRight) {
            newState = RUN;
        }
        setAction(newState);
    }

    @Override
    public void attack() {
        if (isAlive && !attacking && isGrounded) {
            long currentTime = System.currentTimeMillis();

            // 🔥 Combo يزيد فقط إذا الضربة السابقة أصابت عدواً
            if (currentTime - lastAttackTime < COMBO_TIME_WINDOW && comboHitEnemy) {
                comboCount++;
                if (comboCount > 1) {
                    System.out.println("🔥 كومبو! عدد الضربات: " + comboCount);
                }
            } else {
                comboCount = 1;
                System.out.println("👊 هجوم عادي");
            }

            lastAttackTime = currentTime;
            comboHitEnemy = false; // 🔥 إعادة تعيين حتى تثبت الضربة الجديدة

            attacking = true;
            isAttackingProtected = true;
            attackProtectionFrames = ATTACK_PROTECTION_DURATION;
            setAction(COMBO);
        }
    }

    @Override
    public void specialAttack() {
        if (isAlive && !attacking && !isGrounded) {
            attacking = true;
            setAction(AIR_ATTACK);
        }
    }

    @Override
    public void jump() {
        if (jumpsLeft > 0 && isAlive) {
            airSpeed = jumpSpeed;
            jumpsLeft--;
            isGrounded = false;
            setAction(JUMP);
        }
    }

    public void dash() {
        if (isAlive && !attacking && !isDashing) {
            isDashing = true;
            dashCounter = 0;
            setAction(DASH);
        }
    }

//    public void die() {
//        if (isAlive) {
//            setAction(DEATH);
//            System.out.println("💀 موت - DEATH");
//        }
//    }

    // التحكم والإدخال
    @Override
    public void moveLeft() {
        movingLeft = true;
        if (!movingRight) {
            facingRight = false;
        }
    }

    @Override
    public void moveRight() {
        movingRight = true;
        if (!movingLeft) {
            facingRight = true;
        }
    }

    @Override
    public void stop() {
        movingLeft = false;
        movingRight = false;
    }

//    public void setAttacking(boolean attacking) {
//        this.attacking = attacking;
//    }

    // الأنيميشن والعرض
    @Override
    protected void handleAnimationCompletion() {
        switch (currentAction) {
            case COMBO:
            case AIR_ATTACK:
                attacking = false;
                setAction(IDLE);
                break;
            case DASH:
                isDashing = false;
                setAction(IDLE);
                break;
            case SLIDE:
                // 🔥 بعد انتهاء الـ Slide، ارجع إلى IDLE أو RUN
                if (movingLeft || movingRight) {
                    setAction(RUN);
                } else {
                    setAction(IDLE);
                }
                break;
            case DEATH:
                animationIndex = animations[DEATH].length - 1;
                isAlive = false;
                break;
            default:
                animationIndex = 0;
                break;
        }
    }

    @Override
    public void setAction(int action) {
        if (this.currentAction != action) {
            this.currentAction = action;
            animationIndex = 0;
            animationTick = 0;

            switch (action) {
                case COMBO:
                case DASH:
                    animationSpeed = 5;
                    break;
                case SLIDE:
                    animationSpeed = 7;
                    break;
                case JUMP:
                    animationSpeed = 12;
                    break;
                case IDLE:
                    animationSpeed = 25;
                    break;
                case DEATH:
                    animationSpeed = 15;
                    break;
                default:
                    animationSpeed = 10;
                    break;
            }
        }
    }

    private void drawFallbackPlayer(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect((int)x, (int)y, 140, 140);
        g.setColor(Color.WHITE);
        g.drawString("PLAYER", (int)x + 40, (int)y + 70);
        drawHealthBar(g);
    }

    // الدوال الأساسية
    @Override
    public float getSpeed() {
        return 5.0f;
    }

    @Override
    public boolean isMoving() {
        return movingLeft || movingRight;
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

    @Override
    public boolean isAttacking() {
        return attacking || currentAction == COMBO || currentAction == AIR_ATTACK || currentAction == SLIDE;
    }

    @Override
    public int getAttackDamage() {
        int baseDamage;
        switch(currentAction){
            case COMBO:
                baseDamage = 20;
                // 🔥 زيادة الضرر مع الـ Combo
                if (comboCount >= 3) {
                    baseDamage = 35;
                    System.out.println("💥 كومبو ×3! ضرر: " + baseDamage);
                } else if (comboCount == 2) {
                    baseDamage = 25;
                    System.out.println("⚡ كومبو ×2! ضرر: " + baseDamage);
                }
                return baseDamage;
            case AIR_ATTACK: return 30;
            case SLIDE: return 10;
            default: return 5;
        }
    }

    // نظام التصادم (Collision System)
    public Rectangle getHitbox(){
        return new Rectangle((int)x + 30, (int)y + 20, getWidth() - 60, getHeight() - 40);
    }

    public boolean isCollidingWith(Enemy enemy) {
        return getHitbox().intersects(enemy.getHitbox());
    }

    public void takeDamage(int damage) {
        // 🔥 حماية كاملة أثناء القفز فوق العدو
        if (!isAlive || isJumpingOverEnemy || isAttackingProtected) {
            System.out.println("🛡️ حماية من الضرر - القفز فوق العدو!");
            return;
        }

        // 🔥 تقليل الضرر المستلم
        int reducedDamage = Math.max(1, damage - 5);
        health -= reducedDamage;

        System.out.println("💔 اللاعب تأثر بضرر: " + reducedDamage + " (الأصلي: " + damage + ") | الصحة المتبقية: " + health + "/" + maxHealth);

        // 🔥 تأثير مرئي عند أخذ الضرر
        if (health > 0) {
            setAction(HIT);
        }

        if (health <= 0) {
            health = 0;
            setAction(DEATH);
            isAlive = false;
            System.out.println("💀 اللاعب مات!");
        }
    }

    public void heal(int amount) {
        if (!isAlive) return;

        health = Math.min(health + amount, maxHealth);
        System.out.println("❤️‍🩹 تم الشفاء! الصحة: " + health + "/" + maxHealth);
    }

    private void drawHealthBar(Graphics g) {
        int barWidth = 150;
        int barHeight = 12;
        int healthWidth = (barWidth * health) / maxHealth;

        // 🔥 شريط صحة متدرج
        if (health > maxHealth * 0.6) {
            g.setColor(Color.GREEN);
        } else if (health > maxHealth * 0.3) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.RED);
        }

        g.fillRect(getX(), getY() - 15, healthWidth, barHeight);
        g.setColor(Color.WHITE);
        g.drawRect(getX(), getY() - 15, barWidth, barHeight);

        // 🔥 نص الصحة
        g.drawString(health + "/" + maxHealth, getX() + 5, getY() - 5);
    }

    // نظام الهجوم
    public Rectangle getAttackHitbox() {
        int attackRange = 100; // 🔥 زيادة المدى
        int attackHeight = 100;

        if (facingRight) {
            return new Rectangle((int)x + getWidth() - 20, (int)y + 20, attackRange, attackHeight);
        } else {
            return new Rectangle((int)x - attackRange + 20, (int)y + 20, attackRange, attackHeight);
        }
    }

    public boolean isAttackingEnemy(Enemy enemy) {
        if (!isAttacking()) return false;

        Rectangle attackHitbox = getAttackHitbox();
        Rectangle enemyHitbox = enemy.getHitbox();

        boolean isHitting = attackHitbox.intersects(enemyHitbox);

        if (isHitting) {
            comboHitEnemy = true;
            System.out.println("🎯 الضربة أصابت العدو! كومبو: " + comboCount);
        }

        return isHitting;
    }

    // نظام الحماية أثناء القفز
    public void activateJumpOverProtection() {
        isJumpingOverEnemy = true;
        jumpOverProtectionFrames = JUMP_OVER_PROTECTION_DURATION;
        System.out.println("🦘 حماية القفز فوق العدو مفعلة!");
    }

    public boolean isAboveEnemy(Enemy enemy) {
        Rectangle p = getHitbox();
        Rectangle e = enemy.getHitbox();
        boolean isAbove = p.getY() + p.getHeight() < e.getY() + 60; // ← من 30 إلى 60
        boolean isIntersectingHorizontally = p.intersects(e);
        return isAbove && isIntersectingHorizontally;
    }

//    public void setPosition(float x, float y) {
//        this.x = x;
//        this.y = y;
//    }

    // 🔥 أضف هذه الدالة للحصول على الإجراء الحالي
    public int getCurrentAction() {
        return currentAction;
    }

    // 🔥 إصلاح دالة الـ Slide
    public void slide() {
        if (isAlive && isGrounded && !attacking && currentAction != SLIDE) {
            setAction(SLIDE);
            System.out.println("🛷 انزلاق!");
        }
    }

    private void updatePosition() {
        updateFacingDirection();

        if (isDashing) {
            if (facingRight) {
                x += dashSpeed;
            } else {
                x -= dashSpeed;
            }
            dashCounter++;
            if (dashCounter >= dashDuration) {
                isDashing = false;
            }
        } else if (currentAction == SLIDE) {
            // 🔥 حركة الـ Slide
            if (facingRight) {
                x += 10f; // سرعة الانزلاق
            } else {
                x -= 10f;
            }
        } else {
            // الحركة العادية
            if (movingLeft) {
                x -= getSpeed();
                facingRight = false;
            }
            if (movingRight) {
                x += getSpeed();
                facingRight = true;
            }
        }

        // 🔥 أضف هذا الجزء المفقود - نظام الجاذبية والقفز
        if (jumpOverProtectionFrames > 0) {
            jumpOverProtectionFrames--;
            if (jumpOverProtectionFrames == 0) {
                isJumpingOverEnemy = false;
            }
        }

        // 🔥 تحديث نظام الحماية أثناء الهجوم
        if (attackProtectionFrames > 0) {
            attackProtectionFrames--;
            if (attackProtectionFrames == 0) {
                isAttackingProtected = false;
            }
        }

        // حدود المستوى
        x = Math.max(0, Math.min(x, LEVEL_WIDTH - getWidth()));
        y = Math.max(0, Math.min(y, LEVEL_HEIGHT - getHeight()));

        // 🔥 الفيزياء والجاذبية
        if (!isGrounded) {
            y += airSpeed;
            airSpeed += gravity;

            if (y >= groundLevel) {
                y = groundLevel;
                isGrounded = true;
                jumpsLeft = 2;
                airSpeed = 0;
                isJumpingOverEnemy = false;
            }

            if (y > LEVEL_HEIGHT - getHeight()) {
                y = LEVEL_HEIGHT - getHeight();
                isGrounded = true;
                jumpsLeft = 2;
                airSpeed = 0;
                isJumpingOverEnemy = false;
            }
        }
    }

//    public boolean isJumpingOverEnemy() {
//        return isJumpingOverEnemy;
//    }

    // 🔥 دوال getter إضافية إذا لزم الأمر
    public boolean isAttackingProtected() {
        return isAttackingProtected;
    }

//    public int getAttackProtectionFrames() {
//        return attackProtectionFrames;
//    }

    // 🔥 دالة جديدة للتحقق من الحماية
    public boolean hasJumpOverProtection() {
        return isJumpingOverEnemy && jumpOverProtectionFrames > 0;
    }
}
