package levels;

import entities.Player;
import gameplay.GameState;
import gameplay.SurvivalTimer;

public class LevelManager {
    private static LevelManager instance;
    
    private int currentLevelNumber;
    private LevelConfig currentConfig;
    private SurvivalTimer timer;
    private SpawnManager spawnManager;
    private Portal portal;
    private Player player;
    
    private GameState gameState;
    private boolean portalSpawned;
    private int enemiesDefeated;
    
    private static final int PORTAL_X = utils.Constants.PORTAL_X;
    private static final int PORTAL_Y = utils.Constants.PORTAL_Y;

    private LevelManager() {
        currentLevelNumber = 1;
        gameState = GameState.MAIN_MENU;
        portalSpawned = false;
        enemiesDefeated = 0;
    }

    public static LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }

    public void initializeLevel(int levelNumber, Player player) {
        this.currentLevelNumber = levelNumber;
        this.player = player;
        this.currentConfig = LevelConfig.getLevel(levelNumber);
        this.timer = new SurvivalTimer(currentConfig.getDuration());
        this.spawnManager = new SpawnManager(currentConfig, player);
        this.portal = new Portal(PORTAL_X, PORTAL_Y);
        this.portalSpawned = false;
        this.enemiesDefeated = 0;
        
        System.out.println("Initialized: " + currentConfig.toString());
    }

    public void startLevel() {
        timer.start();
        spawnManager.resumeSpawning();
        gameState = GameState.PLAYING;
        System.out.println("Level " + currentLevelNumber + " started!");
    }

    public void update() {
        if (gameState != GameState.PLAYING) return;

        timer.update();
        spawnManager.update();
        portal.update();

        // Check if timer completed
        if (timer.isComplete() && !portalSpawned) {
            onTimerComplete();
        }
    }

    private void onTimerComplete() {
        System.out.println("Timer complete! Stopping spawns and activating portal...");
        spawnManager.stopSpawning();
        portal.activate();
        portalSpawned = true;
    }

//    public void checkPortalEntry() {
//        if (portalSpawned && portal.checkPlayerCollision(player)) {
//            onLevelComplete();
//        }
//    }

    private void onLevelComplete() {
        System.out.println("Level " + currentLevelNumber + " complete!");
        gameState = GameState.LEVEL_COMPLETE;
    }

    public void nextLevel() {
        if (currentLevelNumber < 3) {
            currentLevelNumber++;
            initializeLevel(currentLevelNumber, player);
            startLevel();
        } else {
            // Handle game completion or loop back to level 1
            System.out.println("All levels completed! Restarting game.");
            restartGame();
        }
    }

//    public void restartLevel() {
//        initializeLevel(currentLevelNumber, player);
//        startLevel();
//    }

    public void restartGame() {
        currentLevelNumber = 1;
        enemiesDefeated = 0;
        initializeLevel(1, player);
        startLevel();
    }

    public void onEnemyDefeated() {
        enemiesDefeated++;
    }

    // Getters
    public int getCurrentLevelNumber() { return currentLevelNumber; }
    public LevelConfig getCurrentConfig() { return currentConfig; }
    public SurvivalTimer getTimer() { return timer; }
    public SpawnManager getSpawnManager() { return spawnManager; }
    public Portal getPortal() { return portal; }
    public GameState getGameState() { return gameState; }
    public boolean isPortalSpawned() { return portalSpawned; }
//    public int getEnemiesDefeated() { return enemiesDefeated; }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

//    public void pauseGame() {
//        if (gameState == GameState.PLAYING) {
//            timer.pause();
//            gameState = GameState.PAUSED;
//        }
//    }

//    public void resumeGame() {
//        if (gameState == GameState.PAUSED) {
//            timer.resume();
//            gameState = GameState.PLAYING;
//        }
//    }
}
