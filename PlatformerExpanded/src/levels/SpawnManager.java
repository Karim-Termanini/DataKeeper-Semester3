package levels;

import entities.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnManager {
    private final LevelConfig config;
    private final Player player;
    private final List<GameCharacter> spawnedEnemies;
    private final Random random;
    
    private int chasersSpawned;
    private int shootersSpawned;
    private int tanksSpawned;
    
    private float spawnTimer;
    private boolean canSpawn;
    
    private static final int LEVEL_WIDTH = utils.Constants.LEVEL_WIDTH;

    public SpawnManager(LevelConfig config, Player player) {
        this.config = config;
        this.player = player;
        this.spawnedEnemies = new ArrayList<>();
        this.random = new Random();
        this.spawnTimer = 0;
        this.canSpawn = true;
        this.chasersSpawned = 0;
        this.shootersSpawned = 0;
        this.tanksSpawned = 0;
    }

    public void update() {
        if (!canSpawn) return;

        spawnTimer += 1.0f / 120.0f; // Increment by frame time (assuming 120 FPS)

        if (spawnTimer >= config.getSpawnInterval()) {
            spawnTimer = 0;
            trySpawnEnemy();
        }
    }

    private void trySpawnEnemy() {
        // Always spawn chasers continuously - no cap
        int spawnX = getRandomSpawnX();
        GameCharacter enemy = new ChaserEnemy(spawnX, player);
        chasersSpawned++;
        
        spawnedEnemies.add(enemy);
        System.out.println("Spawned Chaser #" + chasersSpawned + " at X=" + spawnX);
    }
    
    // Old spawn logic - kept for reference
//    private void trySpawnEnemyOld() {
//        int totalSpawned = chasersSpawned + shootersSpawned + tanksSpawned;
//        
//        if (totalSpawned >= config.getTotalEnemies()) {
//            return; // All enemies spawned
//        }
//
//        // Determine which type to spawn
//        List<String> availableTypes = new ArrayList<>();
//        
//        if (chasersSpawned < config.getChaserCount()) {
//            availableTypes.add("chaser");
//        }
//        if (shootersSpawned < config.getShooterCount()) {
//            availableTypes.add("shooter");
//        }
//        if (tanksSpawned < config.getTankCount()) {
//            availableTypes.add("tank");
//        }
//
//        if (availableTypes.isEmpty()) return;
//
//        String typeToSpawn = availableTypes.get(random.nextInt(availableTypes.size()));
//        int spawnX = getRandomSpawnX();
//
//        GameCharacter enemy = null;
//        
//        if ("chaser".equals(typeToSpawn)) {
//            enemy = new ChaserEnemy(spawnX, player);
//            chasersSpawned++;
//            System.out.println("Spawned Chaser at X=" + spawnX);
//        }
//
//        if (enemy != null) {
//            spawnedEnemies.add(enemy);
//        }
//    }

    private int getRandomSpawnX() {
        // Spawn at edges of arena, away from player
        int playerX = player.getX();
        int spawnX;
        
        if (random.nextBoolean()) {
            // Spawn on left side
            spawnX = random.nextInt(300);
        } else {
            // Spawn on right side
            spawnX = LEVEL_WIDTH - 300 + random.nextInt(300);
        }
        
        // Ensure not too close to player
        if (Math.abs(spawnX - playerX) < 400) {
            spawnX = (spawnX < playerX) ? playerX - 400 : playerX + 400;
        }
        
        // Clamp to bounds
        spawnX = Math.max(0, Math.min(spawnX, LEVEL_WIDTH - 140));
        
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

//    public boolean hasSpawnedAllEnemies() {
//        return (chasersSpawned >= config.getChaserCount() &&
//                shootersSpawned >= config.getShooterCount() &&
//                tanksSpawned >= config.getTankCount());
//    }

//    public int getTotalSpawned() {
//        return chasersSpawned + shootersSpawned + tanksSpawned;
//    }

//    public void reset() {
//        spawnedEnemies.clear();
//        chasersSpawned = 0;
//        shootersSpawned = 0;
//        tanksSpawned = 0;
//        spawnTimer = 0;
//        canSpawn = true;
//    }
}
