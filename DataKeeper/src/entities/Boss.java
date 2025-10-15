package entities;

import utils.Constants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Endgegner mit mehreren Angriffsphasen (Dash, Wellenangriff, Sprungschlag) und Telegraphie.
 * Die Schwierigkeit skaliert über Phasenwechsel (Phase 2 bei 50% HP).
 */
public class Boss extends Enemy {
    private int maxHealth = 2500;
    private int health = maxHealth;
    private final int width = 220;
    private final int height = 220;
    private int baselineY;
    private double vy = 0;
    private final double gravity = 0.7;

    private enum AttackType { NONE, TELEGRAPH_DASH, DASH, TELEGRAPH_WAVE, WAVE, TELEGRAPH_JUMP, JUMP }
    private AttackType attackType = AttackType.NONE;
    private int telegraphFrames = 0;
    private int attackFrames = 0;
    private int nextAttackTimer = 120; // frames until next telegraph
    private boolean phase2 = false;
    private int flashFrames = 0;

    /**
     * Erzeugt einen Boss an einer Start-X-Position und verknüpft ihn mit einem Zielspieler.
     * @param startX Startposition (X) in Weltkoordinaten
     * @param target Spielerziel für die Ausrichtung/Verfolgung
     */
    public Boss(int startX, Player target) {
        super(startX, target);
        setHealth(maxHealth);
        setAnimationSpeed(10);
        // Align boss feet with player's feet line: y_boss = GROUND - (bossH - playerH)
        baselineY = utils.Constants.GROUND_LEVEL - (height - utils.Constants.Player.HEIGHT);
        setY(baselineY);
    }

