package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Paint;
import java.awt.image.BufferedImage;

public class MainMenu {
    private int selectedOption;
    private final int OPTION_START = 0;
    private final int OPTION_STORY = 1;
    private final int OPTION_LEVELS = 2;
    private final int OPTION_SFX = 3;
    private final int OPTION_MUSIC = 4;
    private final int OPTION_EXIT = 5;
    private final int TOTAL_OPTIONS = 6;
    private int totalDefeated;
    private int animationTick;
    private float glowIntensity;
    // Icons and fonts
    private BufferedImage iconStart, iconStory, iconSfx, iconMusic, iconExit, iconArrowLeft, iconArrowRight, iconStar;
    private Font uiFontBold, uiFont, titleFont;

    public MainMenu() {
        this.selectedOption = OPTION_START;
        this.totalDefeated = utils.SaveManager.loadTotalEnemiesDefeated();
        this.animationTick = 0;
        this.glowIntensity = 0.5f;
        loadAssets();
    }
    public void update() {
        animationTick++;
        glowIntensity = 0.5f + 0.3f * (float)Math.sin(animationTick * 0.05);
    }
    public void render(Graphics g, int screenWidth, int screenHeight) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Gradient background for subtle depth
        GradientPaint bg = new GradientPaint(0, 0, new Color(15, 20, 40), 0, screenHeight, new Color(5, 5, 15));
        g2d.setPaint(bg);
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        // Starfield
        for (int i = 0; i < 120; i++) {
            int x = (i * 73 + animationTick * 2) % (screenWidth + 100) - 50;
            int y = (i * 137) % (screenHeight + 50) - 25;
            int size = 1 + (i % 3);
            int alpha = 100 + (i % 80);
            g2d.setColor(new Color(200, 220, 255, Math.min(220, alpha)));
            g2d.fillOval(x, y, size, size);
        }
        g.setFont(titleFont != null ? titleFont : new Font("Arial", Font.BOLD, 72));
        g.setColor(new Color(100, 200, 255));
    String title = "DATA KEEPER";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (screenWidth - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 200);
        g.setFont(uiFont != null ? uiFont.deriveFont(Font.PLAIN, 24f) : new Font("Arial", Font.PLAIN, 24));
        g.setColor(new Color(150, 150, 200));
    String subtitle = "Schütze das System";
        fm = g.getFontMetrics();
        int subtitleX = (screenWidth - fm.stringWidth(subtitle)) / 2;
        g.drawString(subtitle, subtitleX, 250);
        g.setFont(uiFontBold != null ? uiFontBold.deriveFont(Font.BOLD, 32f) : new Font("Arial", Font.BOLD, 32));
        g.setColor(new Color(255, 215, 0));
        String highScore = "Besiegte Gegner gesamt: " + totalDefeated;
        fm = g.getFontMetrics();
        int textW = fm.stringWidth(highScore);
        int iconW = (iconStar != null ? 28 : 0);
        int gap = (iconW > 0 ? 10 : 0);
        int combinedW = iconW + gap + textW;
        int scoreX = (screenWidth - combinedW) / 2;
        int scoreY = 350;
        if (iconStar != null) {
            g.drawImage(iconStar, scoreX, scoreY - 24, 28, 28, null);
        }
        g.drawString(highScore, scoreX + iconW + gap, scoreY);
        int menuY = 500;
        int spacing = 80;
    drawOption(g, iconStart, "SPIEL STARTEN", screenWidth / 2, menuY, selectedOption == OPTION_START);
    drawOption(g, iconStory, "GESCHICHTE", screenWidth / 2, menuY + spacing, selectedOption == OPTION_STORY);
    drawOption(g, iconStart, "LEVELAUSWAHL", screenWidth / 2, menuY + spacing * 2, selectedOption == OPTION_LEVELS);
    audio.SoundManager sm = audio.SoundManager.getInstance();
    String sfxLabel = (sm.isSoundEnabled() ? "SFX: EIN " : "SFX: AUS ") + slider(sm.getSfxVolume());
    String musicLabel = (sm.isSoundEnabled() ? "MUSIC: EIN " : "MUSIC: AUS ") + slider(sm.getMusicVolume());
    drawOption(g, iconSfx, sfxLabel, screenWidth / 2, menuY + spacing * 3, selectedOption == OPTION_SFX);
    drawOption(g, iconMusic, musicLabel, screenWidth / 2, menuY + spacing * 4, selectedOption == OPTION_MUSIC);
    drawOption(g, iconExit, "BEENDEN", screenWidth / 2, menuY + spacing * 5, selectedOption == OPTION_EXIT);

