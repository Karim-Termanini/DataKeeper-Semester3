package main;

import entities.Boss;
import entities.Enemy;
import entities.GameCharacter;
import entities.Player;
import gameplay.GameState;
import inputs.KeyboardInputs;
import levels.LevelConfig;
import levels.LevelManager;
import levels.Portal;
import ui.EpilogueScreen;
import ui.GameOverScreen;
import ui.HUD;
import ui.LevelBackgroundRenderer;
import ui.LevelCompleteScreen;
import ui.MainMenu;
import ui.VictoryScreen;
import utils.Constants; // Import constants

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
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
    private int shakeFrames = 0;
    private int shakeMagnitude = 0;
    private final int LEVEL_WIDTH = utils.Constants.LEVEL_WIDTH;
    private final int LEVEL_HEIGHT = utils.Constants.LEVEL_HEIGHT;

    private final LevelManager levelManager;
    private HUD hud;
    private final LevelCompleteScreen levelCompleteScreen;
    private final GameOverScreen gameOverScreen;
    private final EpilogueScreen epilogueScreen;
    private final LevelBackgroundRenderer backgroundRenderer;
    private final MainMenu mainMenu;
    private final audio.SoundManager soundManager;
    private final VictoryScreen victoryScreen;

    private int totalEnemiesDefeated;
    private boolean portalInteractionReady;
    private int combatIntensityFrames = 0; // counts down to keep combat layer active briefly
    private entities.Boss currentBoss = null;
    // Simple hitspark effects
    private static class Spark { int x,y,frames; }
    // Accessed by game loop thread (updates) and EDT (render). Use synchronizedList and lock on it during iteration.
    private final java.util.List<Spark> sparks = java.util.Collections.synchronizedList(new java.util.ArrayList<>());

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
    backgroundRenderer = new LevelBackgroundRenderer();
    epilogueScreen = new EpilogueScreen();
    victoryScreen = new VictoryScreen();
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
            case VICTORY -> victoryScreen.update();
            case EPILOGUE -> epilogueScreen.update();
        }
    }
    private void updatePlaying() {
        player.update();
        // Give spawn manager the current active enemies to enforce cap
        levelManager.getSpawnManager().setActiveEnemyCount(enemies.size());
        levelManager.update();
        syncEnemies();
        // Update all enemies to keep behavior consistent even off-screen
        for (Enemy enemy : enemies) {
            enemy.update();
        }
        updateCamera();
        checkCollisions();
        checkCombat();
        checkEnemyAttacks();
        removeDeadAndOffscreenEnemies();
    hud.update(enemies.size());
    hud.setSessionDefeated(totalEnemiesDefeated);
        // Simple dynamic music intensity: turn on combat layer if many enemies or recent hits
        if (enemies.size() >= 6) combatIntensityFrames = Math.max(combatIntensityFrames, 180);
        if (combatIntensityFrames > 0) {
            combatIntensityFrames--;
            soundManager.setCombatLayerActive(true);
        } else {
            soundManager.setCombatLayerActive(false);
        }
        if (levelManager.isPortalSpawned()) {
            Portal portal = levelManager.getPortal();
            portalInteractionReady = portal.checkPlayerCollision(player);
        }
        if (!player.isAlive()) {
            onGameOver();
        }
    }

    private void syncEnemies() {
        List<GameCharacter> spawnedEnemies = levelManager.getSpawnManager().drainSpawnedEnemies();
        for (GameCharacter gc : spawnedEnemies) {
            if (gc instanceof Enemy enemy) {
                if (!characters.contains(enemy)) {
                    characters.add(enemy);
                    enemies.add(enemy);
                    if (enemy instanceof Boss b) currentBoss = b;
                }
            }
        }
    }
    public void updateCamera() {
        int targetCameraX = player.getX() - Constants.CAMERA_OFFSET_X;
        int targetCameraY = player.getY() - Constants.CAMERA_OFFSET_Y;
        if (utils.Constants.ENABLE_SMOOTH_CAMERA) {
            cameraX += (int)((targetCameraX - cameraX) * utils.Constants.CAMERA_SMOOTHING);
            cameraY += (int)((targetCameraY - cameraY) * utils.Constants.CAMERA_SMOOTHING);
        } else {
            cameraX = targetCameraX;
            cameraY = targetCameraY;
        }
        cameraX = Math.max(0, Math.min(cameraX, LEVEL_WIDTH - getWidth()));
        cameraY = Math.max(0, Math.min(cameraY, LEVEL_HEIGHT - getHeight()));
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

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                boolean wasBoss = (enemy instanceof entities.Boss);
                utils.EnemyPool.getInstance().returnEnemy(enemy);
                toRemove.add(enemy);
                totalEnemiesDefeated++;
                levelManager.onEnemyDefeated();
                utils.SaveManager.addEnemiesDefeated(1);
                // Ensure enemy death sound is always heard even if missed inside Enemy logic
                audio.SoundManager.getInstance().playSound("enemy_death");
                audio.SoundManager.getInstance().duckMusic(500);
                combatIntensityFrames = Math.max(combatIntensityFrames, 120);

                // If a boss died, immediate victory and transition
                if (wasBoss && levelManager.getCurrentConfig().isBossLevel()) {
                    soundManager.setCombatLayerActive(false);
                    levelManager.setGameState(gameplay.GameState.VICTORY);
                }
            }
        }

        if (!toRemove.isEmpty()) {
            enemies.removeAll(toRemove);
            characters.removeAll(toRemove);
        }
    }
    public void checkCombat() {
        if (player.isAttacking()) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && player.isAttackingEnemy(enemy)) {
                    if (player.canApplyDamageTo(enemy)) {
                        int dmg = player.getAttackDamage();
                        // Reduce damage vs boss to avoid 3-combo kill
                        if (enemy instanceof entities.Boss) {
                            dmg = (int)Math.ceil(dmg * 0.50);
                        }
                        enemy.takeDamage(dmg);
                        audio.SoundManager.getInstance().duckMusic(250);
                        combatIntensityFrames = Math.max(combatIntensityFrames, 90);
                        // spawn spark at enemy hitbox center
                        Rectangle hb = enemy.getHitbox();
                        Spark s = new Spark();
                        s.x = hb.x + hb.width/2; s.y = hb.y + hb.height/2; s.frames = 18;
                        sparks.add(s);
                        // Count combo on actual damage, not just swing
                        player.onSuccessfulHit();
                    }
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
            mainMenu.refreshStats();
            soundManager.stopBackgroundMusic();
            soundManager.playMenuMusic();
            mainMenu.render(g, getWidth(), getHeight());
            return;
        }
        backgroundRenderer.setLevel(levelManager.getCurrentLevelNumber());
        backgroundRenderer.render(g, LEVEL_WIDTH, LEVEL_HEIGHT, cameraX, cameraY);
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();
        int shakeX = 0, shakeY = 0;
        if (utils.Constants.ENABLE_SCREEN_SHAKE && shakeFrames > 0) {
            shakeFrames--;
            shakeX = (int)((Math.random() - 0.5) * 2 * shakeMagnitude);
            shakeY = (int)((Math.random() - 0.5) * 2 * shakeMagnitude);
        }
        g2d.translate(-cameraX + shakeX, -cameraY + shakeY);
        for (int i = 0; i < characters.size(); i++) {
            // We must check size on each iteration, in case a character is removed concurrently.
            if (i < characters.size()) {
                characters.get(i).render(g);
            }
        }
        if (levelManager.isPortalSpawned()) {
            levelManager.getPortal().render(g);
        }
        g2d.setTransform(originalTransform);
        switch (state) {
            case MAIN_MENU -> mainMenu.render(g, getWidth(), getHeight());
            case PLAYING -> {
                hud.render(g);
                drawControlsOverlay((Graphics2D) g);
                drawSparks((Graphics2D) g);
                drawBossHealth((Graphics2D) g);
            }
            case LEVEL_COMPLETE -> levelCompleteScreen.render(g, getWidth(), getHeight());
            case GAME_OVER -> gameOverScreen.render(g, getWidth(), getHeight());
            case VICTORY -> victoryScreen.render(g, getWidth(), getHeight());
            case EPILOGUE -> epilogueScreen.render(g, getWidth(), getHeight());
        }
    }

    private void drawBossHealth(Graphics2D g2) {
        if (levelManager.getCurrentConfig().isBossLevel() && currentBoss != null && currentBoss.isAlive()) {
            int w = getWidth() - 300;
            int x = 150;
            int y = 100; // lowered so it doesn't overlap player HUD
            int max = (currentBoss != null) ? currentBoss.getMaxHealth() : 1200; // reflect actual boss max
            int hp = currentBoss.getHealth();
            int fill = Math.max(0, Math.min(w, (int)(w * (hp / (float) max))));
            // Background
            g2.setColor(new Color(0,0,0,160));
            g2.fillRoundRect(x, y, w, 24, 12, 12);
            // Fill
            g2.setColor(new Color(220, 60, 80));
            g2.fillRoundRect(x, y, fill, 24, 12, 12);
            // Border
            g2.setColor(new Color(255, 220, 220));
            g2.drawRoundRect(x, y, w, 24, 12, 12);
            // Label
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(Color.WHITE);
            String label = "BOSS";
            g2.drawString(label, x + w/2 - g2.getFontMetrics().stringWidth(label)/2, y - 6);
        }
    }

    private void drawSparks(Graphics2D g2) {
        if (sparks.isEmpty()) return;
        java.util.List<Spark> expired = new java.util.ArrayList<>();
        // Synchronize during iteration to avoid CME when update thread adds sparks concurrently
        synchronized (sparks) {
            for (Spark s : sparks) {
                s.frames--;
                if (s.frames <= 0) { expired.add(s); continue; }
                float t = s.frames / 18f;
                int r = (int)(36 * (1.2 - t));
                g2.setColor(new Color(255, (int)(220*t), 120, (int)(220*t)));
                g2.fillOval(s.x - r/2 - cameraX, s.y - r/2 - cameraY, r, r);
                g2.setColor(new Color(255, 255, 220, (int)(180*t)));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(s.x - r/2 - cameraX, s.y - r/2 - cameraY, r, r);
            }
            if (!expired.isEmpty()) sparks.removeAll(expired);
        }
    }
    private void onGameOver() {
        levelManager.setGameState(GameState.GAME_OVER);
        gameOverScreen.show(levelManager.getCurrentLevelNumber(), totalEnemiesDefeated);
    }
    private void onLevelComplete() {
        soundManager.playSound("portal");
        if (levelManager.getCurrentLevelNumber() >= 3) {
            levelManager.setGameState(GameState.EPILOGUE);
        } else {
            levelManager.setGameState(GameState.LEVEL_COMPLETE);
            levelCompleteScreen.show(
                levelManager.getCurrentLevelNumber(),
                levelManager.getCurrentConfig().getDuration(),
                totalEnemiesDefeated
            );
        }
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
            case EPILOGUE -> {
                if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_ESCAPE) returnToMainMenu();
            }
            case VICTORY -> {
                if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_ESCAPE) returnToMainMenu();
            }
        }
    }
    private void handleMainMenuKeys(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> { mainMenu.moveUp(); mainMenu.onNavigate(); }
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> { mainMenu.moveDown(); mainMenu.onNavigate(); }
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> {
                audio.SoundManager sm = audio.SoundManager.getInstance();
                if (mainMenu.isLevelSelectSelected()) { // move level selection left
                    mainMenu.onNavigate();
                    try { java.lang.reflect.Field f = ui.MainMenu.class.getDeclaredField("levelSelectIndex"); f.setAccessible(true); int i = (int) f.get(mainMenu); i = (i + 3) % 4; f.set(mainMenu, i); } catch (Exception ignore) {}
                }
                else if (mainMenu.isSfxSelected()) { sm.setSfxVolume(sm.getSfxVolume() - 0.1f); mainMenu.onNavigate(); }
                else if (mainMenu.isMusicSelected()) { sm.setMusicVolume(sm.getMusicVolume() - 0.1f); mainMenu.onNavigate(); }
            }
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> {
                audio.SoundManager sm = audio.SoundManager.getInstance();
                if (mainMenu.isLevelSelectSelected()) { // move level selection right
                    mainMenu.onNavigate();
                    try { java.lang.reflect.Field f = ui.MainMenu.class.getDeclaredField("levelSelectIndex"); f.setAccessible(true); int i = (int) f.get(mainMenu); i = (i + 1) % 4; f.set(mainMenu, i); } catch (Exception ignore) {}
                }
                else if (mainMenu.isSfxSelected()) { sm.setSfxVolume(sm.getSfxVolume() + 0.1f); mainMenu.onNavigate(); }
                else if (mainMenu.isMusicSelected()) { sm.setMusicVolume(sm.getMusicVolume() + 0.1f); mainMenu.onNavigate(); }
            }
            case KeyEvent.VK_1, KeyEvent.VK_NUMPAD1 -> { if (mainMenu.isLevelSelectSelected()) startLevelDirect(1); }
            case KeyEvent.VK_2, KeyEvent.VK_NUMPAD2 -> { if (mainMenu.isLevelSelectSelected()) startLevelDirect(2); }
            case KeyEvent.VK_3, KeyEvent.VK_NUMPAD3 -> { if (mainMenu.isLevelSelectSelected()) startLevelDirect(3); }
            case KeyEvent.VK_4, KeyEvent.VK_NUMPAD4 -> { if (mainMenu.isLevelSelectSelected()) startLevelDirect(4); }
            case KeyEvent.VK_ENTER -> {
                if (mainMenu.isStartSelected()) { mainMenu.onSelect(); startGameFromMenu(); }
                else if (mainMenu.isStorySelected()) {
                    mainMenu.onSelect();
                    // Do nothing on ENTER; overlay already visible. ESC to close.
                }
                else if (mainMenu.isLevelSelectSelected()) {
                    // Start currently selected level via reflection (avoid expanding MainMenu API)
                    try { java.lang.reflect.Field f = ui.MainMenu.class.getDeclaredField("levelSelectIndex"); f.setAccessible(true); int i = (int) f.get(mainMenu); startLevelDirect(i+1); } catch (Exception ignore) {}
                }
                else if (mainMenu.isSfxSelected() || mainMenu.isMusicSelected()) { mainMenu.onSelect(); mainMenu.toggleAudio(); }
                else if (mainMenu.isExitSelected()) { mainMenu.onSelect(); System.exit(0); }
            }
            case KeyEvent.VK_ESCAPE -> {
                // Escape simply closes the story overlay by moving selection off it
                // If story is selected, move selection to START
                // Otherwise, no-op
            }
        }
    }
    private void startLevelDirect(int level) {
        mainMenu.onSelect();
        enemies.clear();
        characters.clear();
        totalEnemiesDefeated = 0;
        player = new Player();
        characters.add(player);
        levelManager.setPlayer(player);
        levelManager.initializeLevel(level, player);
        levelManager.setGameState(GameState.PLAYING);
        levelManager.startLevel();
        hud = new HUD(player, levelManager.getTimer(), levelManager.getCurrentConfig(), level);
        portalInteractionReady = false;
        soundManager.stopMenuMusic();
        soundManager.stopBackgroundMusic();
        soundManager.stopBossMusic();
        if (levelManager.getCurrentConfig().isBossLevel()) soundManager.playBossMusic();
        else soundManager.playBackgroundMusic();
        soundManager.setCombatLayerActive(false);
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
        soundManager.stopMenuMusic();
        if (levelManager.getCurrentConfig().isBossLevel()) {
            soundManager.playBossMusic();
        } else {
            soundManager.playBackgroundMusic();
        }
        soundManager.setCombatLayerActive(false);
    }
    private void returnToMainMenu() {
        soundManager.stopBackgroundMusic();
        soundManager.setCombatLayerActive(false);
        levelManager.setGameState(GameState.MAIN_MENU);
        mainMenu.refreshStats();
        soundManager.playMenuMusic();
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
        // Switch music if entering/leaving boss level
        soundManager.stopBackgroundMusic();
        soundManager.stopBossMusic();
        if (levelManager.getCurrentConfig().isBossLevel()) {
            soundManager.playBossMusic();
        } else {
            soundManager.playBackgroundMusic();
        }
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

    private void drawControlsOverlay(Graphics2D g2) {
        int padding = 16;
        int margin = 12;
        int h = getHeight();

        String title = "Steuerung";
        String[] rows = new String[]{
            "A/D: Bewegen",
            "SPACE: Springen",
            "E: Angriff / Portal",
            "S: Rutschen",
            "SHIFT: Sprinten",
            "ESC: Zurück zum Menü"
        };

        Font titleFont = new Font("Arial", Font.BOLD, 18);
        Font itemFont = new Font("Arial", Font.PLAIN, 14);
        g2.setFont(titleFont);
        FontMetrics fmTitle = g2.getFontMetrics();
        int panelWidth = 0;
        panelWidth = Math.max(panelWidth, fmTitle.stringWidth(title));
        g2.setFont(itemFont);
        FontMetrics fmItem = g2.getFontMetrics();
        for (String row : rows) {
            panelWidth = Math.max(panelWidth, fmItem.stringWidth(row));
        }
        int panelHeight = fmTitle.getHeight() + rows.length * fmItem.getHeight() + padding * 2 + 6;
        int panelX = margin;
        int panelY = h - panelHeight - margin;
        int panelW = panelWidth + padding * 2;

        // Background with soft gradient
        Paint oldPaint = g2.getPaint();
        GradientPaint gp = new GradientPaint(panelX, panelY, new Color(0, 0, 0, 160), panelX, panelY + panelHeight, new Color(20, 20, 40, 160));
        g2.setPaint(gp);
        g2.fillRoundRect(panelX, panelY, panelW, panelHeight, 18, 18);
        g2.setPaint(oldPaint);
        g2.setColor(new Color(100, 200, 255, 180));
        g2.drawRoundRect(panelX, panelY, panelW, panelHeight, 18, 18);

        // Title
        g2.setFont(titleFont);
        g2.setColor(new Color(255, 215, 0));
        int textX = panelX + padding;
        int textY = panelY + padding + fmTitle.getAscent();
        g2.drawString(title, textX, textY);

        // Items with colored key labels
        g2.setFont(itemFont);
        int y = textY + 6;
        for (String row : rows) {
            y += fmItem.getHeight();
            int colon = row.indexOf(":");
            if (colon > 0) {
                String key = row.substring(0, colon);
                String desc = row.substring(colon + 1).trim();
                // Key chip
                String keyLabel = key;
                int keyW = fmItem.stringWidth(keyLabel) + 10;
                int keyH = fmItem.getAscent() + 6;
                int chipY = y - fmItem.getAscent() - 2;
                g2.setColor(new Color(100, 200, 255, 200));
                g2.fillRoundRect(textX, chipY, keyW, keyH, 10, 10);
                g2.setColor(Color.BLACK);
                g2.drawString(keyLabel, textX + 5, y);
                // Description
                g2.setColor(new Color(230, 230, 230));
                g2.drawString(desc, textX + keyW + 10, y);
            } else {
                g2.setColor(new Color(230, 230, 230));
                g2.drawString(row, textX, y);
            }
        }
    }
}
