package levels;

import entities.Enemy;
import entities.GameCharacter;
import entities.Player;

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

        spawnTimer += 1.0f / 120.0f; // Increment by frame time (assuming 120 FPS)

        if (spawnTimer >= config.getSpawnInterval()) {
            spawnTimer = 0;
            trySpawnEnemy();
        }
    }

    private void trySpawnEnemy() {
        int spawnX = getRandomSpawnX();
        // CORRECTED: Instantiate the concrete Enemy class directly
        Enemy enemy = new Enemy(spawnX, player);
        enemiesSpawned++;
        
        spawnedEnemies.add(enemy);
        System.out.println("Spawned Enemy #" + enemiesSpawned + " at X=" + spawnX);
    }

    private int getRandomSpawnX() {
        int playerX = player.getX();
        int spawnX;
        if (random.nextBoolean()) {
            spawnX = random.nextInt(300);
        } else {
            spawnX = LEVEL_WIDTH - 300 + random.nextInt(300);
        }
        if (Math.abs(spawnX - playerX) < 400) {
            spawnX = (spawnX < playerX) ? playerX - 400 : playerX + 400;
        }
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
}