        // Controls hint
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(new Color(150, 150, 150));
    String hint = "Nutze W/S zum Navigieren, ENTER zur Auswahl";
        fm = g.getFontMetrics();
        int hintX = (screenWidth - fm.stringWidth(hint)) / 2;
        g.drawString(hint, hintX, screenHeight - 50);

        // If story is selected, show overlay with German story text
        if (selectedOption == OPTION_STORY) {
            drawStoryOverlay(g, screenWidth, screenHeight);
        } else if (selectedOption == OPTION_LEVELS) {
            drawLevelSelectOverlay(g, screenWidth, screenHeight);
        }
    }

    private void drawStoryOverlay(Graphics g, int w, int h) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0,0,0,180));
        g2d.fillRect(0,0,w,h);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        String[] lines = new String[]{
            "Bit ist ein kleines digitales Wesen, das in einem Computer lebt.",
            "Eines Tages wird das System von Glitches (Viren) angegriffen und beginnt zu zerfallen.",
            "Bits Aufgabe ist es, das System während eines begrenzten Eindringzeitfensters zu schützen."
        };
        int y = h/2 - lines.length * 20;
        for (String line : lines) {
            FontMetrics fm = g.getFontMetrics();
            int x = (w - fm.stringWidth(line)) / 2;
            g2d.drawString(line, x, y);
            y += 40;
        }
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        String esc = "ESC zum Schließen";
        FontMetrics fm2 = g.getFontMetrics();
        int x2 = (w - fm2.stringWidth(esc)) / 2;
        g2d.drawString(esc, x2, h - 60);
    }

    private void drawOption(Graphics g, BufferedImage icon, String text, int centerX, int y, boolean selected) {
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int iconWidth = (icon != null ? 28 : 0);
        int gap = (icon != null ? 10 : 0);
        int combinedWidth = iconWidth + gap + textWidth;
        int baseX = centerX - combinedWidth / 2;
        int textX = baseX + iconWidth + gap;

        if (selected) {
            int glowAlpha = (int)(100 * glowIntensity);
            // Soft glow + gradient button
            g2d.setColor(new Color(100, 200, 255, glowAlpha));
            g2d.fillRoundRect(baseX - 30, y - 40, combinedWidth + 60, 60, 20, 20);
            GradientPaint gp = new GradientPaint(baseX - 30, y - 40, new Color(50, 120, 200, 220), baseX - 30, y + 20, new Color(80, 180, 240, 220));
            Paint old = g2d.getPaint();
            g2d.setPaint(gp);
            g2d.fillRoundRect(baseX - 30, y - 40, combinedWidth + 60, 60, 20, 20);
            g2d.setPaint(old);
            g.setFont(uiFontBold != null ? uiFontBold.deriveFont(Font.BOLD, 36f) : new Font("Arial", Font.BOLD, 36));
            g.setColor(new Color(255, 255, 255));
            
            if (iconArrowLeft != null && iconArrowRight != null) {
                g.drawImage(iconArrowLeft, baseX - 60, y - 28, 24, 24, null);
                g.drawImage(iconArrowRight, baseX + combinedWidth + 36, y - 28, 24, 24, null);
            } else {
                g.drawString(">", baseX - 50, y);
                g.drawString("<", baseX + combinedWidth + 30, y);
            }
        } else {
            g.setFont(uiFont != null ? uiFont.deriveFont(Font.PLAIN, 32f) : new Font("Arial", Font.PLAIN, 32));
            g.setColor(new Color(180, 180, 180));
        }

        if (icon != null) {
            g.drawImage(icon, baseX, y - 24, 28, 28, null);
        }
        g.drawString(text, textX, y);
    }

    private String slider(float v) {
        int filled = Math.round(v * 10);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) sb.append(i < filled ? '#' : '-');
        sb.append(']');
        return sb.toString();
    }
    public void moveUp() {
        selectedOption--;
        if (selectedOption < 0) {
            selectedOption = TOTAL_OPTIONS - 1;
        }
    }

    public void moveDown() {
        selectedOption++;
        if (selectedOption >= TOTAL_OPTIONS) {
            selectedOption = 0;
        }
    }
    public boolean isStartSelected() {
        return selectedOption == OPTION_START;
    }

    public boolean isStorySelected() {
        return selectedOption == OPTION_STORY;
    }
    public boolean isLevelSelectSelected() { return selectedOption == OPTION_LEVELS; }
    public boolean isSfxSelected() { return selectedOption == OPTION_SFX; }
    public boolean isMusicSelected() { return selectedOption == OPTION_MUSIC; }

    public boolean isExitSelected() {
        return selectedOption == OPTION_EXIT;
    }

    public void refreshStats() {
        this.totalDefeated = utils.SaveManager.loadTotalEnemiesDefeated();
    }

    public void onNavigate() {
        audio.SoundManager.getInstance().playMenuMoveSound();
    }

    public void onSelect() {
        audio.SoundManager.getInstance().playMenuSelectSound();
    }

    public void toggleAudio() {
        audio.SoundManager sm = audio.SoundManager.getInstance();
        sm.setSoundEnabled(!sm.isSoundEnabled());
    }

    // Level select state
    private int levelSelectIndex = 0; // 0..3 -> levels 1..4
    private void drawLevelSelectOverlay(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0,0,0,200));
        g2.fillRect(0,0,w,h);
        g2.setColor(Color.WHITE);
        g2.setFont(uiFontBold != null ? uiFontBold.deriveFont(Font.BOLD, 28f) : new Font("Arial", Font.BOLD, 28));
        String title = "Levelauswahl";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (w - fm.stringWidth(title))/2, 180);
        g2.setFont(uiFont != null ? uiFont.deriveFont(Font.PLAIN, 22f) : new Font("Arial", Font.PLAIN, 22));
    String[] options = new String[]{"RAM", "CPU", "Festplattenlevel", "Kernel (Boss)"};
        int bx = w/2 - 260, by = 240, bw = 520, bh = 80;
        // arrows
        int arrowY = by + bh/2 - 12;
        if (iconArrowLeft != null) g2.drawImage(iconArrowLeft, bx - 40, arrowY, 24, 24, null);
        if (iconArrowRight != null) g2.drawImage(iconArrowRight, bx + bw + 16, arrowY, 24, 24, null);
        // selection box
        g2.setColor(new Color(50, 120, 200, 180));
        g2.fillRoundRect(bx, by, bw, bh, 16, 16);
        g2.setColor(Color.WHITE);
        String selected = options[levelSelectIndex];
        g2.drawString(selected, bx + 20, by + 50);
        // preview box
        int px = bx, py = by + bh + 20, pw = bw, ph = 140;
        g2.setColor(new Color(20, 40, 80, 160));
        g2.fillRoundRect(px, py, pw, ph, 16, 16);
        g2.setColor(new Color(180, 220, 255));
        String preview = switch (levelSelectIndex) {
            case 0 -> "Dauer 60s • Spawn langsam";
            case 1 -> "Dauer 75s • Spawn schneller";
            case 2 -> "Dauer 90s • Spawn sehr schnell";
            default -> "BOSS • Angriffe werden telegraphiert";
        };
        g2.drawString(preview, px + 20, py + 40);
        g2.setFont(uiFont != null ? uiFont.deriveFont(Font.PLAIN, 16f) : new Font("Arial", Font.PLAIN, 16));
        String hint = "Links/Rechts zur Auswahl • ENTER starten • ESC schließen";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(hint, (w - fm2.stringWidth(hint))/2, py + ph + 40);
    }

    private void loadAssets() {
        // Load icons
        iconStart = utils.ImageUtils.loadImage("/ui/icons/start.png", getClass());
        iconStory = utils.ImageUtils.loadImage("/ui/icons/story.png", getClass());
        iconSfx = utils.ImageUtils.loadImage("/ui/icons/sfx.png", getClass());
        iconMusic = utils.ImageUtils.loadImage("/ui/icons/music.png", getClass());
        iconExit = utils.ImageUtils.loadImage("/ui/icons/exit.png", getClass());
        iconArrowLeft = utils.ImageUtils.loadImage("/ui/icons/left.png", getClass());
        iconArrowRight = utils.ImageUtils.loadImage("/ui/icons/right.png", getClass());
        iconStar = utils.ImageUtils.loadImage("/ui/icons/star.png", getClass());

        // Load fonts
        try {
            java.io.File fontFile = new java.io.File("res/fonts/DejaVuSans.ttf");
            if (!fontFile.exists()) fontFile = new java.io.File("res/fonts/Roboto-Regular.ttf");
            if (fontFile.exists()) {
                java.io.FileInputStream fis = new java.io.FileInputStream(fontFile);
                Font base = Font.createFont(Font.TRUETYPE_FONT, fis);
                uiFont = base.deriveFont(Font.PLAIN, 28f);
                uiFontBold = base.deriveFont(Font.BOLD, 28f);
                titleFont = base.deriveFont(Font.BOLD, 64f);
            } else {
                // Try system fonts
                uiFont = new Font("DejaVu Sans", Font.PLAIN, 28);
                uiFontBold = new Font("DejaVu Sans", Font.BOLD, 28);
                titleFont = new Font("DejaVu Sans", Font.BOLD, 64);
            }
        } catch (Exception ignore) {}
    }
}
