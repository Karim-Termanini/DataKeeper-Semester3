package main;

import entities.*;
import gameplay.GameState;
import inputs.KeyboardInputs;
import levels.*;
import ui.*;
import utils.Constants; // Import constants

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

    private int cameraX = 0;
    private int cameraY = 0;
    private final int LEVEL_WIDTH = utils.Constants.LEVEL_WIDTH;
    private final int LEVEL_HEIGHT = utils.Constants.LEVEL_HEIGHT;

    private final LevelManager levelManager;
    private HUD hud;
    private final LevelCompleteScreen levelCompleteScreen;
    private final GameOverScreen gameOverScreen;
    private final ui.LevelBackgroundRenderer backgroundRenderer;
    private final ui.MainMenu mainMenu;
    private final audio.SoundManager soundManager;

    private int totalEnemiesDefeated;
    private boolean portalInteractionReady;

    public GamePanel() {
        soundManager = audio.SoundManager.getInstance();
        mainMenu = new ui.MainMenu();
        player = new Player();
        characters = new ArrayList<>();
        enemies = new ArrayList<>();
        characters.add(player);
        totalEnemiesDefeated = 0;
        portalInteractionReady = false;
        levelManager = LevelManager.getInstance();
        levelManager.initializeLevel(1, player);
        levelManager.setGameState(GameState.MAIN_MENU);
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
            case MAIN_MENU -> mainMenu.update();
            case PLAYING -> updatePlaying();
            case LEVEL_COMPLETE -> levelCompleteScreen.update();
            case GAME_OVER -> gameOverScreen.update();
        }
    }
    private void updatePlaying() {
        player.update();
        levelManager.update();
        syncEnemies();
        // The player is already in the characters list, updating it again is redundant.
        // We only need to update the enemies.
        for (Enemy enemy : enemies) {
            enemy.update();
        }
        updateCamera();
        checkCollisions();
        checkCombat();
        checkEnemyAttacks();
        removeDeadAndOffscreenEnemies();
        hud.update(enemies.size());
        if (levelManager.isPortalSpawned()) {
            Portal portal = levelManager.getPortal();
            portalInteractionReady = portal.checkPlayerCollision(player);
        }
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
    public void updateCamera() {
        int targetCameraX = player.getX() - Constants.CAMERA_OFFSET_X;
        int targetCameraY = player.getY() - Constants.CAMERA_OFFSET_Y;
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
        if (player.isAttacking()) return;
        if (player.getCurrentAction() == Constants.PlayerActions.SLIDE) {
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

            // RESTORED BUMP DAMAGE
            if (player.isAlive() && !player.hasJumpOverProtection() && !player.isAttackingProtected()) {
                player.takeDamage(enemy.getAttackDamage());
            }
        }
    }
    public void removeDeadAndOffscreenEnemies() {
        List<Enemy> toRemove = new ArrayList<>();
        List<Enemy> deadEnemies = new ArrayList<>();
        int playerX = player.getX();
        final int despawnDistance = 2000;

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                toRemove.add(enemy);
                deadEnemies.add(enemy); // Keep track of them for score/etc
            } else if (Math.abs(playerX - enemy.getX()) > despawnDistance) {
                toRemove.add(enemy);
            }
        }

        if (!toRemove.isEmpty()) {
            enemies.removeAll(toRemove);
            characters.removeAll(toRemove);
        }
        
        if (!deadEnemies.isEmpty()) {
            totalEnemiesDefeated += deadEnemies.size();
            for (int i = 0; i < deadEnemies.size(); i++) {
                levelManager.onEnemyDefeated();
            }
        }
    }
    public void checkCombat() {
        if (player.isAttacking()) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && player.isAttackingEnemy(enemy)) {
                    enemy.takeDamage(player.getAttackDamage());
                }
            }
        }
    }

    private void checkEnemyAttacks() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.isAttacking()) {
                if (enemy.getAttackHitbox().intersects(player.getHitbox())) {
                    player.takeDamage(enemy.getAttackDamage());
                }
            }
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameState state = levelManager.getGameState();
        if (state == GameState.MAIN_MENU) {
            mainMenu.render(g, getWidth(), getHeight());
            return;
        }
        backgroundRenderer.setLevel(levelManager.getCurrentLevelNumber());
        backgroundRenderer.render(g, LEVEL_WIDTH, LEVEL_HEIGHT, cameraX, cameraY);
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();
        g2d.translate(-cameraX, -cameraY);
        List<GameCharacter> charactersCopy = new ArrayList<>(characters);
        for (GameCharacter character : charactersCopy) {
            character.render(g);
        }
        if (levelManager.isPortalSpawned()) {
            levelManager.getPortal().render(g);
        }
        g2d.setTransform(originalTransform);
        switch (state) {
            case PLAYING -> {
                hud.render(g);
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.setColor(new Color(200, 200, 200));
                g.drawString("ESC - Return to Menu", 10, getHeight() - 10);
            }
            case LEVEL_COMPLETE -> levelCompleteScreen.render(g, getWidth(), getHeight());
            case GAME_OVER -> gameOverScreen.render(g, getWidth(), getHeight());
        }
    }
    private void onGameOver() {
        levelManager.setGameState(GameState.GAME_OVER);
        gameOverScreen.show(levelManager.getCurrentLevelNumber(), totalEnemiesDefeated);
    }
    private void onLevelComplete() {
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
        switch (state) {
            case MAIN_MENU -> handleMainMenuKeys(keyCode);
            case PLAYING -> handlePlayingKeys(keyCode);
            case LEVEL_COMPLETE -> {
                if (keyCode == KeyEvent.VK_ENTER) startNextLevel();
            }
            case GAME_OVER -> {
                if (keyCode == KeyEvent.VK_R) restartGame();
                else if (keyCode == KeyEvent.VK_ESCAPE) returnToMainMenu();
            }
        }
    }
    private void handleMainMenuKeys(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> mainMenu.moveUp();
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> mainMenu.moveDown();
            case KeyEvent.VK_ENTER -> {
                if (mainMenu.isStartSelected()) startGameFromMenu();
                else if (mainMenu.isExitSelected()) System.exit(0);
            }
        }
    }
    private void handlePlayingKeys(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W -> player.specialAttack();
            case KeyEvent.VK_E -> {
                if (portalInteractionReady) onLevelComplete();
                else {
                    player.attack();
                    soundManager.playSound("attack");
                }
            }
            case KeyEvent.VK_S -> player.slide();
            case KeyEvent.VK_A -> player.moveLeft();
            case KeyEvent.VK_D -> player.moveRight();
            case KeyEvent.VK_SPACE -> {
                player.jump();
                soundManager.playSound("jump");
            }
            case KeyEvent.VK_SHIFT -> player.dash();
            case KeyEvent.VK_ESCAPE -> returnToMainMenu();
        }
    }
    private void startGameFromMenu() {
        enemies.clear();
        characters.clear();
        totalEnemiesDefeated = 0;
        player = new Player();
        characters.add(player);
        levelManager.setPlayer(player);
        levelManager.restartGame();
        levelManager.setGameState(GameState.PLAYING);
        levelManager.startLevel();
        hud = new HUD(player, levelManager.getTimer(), levelManager.getCurrentConfig(), 1);
        portalInteractionReady = false;
        soundManager.playBackgroundMusic();
    }
    private void returnToMainMenu() {
        soundManager.stopBackgroundMusic();
        levelManager.setGameState(GameState.MAIN_MENU);
        mainMenu.updateHighestLevel(levelManager.getCurrentLevelNumber());
    }
    public void handleKeyReleased(int keyCode) {
        if (levelManager.getGameState() == GameState.PLAYING) {
            if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D) {
                player.stop();
            }
        }
    }
    private void startNextLevel() {
        enemies.clear();
        characters.clear();
        characters.add(player);
        player.setPosition(100, Constants.GROUND_LEVEL);
        player.heal(200);
        levelManager.nextLevel();
        hud.setTimer(levelManager.getTimer());
        hud.setConfig(levelManager.getCurrentConfig());
        hud.setCurrentLevel(levelManager.getCurrentLevelNumber());
        portalInteractionReady = false;
    }

    private void restartGame() {
        enemies.clear();
        characters.clear();
        totalEnemiesDefeated = 0;
        player = new Player();
        characters.add(player);
        levelManager.setPlayer(player);
        levelManager.restartGame();
        hud = new HUD(player, levelManager.getTimer(), levelManager.getCurrentConfig(), 1);
        portalInteractionReady = false;
    }
    public Player getPlayer() {
        return player;
    }
}
