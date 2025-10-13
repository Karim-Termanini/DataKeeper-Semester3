package entities;

import utils.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

// This is the concrete implementation of a Chaser enemy.
public class ChaserEnemy extends Enemy {

    private final float speed = 1.5f;
    private int attackCooldownTimer = 0; // Fixed: Added cooldown tracking

    public ChaserEnemy(int startX, Player target) {
        super(startX, target);
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
                    System.err.println("❌ Chaser animation not found: " + path);
                }
            } catch (Exception e) {
                System.err.println("❌ Error loading chaser animation: " + path);
            }
        }
        return frames;
    }


    // This is the CORRECT AI logic, based on the user's working code.
    protected void decideNextAction() {
        // Fixed: Cooldown decrement logic
        if (attackCooldownTimer > 0) {
            attackCooldownTimer--;
        }

        if (isHit() || currentAction == Constants.EnemyActions.FIGHT) {
            return;
        }


        if (target == null || !target.isAlive()) {
            setAction(Constants.EnemyActions.IDLE);
            return;
        }

        if (target.getX() < getX()) {
            facingRight = false;
        } else {
            facingRight = true;
        }

        int distance = Math.abs(getX() - target.getX());

        if (distance < Constants.ATTACK_RANGE) {
            // Fixed: Use instance variable instead of local
            if (attackCooldownTimer == 0) {
                attack();
            } else {
                setAction(Constants.EnemyActions.IDLE);
            }
        } else if (distance < 600) {
            setAction(Constants.EnemyActions.WALK);
            if (facingRight) {
                x += speed;
            } else {
                x -= speed;
            }
        } else {
            setAction(Constants.EnemyActions.IDLE);
        }
    }

    @Override
    public void attack() {
        // Fixed: Set cooldown after attacking
        attackCooldownTimer = Constants.ATTACK_COOLDOWN;
        setAction(Constants.EnemyActions.FIGHT);
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
    }

    @Override
    public float getSpeed() {
        return speed;
    }
}