    @Override
    public int getWidth() { return width; }
    @Override
    public int getHeight() { return height; }
    @Override
    public int getAttackDamage() { return 22; }
    @Override
    public float getSpeed() { return 1.2f; }
    @Override
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }

    @Override
    public Rectangle getHitbox() {
        return new Rectangle(getX() + 30, getY() + 30, width - 60, height - 60);
    }

    @Override
    public void takeDamage(int damage) {
        if (!isAlive()) return;
        health -= damage;
        audio.SoundManager.getInstance().playSound("hit");
        if (health <= 0) {
            health = 0;
            setAlive(false);
            setAction(Constants.EnemyActions.DEATH);
            audio.SoundManager.getInstance().playSound("enemy_death");
        } else {
            setAction(Constants.EnemyActions.HIT);
            // Phase 2 at 50%: faster schedule
            if (!phase2 && health <= maxHealth / 2) phase2 = true;
        }
    }

    @Override
    public void render(Graphics g) {
        BufferedImage frame = getCurrentFrame();
        if (frame != null) {
            BufferedImage displayImage = frame;
            if (!isFacingRight()) displayImage = flipImageHorizontally(frame);
            g.drawImage(displayImage, getX(), getY(), width, height, null);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(getX(), getY(), width, height);
        }
        // Telegraph visuals
        if (telegraphFrames > 0) {
            Graphics2D g2 = (Graphics2D) g;
            Stroke old = g2.getStroke();
            // Soft warm glow during telegraph
            Rectangle hbGlow = getHitbox();
            int cxGlow = hbGlow.x + hbGlow.width/2;
            int cyGlow = hbGlow.y + hbGlow.height/2;
            int R = 220;
            g2.setColor(new Color(255, 230, 120, 60));
            g2.fillOval(cxGlow - R/2, cyGlow - R/2, R, R);
            switch (attackType) {
                case TELEGRAPH_DASH -> {
                    // Amber chevrons pointing in dash direction instead of a square
                    int dir = isFacingRight() ? 1 : -1;
                    Rectangle hb = getHitbox();
                    int baseX = dir > 0 ? hb.x + hb.width : hb.x;
                    int cy = hb.y + hb.height / 2;
                    g2.setColor(new Color(255, 210, 80, 180));
                    for (int i = 0; i < 3; i++) {
                        int offset = 20 + i * 30;
                        int x = baseX + dir * offset;
                        int size = 18;
                        int[] xs = new int[]{x, x - dir * size, x - dir * size};
                        int[] ys = new int[]{cy, cy - size, cy + size};
                        g2.fillPolygon(xs, ys, 3);
                    }
                    // Speed lines
                    g2.setStroke(new BasicStroke(2f));
                    for (int i = 0; i < 3; i++) {
                        int lx = baseX + dir * (10 + i * 18);
                        g2.drawLine(lx, cy - 28, lx, cy + 28);
                    }
                }
                case TELEGRAPH_WAVE -> {
                    // Cyan dashed concentric circles
                    g2.setColor(new Color(100, 220, 255, 200));
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{8f, 8f}, 0f));
                    Rectangle hb = getHitbox();
                    int cx = hb.x + hb.width/2;
                    int cy = hb.y + hb.height/2;
                    int[] radii = new int[]{120, 160, 200};
                    for (int r : radii) {
                        g2.drawOval(cx - r/2, cy - r/2, r, r);
                    }
                }
                case TELEGRAPH_JUMP -> {
                    // Red ground reticle at expected slam position
                    Rectangle hb = getHitbox();
                    int cx = hb.x + hb.width/2;
                    int gy = baselineY + hb.height - 20;
                    g2.setColor(new Color(255, 120, 120, 200));
                    g2.setStroke(new BasicStroke(3f));
                    int rad = 60;
                    g2.drawOval(cx - rad/2, gy - rad/2, rad, rad);
                    g2.drawLine(cx - rad/2 - 10, gy, cx - 10, gy);
                    g2.drawLine(cx + 10, gy, cx + rad/2 + 10, gy);
                    g2.drawLine(cx, gy - rad/2 - 10, cx, gy - 10);
                    g2.drawLine(cx, gy + 10, cx, gy + rad/2 + 10);
                }
                default -> {}
            }
            g2.setStroke(old);
        }
        // Brief flash after attack starts
        if (flashFrames > 0) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(255,255,255,90));
            Rectangle hb = getHitbox();
            g2.fillRect(hb.x - 8, hb.y - 8, hb.width + 16, hb.height + 16);
            flashFrames--;
        }
    }

    @Override
    public void update() {
        if (!isAlive()) {
            if (getCurrentAction() == utils.Constants.EnemyActions.DEATH) updateAnimation();
            return;
        }
        // Face player
        Player p = getTarget();
        if (p != null) setFacingRight(p.getX() > getX());

        // Choose attacks
        if (telegraphFrames > 0) {
            telegraphFrames--;
            setAction(utils.Constants.EnemyActions.IDLE);
            if (telegraphFrames == 0) beginAttack();
        } else if (attackFrames > 0) {
            // Active attack
            attackFrames--;
            setAction(utils.Constants.EnemyActions.FIGHT);
            switch (attackType) {
                case DASH -> doDash();
                case WAVE -> doWave();
                case JUMP -> doJump();
                default -> {}
            }
            if (attackFrames == 0) {
                attackType = AttackType.NONE;
                setAction(utils.Constants.EnemyActions.IDLE);
                nextAttackTimer = phase2 ? 60 : 90;
            }
        } else {
            // Idle/walk toward player
            if (nextAttackTimer > 0) nextAttackTimer--;
            if (nextAttackTimer == 0) scheduleNextAttack();
            else followPlayerWalk();
        }
        // Gravity for jump
        applyVertical();
        updateAnimation();
    }

    private void followPlayerWalk() {
        Player target = getTarget();
        if (target == null || !target.isAlive()) { setAction(utils.Constants.EnemyActions.IDLE); return; }
        float dx = target.getX() - this.getX();
        float dir = Math.signum(dx);
        float newX = this.getX() + dir * this.getSpeed();
        if (newX < 0) newX = 0;
        if (newX > utils.Constants.LEVEL_WIDTH - getWidth()) newX = utils.Constants.LEVEL_WIDTH - getWidth();
        setX(newX);
        setAction(utils.Constants.EnemyActions.WALK);
    }

    private void scheduleNextAttack() {
        double r = Math.random();
        if (r < 0.34) { attackType = AttackType.TELEGRAPH_DASH; }
        else if (r < 0.67) { attackType = AttackType.TELEGRAPH_WAVE; }
        else { attackType = AttackType.TELEGRAPH_JUMP; }
        telegraphFrames = phase2 ? 90 : 110;
        audio.SoundManager.getInstance().playSound("boss_warn");
    }

    private void beginAttack() {
        switch (attackType) {
            case TELEGRAPH_DASH -> { attackType = AttackType.DASH; attackFrames = phase2 ? 28 : 36; flashFrames = 6; }
            case TELEGRAPH_WAVE -> { attackType = AttackType.WAVE; attackFrames = phase2 ? 24 : 30; flashFrames = 6; }
            case TELEGRAPH_JUMP -> {
                attackType = AttackType.JUMP;
                attackFrames = phase2 ? 36 : 48;
                if (getY() >= baselineY) { vy = -12.5; }
                flashFrames = 6;
            }
            default -> {}
        }
    }

    private void doDash() {
        int dir = isFacingRight() ? 1 : -1;
        double dashSpeed = phase2 ? 16.0 : 12.0;
        setX((float)(getX() + dir * dashSpeed));
        // Clamp to level bounds
        if (getX() < 0) setX(0);
        if (getX() > utils.Constants.LEVEL_WIDTH - getWidth()) setX(utils.Constants.LEVEL_WIDTH - getWidth());
    }

    private void doWave() {
        // No movement; hitbox becomes large around boss via getAttackHitbox()
    }

    private void doJump() {
        // Movement handled by vertical physics; optional small horizontal tweak toward player
        Player p = getTarget();
        if (p != null) {
            int dir = p.getX() > getX() ? 1 : -1;
            setX((float)(getX() + dir * (phase2 ? 4.0 : 3.0)));
        }
    }

    private void applyVertical() {
        // Vertical motion only for jump attacks
        if (attackType == AttackType.JUMP || vy != 0) {
            int newY = (int) Math.round(getY() + vy);
            vy += gravity;
            if (newY >= baselineY) { newY = baselineY; vy = 0; }
            setY(newY);
        }
    }

    @Override
    public boolean isAttacking() {
        return attackType == AttackType.DASH || attackType == AttackType.WAVE || attackType == AttackType.JUMP;
    }

    @Override
    public Rectangle getAttackHitbox() {
        Rectangle hb = getHitbox();
        if (attackType == AttackType.DASH) {
            int dir = isFacingRight() ? 1 : -1;
            return new Rectangle(hb.x + dir * hb.width, hb.y, hb.width, hb.height);
        } else if (attackType == AttackType.WAVE) {
            int r = phase2 ? 220 : 180;
            return new Rectangle(hb.x + hb.width/2 - r/2, hb.y + hb.height/2 - r/2, r, r);
        } else if (attackType == AttackType.JUMP) {
            // Slam AOE when near ground
            if (getY() >= baselineY - 10) {
                int w = phase2 ? 260 : 200;
                return new Rectangle(hb.x + hb.width/2 - w/2, baselineY + hb.height - 40, w, 60);
            } else {
                return hb; // airborne body hit
            }
        }
        return hb;
    }
}
