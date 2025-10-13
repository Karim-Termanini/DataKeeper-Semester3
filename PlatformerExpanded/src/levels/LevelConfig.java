package levels;

public class LevelConfig {
    private final int levelNumber;
    private final int duration; // in seconds
    private final int totalEnemies;
    private final int chaserCount;
    private final int shooterCount;
    private final int tankCount;
    private final float spawnInterval; // in seconds

    public LevelConfig(int levelNumber, int duration, int chaserCount, int shooterCount, int tankCount, float spawnInterval) {
        this.levelNumber = levelNumber;
        this.duration = duration;
        this.chaserCount = chaserCount;
        this.shooterCount = shooterCount;
        this.tankCount = tankCount;
        this.totalEnemies = chaserCount + shooterCount + tankCount;
        this.spawnInterval = spawnInterval;
    }
    public int getDuration() { return duration; }
    public float getSpawnInterval() { return spawnInterval; }
    public static LevelConfig getLevel1() {
        return new LevelConfig(1, 60, 12, 0, 0, 3.0f); // Hard from start - spawn every 3 seconds
    }

    public static LevelConfig getLevel2() {
        return new LevelConfig(2, 75, 16, 0, 0, 2.5f); // Harder - spawn every 2.5 seconds
    }
    public static LevelConfig getLevel3() {
        return new LevelConfig(3, 90, 20, 0, 0, 2.0f); // Very hard - spawn every 2 seconds
    }
    public static LevelConfig getLevel(int levelNumber) {
        return switch (levelNumber) {
            case 1 -> getLevel1();
            case 2 -> getLevel2();
            case 3 -> getLevel3();
            default -> {
                // Scale for levels 4+ (only chasers) - gets progressively harder
                int duration = 90 + (levelNumber - 3) * 20;
                int chasers = 20 + (levelNumber - 3) * 4;
                float spawnInterval = Math.max(1.0f, 2.0f - (levelNumber - 3) * 0.2f);
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
