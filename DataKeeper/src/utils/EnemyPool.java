package utils;

import entities.Enemy;
import entities.Player;
import java.util.ArrayList;
import java.util.List;

public class EnemyPool {
    private static EnemyPool instance;
    private final List<Enemy> pool;
    private final int initialSize = 10; // Initial pool size

    private EnemyPool() {
        pool = new ArrayList<>();
        for (int i = 0; i < initialSize; i++) {
            pool.add(new Enemy(0, null)); // Create inactive enemies
        }
    }

    public static EnemyPool getInstance() {
        if (instance == null) {
            instance = new EnemyPool();
        }
        return instance;
    }

    public Enemy getEnemy(int startX, Player target) {
        for (Enemy enemy : pool) {
            if (!enemy.isAlive()) {
                enemy.reset(startX, target);
                return enemy;
            }
        }
        // If no available enemy, create a new one
        Enemy newEnemy = new Enemy(startX, target);
        pool.add(newEnemy);
        return newEnemy;
    }

    public void returnEnemy(Enemy enemy) {
        enemy.setAlive(false);
        enemy.setTarget(null);
        // Minimal cleanup; full reset happens on checkout
    }
}