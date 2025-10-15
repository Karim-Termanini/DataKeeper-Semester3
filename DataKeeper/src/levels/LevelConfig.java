package levels;

public class LevelConfig {
    private final int levelNumber;
    private final int duration; // in seconds
    private final int totalEnemies;
    private final int chaserCount;
    private final int shooterCount;
    private final int tankCount;
    private final float spawnInterval; // in seconds
    private final boolean bossLevel;

    public LevelConfig(int levelNumber, int duration, int chaserCount, int shooterCount, int tankCount, float spawnInterval) {
        this.levelNumber = levelNumber;
        this.duration = duration;
        this.chaserCount = chaserCount;
        this.shooterCount = shooterCount;
        this.tankCount = tankCount;
        this.totalEnemies = chaserCount + shooterCount + tankCount;
        this.spawnInterval = spawnInterval;
        this.bossLevel = false;
    }
    private LevelConfig(int levelNumber, int duration, float spawnInterval, boolean bossLevel) {
        this.levelNumber = levelNumber;
        this.duration = duration;
        this.chaserCount = 0;
        this.shooterCount = 0;
        this.tankCount = 0;
        this.totalEnemies = 1;
        this.spawnInterval = spawnInterval;
        this.bossLevel = bossLevel;
    }
    public int getDuration() { return duration; }
    public float getSpawnInterval() { return spawnInterval; }
    public boolean isBossLevel() { return bossLevel; }
    public static LevelConfig getLevel1() {
        return new LevelConfig(1, 60, 12, 0, 0, 3.0f); // Hard from start - spawn every 3 seconds
    }

    public static LevelConfig getLevel2() {
        return new LevelConfig(2, 75, 20, 0, 0, 2.2f); // Slightly faster spawns
    }
    public static LevelConfig getLevel3() {
        return new LevelConfig(3, 90, 26, 0, 0, 1.8f); // Faster spawns
    }
    public static LevelConfig getLevel(int levelNumber) {
        return switch (levelNumber) {
            case 1 -> getLevel1();
            case 2 -> getLevel2();
            case 3 -> getLevel3();
            case 4 -> new LevelConfig(4, 60, 3.0f, true); // Boss level: duration used for portal timing if needed
            default -> {
                // Scale for levels 4+ (only chasers) - gets progressively harder
                int duration = 90 + (levelNumber - 3) * 20;
                int chasers = 26 + (levelNumber - 3) * 5;
                float spawnInterval = Math.max(0.8f, 1.8f - (levelNumber - 3) * 0.15f);
                yield new LevelConfig(levelNumber, duration, chasers, 0, 0, spawnInterval);
            }
        };
    }
    @Override
    public String toString() {
        return String.format("Level %d: %ds, Enemies: %d (C:%d S:%d T:%d), Spawn: %.1fs",
            levelNumber, duration, totalEnemies, chaserCount, shooterCount, tankCount, spawnInterval);
    }
}
