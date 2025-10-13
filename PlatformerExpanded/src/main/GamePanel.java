package main;

import entities.*;
import gameplay.GameState;
import inputs.KeyboardInputs;
import levels.*;
import ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private Player player;
    private final List<GameCharacter> characters;
    private final List<Enemy> enemies;
    private final List<Projectile> allProjectiles;

    // Camera system
    private int cameraX = 0;
    private int cameraY = 0;
    private final int LEVEL_WIDTH = utils.Constants.LEVEL_WIDTH;
    private final int LEVEL_HEIGHT = utils.Constants.LEVEL_HEIGHT;

    // Level management
    private final LevelManager levelManager;
    private HUD hud;
    private final LevelCompleteScreen levelCompleteScreen;
    private final GameOverScreen gameOverScreen;
    private final ui.LevelBackgroundRenderer backgroundRenderer;
    private final ui.MainMenu mainMenu;
    private final audio.SoundManager soundManager;

    // Game state
    private int totalEnemiesDefeated;
    private boolean portalInteractionReady;

    public GamePanel() {
        // Initialize sound first
        soundManager = audio.SoundManager.getInstance();

        // Initialize main menu
        mainMenu = new ui.MainMenu();

        player = new Player();
        characters = new ArrayList<>();
        enemies = new ArrayList<>();
        allProjectiles = new ArrayList<>();
        characters.add(player);

        totalEnemiesDefeated = 0;
        portalInteractionReady = false;

        // Initialize level system
        levelManager = LevelManager.getInstance();
        levelManager.initializeLevel(1, player);
        levelManager.setGameState(GameState.MAIN_MENU); // Start at main menu

        // Initialize UI
        LevelConfig config = levelManager.getCurrentConfig();
        hud = new HUD(player, levelManager.getTimer(), config, 1);
        levelCompleteScreen = new LevelCompleteScreen();
        gameOverScreen = new GameOverScreen();
        backgroundRenderer = new ui.LevelBackgroundRenderer();

        setupInputListeners();
        setPanelSize();
    }

    private void setupInputListeners() {
        addKeyListener(new KeyboardInputs(this));
        setFocusable(true);
    }

    private void setPanelSize() {
        Dimension size = new Dimension(1800, 1000);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    public void updateGame() {
        GameState state = levelManager.getGameState();

        switch (state) {
            case MAIN_MENU:
                mainMenu.update();
                break;
            case PLAYING:
                updatePlaying();
                break;
            case LEVEL_COMPLETE:
                levelCompleteScreen.update();
                break;
            case GAME_OVER:
                gameOverScreen.update();
                break;
            default:
                break;
        }
    }

    private void updatePlaying() {
        // Update player
        player.update();

        // Update level manager
        levelManager.update();

        // Sync enemies from spawn manager
        syncEnemies();

        // Update all characters
        for (GameCharacter character : characters) {
            character.update();
        }

        // Update projectiles from shooter enemies
        updateProjectiles();

        // Update camera
        updateCamera();

        // Check collisions
        checkCollisions();
        checkCombat();
        checkProjectileCollisions();

        // Remove dead enemies
        removeDeadEnemies();

        // Update HUD
        hud.update(enemies.size());

        // Check portal interaction
        if (levelManager.isPortalSpawned()) {
            Portal portal = levelManager.getPortal();
            portalInteractionReady = portal.checkPlayerCollision(player);
        }

        // Check game over
        if (!player.isAlive()) {
            onGameOver();
        }
    }

    private void syncEnemies() {
        List<GameCharacter> spawnedEnemies = levelManager.getSpawnManager().getSpawnedEnemies();

        for (GameCharacter gc : spawnedEnemies) {
            if (gc instanceof Enemy enemy) {
                if (!characters.contains(enemy)) {
                    characters.add(enemy);
                    enemies.add(enemy);
                }
            }
        }

    }

    private void updateProjectiles() {
        // No projectiles since we removed shooter enemies
        allProjectiles.clear();
    }

    private void checkProjectileCollisions() {
        // No projectile collisions needed
    }

    public void updateCamera() {
        final int CAMERA_OFFSET_X = utils.Constants.CAMERA_OFFSET_X;
        final int CAMERA_OFFSET_Y = utils.Constants.CAMERA_OFFSET_Y;
        int targetCameraX = player.getX() - CAMERA_OFFSET_X;
        int targetCameraY = player.getY() - CAMERA_OFFSET_Y;

        cameraX = Math.max(0, Math.min(targetCameraX, LEVEL_WIDTH - getWidth()));
        cameraY = Math.max(0, Math.min(targetCameraY, LEVEL_HEIGHT - getHeight()));

        if (LEVEL_WIDTH < getWidth()) {
            cameraX = (LEVEL_WIDTH - getWidth()) / 2;
        }
        if (LEVEL_HEIGHT < getHeight()) {
            cameraY = (LEVEL_HEIGHT - getHeight()) / 2;
        }
    }

    public void checkCollisions() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && player.isCollidingWith(enemy)) {
                handlePlayerEnemyCollision(enemy);
            }
        }
    }

    public void handlePlayerEnemyCollision(Enemy enemy) {
        if (player.isAttacking()) {
            return;
        }

        if (player.getCurrentAction() == Player.SLIDE) {
            enemy.takeDamage(player.getAttackDamage());
            return;
        }

        if (player.isAboveEnemy(enemy)) {
            player.activateJumpOverProtection();
            player.setPosition(player.getX(), player.getY() - 15);
        } else {
            if (player.getX() < enemy.getX()) {
                player.setPosition(player.getX() - 25, player.getY());
            } else {
                player.setPosition(player.getX() + 25, player.getY());
            }

            if (player.isAlive() && !player.hasJumpOverProtection() && !player.isAttackingProtected()) {
                player.takeDamage(enemy.getAttackDamage());
            }
        }
    }

    public void removeDeadEnemies() {
        List<Enemy> deadEnemies = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                deadEnemies.add(enemy);
            }
        }

        if (!deadEnemies.isEmpty()) {
            enemies.removeAll(deadEnemies);
            characters.removeAll(deadEnemies);
            for (int i = 0; i < deadEnemies.size(); i++) {
                totalEnemiesDefeated++;
                levelManager.onEnemyDefeated();
            }
        }
    }

    public void checkCombat() {
        if (player.isAttacking()) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && player.isAttackingEnemy(enemy)) {
                    int damage = player.getAttackDamage();
                    enemy.takeDamage(damage);
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameState state = levelManager.getGameState();

        if (state == GameState.MAIN_MENU) {
            // Render main menu
            mainMenu.render(g, getWidth(), getHeight());
            return;
        }

        // Draw level background with parallax
        backgroundRenderer.setLevel(levelManager.getCurrentLevelNumber());
        backgroundRenderer.render(g, LEVEL_WIDTH, LEVEL_HEIGHT, cameraX, cameraY);

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        // Apply camera transformation
        g2d.translate(-cameraX, -cameraY);

        // Draw all characters (use copy to avoid ConcurrentModificationException)
        List<GameCharacter> charactersCopy = new ArrayList<>(characters);
        for (GameCharacter character : charactersCopy) {
            character.render(g);
        }

        // Draw portal
        if (levelManager.isPortalSpawned()) {
            levelManager.getPortal().render(g);
        }

        // Restore original transform
        g2d.setTransform(originalTransform);

        // Draw UI (no camera effect)
        switch (state) {
            case PLAYING:
                hud.render(g);
                // Draw ESC hint
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.setColor(new Color(200, 200, 200));
                g.drawString("ESC - Return to Menu", 10, getHeight() - 10);
                break;
            case LEVEL_COMPLETE:
                levelCompleteScreen.render(g, getWidth(), getHeight());
                break;
            case GAME_OVER:
                gameOverScreen.render(g, getWidth(), getHeight());
                break;
            default:
                break;
        }
    }

    private void onGameOver() {
        levelManager.setGameState(GameState.GAME_OVER);
        gameOverScreen.show(levelManager.getCurrentLevelNumber(), totalEnemiesDefeated);
    }

    private void onLevelComplete() {
        // Play portal sound
        soundManager.playSound("portal");

        levelManager.setGameState(GameState.LEVEL_COMPLETE);
        levelCompleteScreen.show(
            levelManager.getCurrentLevelNumber(),
            levelManager.getCurrentConfig().getDuration(),
            totalEnemiesDefeated
        );
    }

    public void handleKeyPressed(int keyCode) {
        GameState state = levelManager.getGameState();

        if (state == GameState.MAIN_MENU) {
            handleMainMenuKeys(keyCode);
        } else if (state == GameState.PLAYING) {
            handlePlayingKeys(keyCode);
        } else if (state == GameState.LEVEL_COMPLETE) {
            if (keyCode == KeyEvent.VK_ENTER) {
                startNextLevel();
            }
        } else if (state == GameState.GAME_OVER) {
            if (keyCode == KeyEvent.VK_R) {
                restartGame();
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
                returnToMainMenu();
            }
        }
    }

    private void handleMainMenuKeys(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                mainMenu.moveUp();
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                mainMenu.moveDown();
                break;
            case KeyEvent.VK_ENTER:
                if (mainMenu.isStartSelected()) {
                    startGameFromMenu();
                } else if (mainMenu.isExitSelected()) {
                    System.exit(0);
                }
                break;
        }
    }

    private void handlePlayingKeys(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W:
                player.specialAttack();
                break;
            case KeyEvent.VK_E:
                if (portalInteractionReady) {
                    onLevelComplete();
                } else {
                    player.attack();
                    soundManager.playSound("attack");
                }
                break;
            case KeyEvent.VK_S:
                player.slide();
                break;
            case KeyEvent.VK_A:
                player.moveLeft();
                break;
            case KeyEvent.VK_D:
                player.moveRight();
                break;
            case KeyEvent.VK_SPACE:
                player.jump();
                soundManager.playSound("jump");
                break;
            case KeyEvent.VK_SHIFT:
                player.dash();
                break;
            case KeyEvent.VK_ESCAPE:
                returnToMainMenu();
                break;
        }
    }

    private void startGameFromMenu() {
        // Reset everything
        enemies.clear();
        characters.clear();
        allProjectiles.clear();
        totalEnemiesDefeated = 0;

        // Recreate player
        player = new Player();
        characters.add(player);

        // Start from level 1
        levelManager.restartGame();
        levelManager.setGameState(GameState.PLAYING);
        levelManager.startLevel();

        // Update HUD
        hud = new HUD(player, levelManager.getTimer(), levelManager.getCurrentConfig(), 1);

        portalInteractionReady = false;

        // Start music
        soundManager.playBackgroundMusic();
    }

    private void returnToMainMenu() {
        soundManager.stopBackgroundMusic();
        levelManager.setGameState(GameState.MAIN_MENU);

        // Update high score
        mainMenu.updateHighestLevel(levelManager.getCurrentLevelNumber());
    }

    public void handleKeyReleased(int keyCode) {
        if (levelManager.getGameState() == GameState.PLAYING) {
            switch (keyCode) {
                case KeyEvent.VK_A:
                case KeyEvent.VK_D:
                    player.stop();
                    break;
            }
        }
    }

    private void startNextLevel() {
        // Clear current enemies
        enemies.clear();
        characters.clear();
        characters.add(player);
        allProjectiles.clear();

        // Reset player position and health
        player.setPosition(100, 560);
        player.heal(200); // Full heal

        // Load next level
        levelManager.nextLevel();

        // Update HUD
        hud.setTimer(levelManager.getTimer());
        hud.setConfig(levelManager.getCurrentConfig());
        hud.setCurrentLevel(levelManager.getCurrentLevelNumber());

        portalInteractionReady = false;
    }

    private void restartGame() {
        // Reset everything
        enemies.clear();
        characters.clear();
        allProjectiles.clear();
        totalEnemiesDefeated = 0;

        // Recreate player
        player = new Player();
        characters.add(player);

        // Restart from level 1
        levelManager.restartGame();

        // Update HUD
        hud = new HUD(player, levelManager.getTimer(), levelManager.getCurrentConfig(), 1);

        portalInteractionReady = false;
    }

    public Player getPlayer() {
        return player;
    }
}
