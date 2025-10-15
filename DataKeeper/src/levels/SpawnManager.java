package levels;

import entities.Enemy;
import entities.GameCharacter;
import entities.Player;
import entities.Boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnManager {
    private final LevelConfig config;
    private final Player player;
    private final List<GameCharacter> spawnedEnemies;
    private final Random random;
    
    private int enemiesSpawned;
    
    private float spawnTimer;
    private boolean canSpawn;
    private int activeEnemyCount;
    
    private static final int LEVEL_WIDTH = utils.Constants.LEVEL_WIDTH;

    public SpawnManager(LevelConfig config, Player player) {
        this.config = config;
        this.player = player;
        this.spawnedEnemies = new ArrayList<>();
        this.random = new Random();
        this.spawnTimer = 0;
        this.canSpawn = true;
        this.enemiesSpawned = 0;
    }

    public void update() {
        if (!canSpawn) return;
        // Boss level: spawn only one boss and stop spawning further
        if (config.isBossLevel()) {
            if (enemiesSpawned == 0 && activeEnemyCount == 0) {
                int spawnX = getRandomSpawnX();
                Boss boss = new Boss(spawnX, player);
                spawnedEnemies.add(boss);
                enemiesSpawned = 1;
                if (utils.Constants.DEBUG_LOGS) System.out.println("Spawned BOSS at X=" + spawnX);
            }
            return;
        }
        // Cap active enemies to mitigate lag based on current active count from GamePanel
        if (activeEnemyCount >= utils.Constants.MAX_ACTIVE_ENEMIES) return;

        spawnTimer += 1.0f / 120.0f; // Increment by frame time (assuming 120 FPS)

        if (spawnTimer >= config.getSpawnInterval()) {
            spawnTimer = 0;
            trySpawnEnemy();
        }
    }

    private void trySpawnEnemy() {
        int spawnX = getRandomSpawnX();
        // Use object pool instead of creating new enemy
        Enemy enemy = utils.EnemyPool.getInstance().getEnemy(spawnX, player);
    // Scale health with level: base * (1 + 0.25*(level-1)) a bit steeper
        int level = LevelManager.getInstance().getCurrentLevelNumber();
        int base = utils.Constants.Enemy.HEALTH;
    int scaled = (int) Math.round(base * (1.0 + 0.25 * Math.max(0, level - 1)));
        enemy.setHealth(scaled);
        enemiesSpawned++;
        
        spawnedEnemies.add(enemy);
        if (utils.Constants.DEBUG_LOGS) {
            System.out.println("Spawned Enemy #" + enemiesSpawned + " at X=" + spawnX);
        }
    }

    private int getRandomSpawnX() {
        int playerX = player.getX();
        int screenWidth = 1800; // GamePanel width
        int offset = screenWidth / 2 + random.nextInt(200); // Spawn just off-screen

        int spawnX = playerX + (random.nextBoolean() ? offset : -offset);

        // Clamp the spawn position to be within the level boundaries
        spawnX = Math.max(0, Math.min(spawnX, LEVEL_WIDTH - 140)); // 140 is assumed enemy width
        
        return spawnX;
    }

    public void stopSpawning() {
        canSpawn = false;
    }

    public void resumeSpawning() {
        canSpawn = true;
    }

    public List<GameCharacter> getSpawnedEnemies() {
        return spawnedEnemies;
    }

    // Return a copy of spawned enemies and clear the internal list to avoid unbounded growth.
    public List<GameCharacter> drainSpawnedEnemies() {
        List<GameCharacter> copy = new ArrayList<>(spawnedEnemies);
        spawnedEnemies.clear();
        return copy;
    }

    public void setActiveEnemyCount(int count) {
        this.activeEnemyCount = Math.max(0, count);
    }
}
